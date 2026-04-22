---
name: db-migration
description: 'Database migration workflow for Quarkus/Flyway. Use when: creating new migrations from entity changes, running/validating migrations, reviewing migration safety, debugging failed migrations, planning comprehensive schema changes.'
argument-hint: 'Describe the migration task: creating, running, reviewing, or troubleshooting'
---

# Database Migration Workflow

## When to Use

This skill applies to any database migration task in a Quarkus application using Flyway:

- **Creating migrations** — Converting JPA entity changes into SQL migration files
- **Running migrations** — Executing pending migrations and verifying schema changes
- **Reviewing migrations** — Auditing SQL quality, performance impact, and rollback safety
- **Troubleshooting** — Debugging failed migrations, resolving schema conflicts, or recovering from corrupted migration history
- **Planning** — Designing comprehensive multi-step schema changes across environments

## Key Concepts

### Flyway Migration File Naming

```
V<version>_<description>.sql
```

- `V` = Versioned migration (uppercase required)
- `<version>` = Sequential number (e.g., 1, 2, 3, or 1.1, 1.2). Version must be greater than previous
- `<description>` = Snake_case summary of changes (max ~50 chars)
- `.sql` = SQL file extension

Example: `V2_add_user_status_column.sql`

### Quarkus Configuration

In `application.properties` or `application-api.properties`:

```properties
quarkus.flyway.migrate-at-startup=true
quarkus.flyway.locations=classpath:db/migration
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/mydb
quarkus.datasource.username=user
quarkus.datasource.password=pass
```

### Location Convention

Migration files are stored in:
```
src/main/resources/db/migration/
```

## Procedure: Creating a New Migration

### Step 1: Identify Schema Changes

Determine what needs to change:
- [ ] New table creation
- [ ] Column additions/removals/modifications
- [ ] Index creation/removal
- [ ] Constraint changes (FK, PK, unique, check)
- [ ] Data transformations
- [ ] Trigger/function changes

If changes come from JPA entity updates:
- [ ] Review `@Entity` class changes
- [ ] Check `@Column`, `@OneToMany`, `@ManyToOne` annotations
- [ ] Verify `@Table` and `@Index` definitions

### Step 2: Calculate Next Version

Check existing migration files in `src/main/resources/db/migration/`:
- [ ] Identify the highest version number in use
- [ ] Increment by 1 (e.g., V3 → V4)
- [ ] For patch versions: V2_1, V2_2 (use only if previous migration incomplete)

### Step 3: Write Migration SQL

Create file: `src/main/resources/db/migration/V<version>_<description>.sql`

**Principles:**
- Make migrations **idempotent** where possible (safe to re-run)
- Use `IF NOT EXISTS` / `IF EXISTS` for table/column operations
- Keep migrations focused on one logical change
- Include comments explaining business logic
- Write forward and rollback paths (comments showing rollback)

**Example migration for students-report-alkarim project:**

```sql
-- V2_add_student_section_to_users.sql
-- Adds section tracking for student classification

ALTER TABLE users
  ADD COLUMN section VARCHAR(10),
  ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true,
  ADD COLUMN last_login TIMESTAMP;

CREATE INDEX idx_users_section ON users(section);
CREATE INDEX idx_users_is_active ON users(is_active);

-- Rollback:
-- DROP INDEX IF EXISTS idx_users_is_active;
-- DROP INDEX IF EXISTS idx_users_section;
-- ALTER TABLE users DROP COLUMN last_login;
-- ALTER TABLE users DROP COLUMN is_active;
-- ALTER TABLE users DROP COLUMN section;
```

### Step 4: Test Locally

- [ ] Run `./mvnw clean quarkus:dev` to auto-apply migration at startup
- [ ] Verify `flyway_schema_history` table shows new migration with SUCCESS status
- [ ] Test application behavior with new schema
- [ ] Verify data integrity and performance

### Step 5: Code Review

Check migration for:
- [ ] Naming follows `V<version>_<description>.sql` convention
- [ ] No breaking changes (when possible)
- [ ] Indexes on foreign key columns
- [ ] Rollback path documented in comments
- [ ] Performance considerations noted (index, constraints on large tables)

## Procedure: Running Migrations

### Step 1: Verify Configuration

Check `application.properties` or environment overrides:

```bash
grep -E "quarkus.flyway|quarkus.datasource" src/main/resources/application*.properties
```

- [ ] Database URL points to correct environment
- [ ] Credentials are valid
- [ ] `migrate-at-startup=true` is set for dev/test environments

### Step 2: Check Pending Migrations

View pending migrations without applying:

```bash
./mvnw flyway:info
```

Output shows:
- Version, description, type, installed on, execution time, success status

### Step 3: Run Migrations

Auto-apply at startup:
```bash
./mvnw clean quarkus:dev
```

Or manually trigger (if `migrate-at-startup=false`):
```bash
./mvnw flyway:migrate
```

### Step 4: Verify History

Query the migration history table:

```sql
SELECT * FROM flyway_schema_history 
  ORDER BY installed_rank DESC 
  LIMIT 5;
```

- [ ] All pending migrations show `success = true`
- [ ] No `pending` status migrations remain
- [ ] Execution times are reasonable (watch for long-running DDL)

## Procedure: Reviewing Migrations

### Code Review Checklist

**SQL Safety:**
- [ ] No schema changes without `IF [NOT] EXISTS` guards
- [ ] No data loss without documented approval
- [ ] No implicit type conversions
- [ ] Foreign key constraints respected

**Performance:**
- [ ] Large table modifications use `CONCURRENTLY` (PostgreSQL) when applicable
- [ ] Indexes created on lookup columns
- [ ] Full table scans avoided in WHERE clauses

**Compliance:**
- [ ] Rollback path documented
- [ ] Version number increments correctly
- [ ] Naming convention followed
- [ ] Author/date comments included

**Example Review Comment:**

```
✓ Version number increments correctly (V1 → V2)
✓ Rollback documented in comments
⚠ Consider adding index on status for query performance
✓ IF NOT EXISTS prevents duplicate application
```

## Procedure: Troubleshooting Failed Migrations

### Symptom: Migration Status = ERROR

```sql
SELECT * FROM flyway_schema_history WHERE success = false;
```

**Recovery Options:**

#### Option A: Repair and Reapply

If migration is partially applied:

1. Manually undo the partial changes:
   ```sql
   -- Revert the incomplete transaction
   DROP TABLE IF EXISTS new_table;
   ALTER TABLE users DROP COLUMN IF EXISTS new_column;
   ```

2. Mark migration as repaired:
   ```sql
   DELETE FROM flyway_schema_history 
   WHERE version = 'X' AND success = false;
   ```

3. Re-apply:
   ```bash
   ./mvnw flyway:migrate
   ```

#### Option B: Baseline (Emergency Only)

If migration history is corrupted and cannot be recovered:

```bash
./mvnw flyway:baseline -Dflyway.baselineVersion=<version>
```

This marks all migrations up to `<version>` as applied without running them. **Use only in production disaster recovery.**

### Symptom: Pending Migrations Not Applying

1. Check database connectivity:
   ```bash
   ./mvnw flyway:info -X
   ```

2. Verify migration file location: `src/main/resources/db/migration/V*.sql`

3. Check file permissions and naming:
   ```bash
   ls -la src/main/resources/db/migration/
   ```

4. Inspect Flyway configuration in logs

## Procedure: Multi-Step Schema Changes

For large, complex migrations spread across multiple steps:

### Step 1: Plan Rollback Strategy

**Expand Window Method** (safest):
```
V1: Add new column (nullable)
V2: Populate new column with data
V3: Remove old column / Add NOT NULL constraint
```

This allows rollback at each step.

### Step 2: Use Flyway Callbacks (Optional)

Add Java-based migration hooks in `src/main/java/org/ichwan/db/migration/`:

```java
@Slf4j
public class V3_PopulateUserStatusCallback implements Callback {
  @Override
  public void handle(CallbackContext context) throws SQLException {
    // Complex logic to populate new column
    log.info("Populating user status from legacy data");
  }
}
```

### Step 3: Validate Schema Consistency

After multi-step migration:

```sql
-- Verify table structure
\d users

-- Check constraint definitions
SELECT constraint_name, constraint_type 
  FROM information_schema.table_constraints 
  WHERE table_name = 'users';

-- List indexes
\di users*
```

## Common Patterns for This Project

### Adding a Column to Users Table

```sql
-- Add new field to track student progress
ALTER TABLE users 
  ADD COLUMN average_score DECIMAL(5,2) DEFAULT 0.0,
  ADD COLUMN total_reports_submitted INT DEFAULT 0;

CREATE INDEX idx_users_average_score ON users(average_score DESC);
```

### Safe Column Removal

```sql
-- Step 1: Drop dependent indexes/constraints
DROP INDEX IF EXISTS idx_users_section;
ALTER TABLE class_rooms DROP CONSTRAINT IF EXISTS fk_class_rooms_teacher;

-- Step 2: Remove column
ALTER TABLE users DROP COLUMN IF EXISTS section;
```

### Adding Foreign Key Constraint to RefreshToken (CRITICAL)

```sql
-- RefreshToken currently has userId but NO FK constraint
-- This migration adds the missing constraint
ALTER TABLE refresh_tokens ADD CONSTRAINT fk_refresh_tokens_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE;

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
```

## Quick Reference: File Locations

| Item | Path |
|------|------|
| Migration files | `src/main/resources/db/migration/` |
| Quarkus config | `src/main/resources/application.properties` |
| Alternative config | `src/main/resources/application-api.properties` |
| Migration history | Database table `flyway_schema_history` |
| Docker database | `src/main/docker/data/pgdata/` |
| Domain entities | `src/main/java/org/ichwan/domain/` |

## Domain Model Reference

Your project uses these entities and relationships:

| Entity | Table | Relationships |
|--------|-------|--------------|
| **User** | `users` | `→ ClassRoom` (as student), `→ ClassRoom` (as teacher), `← Report`, `← RefreshToken` |
| **ClassRoom** | `class_rooms` | `→ User` (teacher_id), `→ Level` (level_id), `← User` (students) |
| **Level** | `levels` | `← ClassRoom` |
| **Category** | `categories` | `← Question`, `← Report` |
| **Question** | `questions` | `→ ClassRoom` (class_room_id), `→ Category` (category_id), `← Report` |
| **Report** | `reports` | `→ User` (user_id), `→ Category` (category_id), `→ Question` (question_id) |
| **RefreshToken** | `refresh_tokens` | `→ User` (user_id) ⚠️ **Missing FK constraint** |

**⚠️ Known Gaps in Current Schema:**
1. **RefreshToken.user_id** — No foreign key constraint defined (see V2_fix_refresh_token_fk.sql)
2. **Bidirectional relationships** — No `@OneToMany` reverse mappings defined
3. **Schema version control** — Currently using Hibernate `database.generation=update` (enable Flyway for production)

## Links

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Quarkus Datasources Guide](https://quarkus.io/guides/datasource)
- [PostgreSQL DDL Reference](https://www.postgresql.org/docs/current/ddl.html)
