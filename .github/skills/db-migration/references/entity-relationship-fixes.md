# Entity Relationship Fixes for students-report-alkarim

## Current State Analysis

Your domain entities have been mapped to the database, but there are several gaps between the JPA entity definitions and the actual database schema. This document outlines the issues and recommended fixes.

---

## Issue #1: Missing Foreign Key Constraint on RefreshToken

### Current Problem

```java
// RefreshToken.java
@Entity
public class RefreshToken extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    private Long userId;  // ⚠️ Bare Long, no @ManyToOne relationship!
    private Instant expireAt;
}
```

**Issue:** `userId` is stored as a plain `Long` field rather than a proper JPA relationship. The database has no foreign key constraint linking `refresh_tokens.user_id` to `users.id`.

### Impact

- ❌ Database integrity: Orphaned RefreshTokens can reference deleted users
- ❌ ORM behavior: Cannot use JPA lazy loading or cascading operations
- ❌ Query complexity: Must write raw SQL for relationships
- ❌ Referential integrity: No database-level enforcement

### Fix

**Step 1: Add Migration (V3_fix_refresh_token_fk.sql)**

```sql
-- V3_fix_refresh_token_fk.sql
-- Adds foreign key constraint to refresh_tokens.user_id

ALTER TABLE refresh_tokens 
  ADD CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Verify constraint was added
-- SELECT * FROM information_schema.table_constraints 
-- WHERE table_name = 'refresh_tokens' 
-- AND constraint_type = 'FOREIGN KEY';
```

**Step 2: Update RefreshToken Entity**

```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    // NEW: Proper JPA relationship instead of bare Long
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_refresh_tokens_user"))
    private User user;
    
    private Instant expireAt;
    
    // Old field for backwards compatibility (mark deprecated)
    @Deprecated
    @Transient
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}
```

**Step 3: Update Service Code**

Replace bare `userId` references:

```java
// OLD
RefreshToken token = new RefreshToken();
token.setUserId(user.getId());  // ❌ Bare ID

// NEW
RefreshToken token = new RefreshToken();
token.setUser(user);  // ✅ Full relationship
```

---

## Issue #2: Missing Bidirectional Relationships (No `@OneToMany`)

### Current Problem

All relationships are **unidirectional**. For example:

```java
// Report.java
@ManyToOne
@JoinColumn(name = "user_id")
private User user;  // ✅ Can navigate from Report → User

// User.java
// ❌ No way to navigate from User → Reports
// No @OneToMany List<Report> reports;
```

### Impact

- ❌ **Query inefficiency:** To find all reports by a user requires explicit query
  ```java
  // Inefficient: Must write separate query
  List<Report> reports = reportRepo.findByUserId(userId);
  
  // Better (if bidirectional):
  List<Report> reports = user.getReports();
  ```

- ❌ **Lazy loading problems:** Cannot use `@OneToMany(fetch = FetchType.LAZY)` for dynamic loading
- ❌ **Data loss risk:** Deleting parent doesn't cascade cleanly without explicit configuration

### Recommended Fixes

#### Fix 2A: User ↔ Report (Bidirectional)

**Entity Changes:**

```java
// User.java
@Entity
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ... existing fields ...
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_room_id")
    private ClassRoom classRoom;
    
    // NEW: Bidirectional relationship
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();
    
    // Convenience methods
    public void addReport(Report report) {
        reports.add(report);
        report.setUser(this);
    }
    
    public void removeReport(Report report) {
        reports.remove(report);
        report.setUser(null);
    }
}

// Report.java
@Entity
public class Report extends Auditable {
    // ... existing fields ...
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // Inverse side of User.reports
    
    // No changes needed here, it's the owning side
}
```

#### Fix 2B: ClassRoom ↔ User (Bidirectional)

```java
// ClassRoom.java
@Entity
public class ClassRoom extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private Integer studentCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;
    
    // NEW: Students in this classroom
    @OneToMany(mappedBy = "classRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> students = new ArrayList<>();
    
    public void addStudent(User student) {
        students.add(student);
        student.setClassRoom(this);
    }
}
```

#### Fix 2C: Category ↔ Question & Report (Bidirectional)

```java
// Category.java
@Entity
public class Category extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    // NEW: All questions in this category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();
    
    // NEW: All reports for this category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();
}
```

---

## Issue #3: Level Entity Incompleteness

### Current Problem

Based on the migrations, `Level` is used as a reference from `ClassRoom`, but the entity may lack proper implementation.

### Recommended Fix

```java
// Level.java
@Entity
@Table(name = "levels")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private LevelType levelType;  // I, II, III, ... XII
    
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Reverse relationship
    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL)
    private List<ClassRoom> classRooms = new ArrayList<>();
    
    // Constructors
    public Level() {}
    
    public Level(LevelType levelType) {
        this.levelType = levelType;
    }
    
    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LevelType getLevelType() { return levelType; }
    public void setLevelType(LevelType levelType) { this.levelType = levelType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

// LevelType.java
public enum LevelType {
    I, II, III, IV, V, VI, VII, VIII, IX, X, XI, XII
}
```

---

## Issue #4: Circular Dependency (User as Student & Teacher)

### Current Problem

The `User` entity serves dual roles:
- **Student:** `class_room_id` references the classroom the student is in
- **Teacher:** `class_rooms.teacher_id` references the teacher

This creates a design ambiguity:

```java
User user = userRepository.findById(1L);
// Is this user a student? A teacher? Both?
// How do we know their role?
```

### Recommendation

Use the existing `roles` field properly:

```java
// User.java
@Entity
public class User extends Auditable {
    // ...
    
    @Enumerated(EnumType.STRING)  // Store as VARCHAR(50)
    @Column(nullable = false)
    private UserRole roles;  // STUDENT, TEACHER, or ADMIN
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_room_id")
    private ClassRoom classRoom;  // Only for STUDENT role
    
    // Helper methods
    public boolean isStudent() {
        return UserRole.STUDENT.equals(roles);
    }
    
    public boolean isTeacher() {
        return UserRole.TEACHER.equals(roles);
    }
    
    // Ensure data consistency
    public void setAsStudent(ClassRoom classroom) {
        this.roles = UserRole.STUDENT;
        this.classRoom = classroom;
    }
    
    public void setAsTeacher() {
        this.roles = UserRole.TEACHER;
        this.classRoom = null;  // Teachers don't belong to a class_room
    }
}

// UserRole.java
public enum UserRole {
    STUDENT,
    TEACHER,
    ADMIN
}
```

---

## Migration Plan

### Phase 1: Add Constraints (Immediate)
- [ ] Create `V3_fix_refresh_token_fk.sql` — Add missing FK constraint
- [ ] Run migration: `./mvnw flyway:migrate`

### Phase 2: Update JPA Entities (Development)
- [ ] Update `RefreshToken` to use `@ManyToOne` instead of bare `Long`
- [ ] Add `@OneToMany` bidirectional mappings
- [ ] Complete `Level` entity implementation
- [ ] Update service code to use relationships instead of bare IDs

### Phase 3: Enable Flyway (Production)
- [ ] Uncomment Flyway config in `application.properties`
- [ ] Add `quarkus-flyway` dependency to `pom.xml`
- [ ] Change from Hibernate `database.generation=update` to `validate`
- [ ] Run existing migrations baseline

### Phase 4: Testing
- [ ] Unit tests for lazy loading behavior
- [ ] Integration tests for cascade operations
- [ ] Verify orphan removal works correctly

---

## Code Review Checklist

Before committing entity relationship changes:

- [ ] All foreign keys have corresponding JPA relationships
- [ ] Bidirectional relationships use `mappedBy` on the inverse side
- [ ] Cascade types match deletion semantics (CASCADE vs SET NULL vs RESTRICT)
- [ ] Orphan removal enabled for composition relationships
- [ ] Lazy loading properly configured (`fetch = FetchType.LAZY`)
- [ ] Service code updated to use relationships, not bare IDs
- [ ] No circular references without proper handling
- [ ] Tests verify relationship loading and cascade behavior

---

## Quick Reference: JPA Relationship Annotations

| Relationship | Annotation | Owning Side | Inverse Side |
|---|---|---|---|
| One-to-Many | `@OneToMany` | `mappedBy = "field"` | `@ManyToOne` |
| Many-to-One | `@ManyToOne` + `@JoinColumn` | (owning) | — |
| Many-to-Many | `@ManyToMany` | `@JoinTable` | `mappedBy = "field"` |

**Example:**

```java
// Owning side (Many)
@ManyToOne
@JoinColumn(name = "user_id")
private User user;

// Inverse side (One)
@OneToMany(mappedBy = "user")
private List<Report> reports;
```
