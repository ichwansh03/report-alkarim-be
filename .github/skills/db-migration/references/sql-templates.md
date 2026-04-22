# SQL Migration Examples & Templates

## PostgreSQL Migration Templates

### Create New Table

```sql
-- V3_create_students_table.sql
-- Creates core students table with audit columns

CREATE TABLE IF NOT EXISTS students (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_created_at ON students(created_at DESC);

-- Rollback:
-- DROP TABLE IF EXISTS students CASCADE;
```

### Add Column to Existing Table

```sql
-- V4_add_status_to_users.sql
-- Tracks user account status for feature flags

ALTER TABLE users 
  ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
  ADD COLUMN status_updated_at TIMESTAMP DEFAULT NOW();

CREATE INDEX idx_users_status ON users(status);

-- Add constraint if needed
ALTER TABLE users ADD CONSTRAINT ck_users_status 
  CHECK (status IN ('ACTIVE', 'SUSPENDED', 'INACTIVE'));

-- Rollback:
-- ALTER TABLE users DROP CONSTRAINT IF EXISTS ck_users_status;
-- DROP INDEX IF EXISTS idx_users_status;
-- ALTER TABLE users DROP COLUMN status_updated_at;
-- ALTER TABLE users DROP COLUMN status;
```

### Modify Column Type

```sql
-- V5_change_user_phone_to_text.sql
-- Allows international phone format

-- Create temporary column with new type
ALTER TABLE users ADD COLUMN phone_new TEXT;

-- Copy and transform data
UPDATE users SET phone_new = phone::TEXT;

-- Drop old, rename new
ALTER TABLE users DROP COLUMN phone;
ALTER TABLE users RENAME COLUMN phone_new TO phone;

-- Rollback:
-- ALTER TABLE users ADD COLUMN phone_old VARCHAR(20);
-- UPDATE users SET phone_old = phone;
-- ALTER TABLE users DROP COLUMN phone;
-- ALTER TABLE users RENAME COLUMN phone_old TO phone;
```

### Create Foreign Key

```sql
-- V6_add_student_class_relationship.sql
-- Links students to their classroom

-- Add foreign key column
ALTER TABLE students 
  ADD COLUMN class_id INT;

-- Create the constraint (assumes classes table exists)
ALTER TABLE students ADD CONSTRAINT fk_students_class
  FOREIGN KEY (class_id) REFERENCES classes(id)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

CREATE INDEX idx_students_class_id ON students(class_id);

-- Rollback:
-- ALTER TABLE students DROP CONSTRAINT fk_students_class;
-- DROP INDEX IF EXISTS idx_students_class_id;
-- ALTER TABLE students DROP COLUMN class_id;
```

### Rename Column

```sql
-- V7_rename_user_status_to_account_status.sql

ALTER TABLE users RENAME COLUMN status TO account_status;

-- Update related index names for clarity (PostgreSQL automatically renames check constraints with old column name)
-- Manual index rename if needed

-- Rollback:
-- ALTER TABLE users RENAME COLUMN account_status TO status;
```

### Add Data Validation Constraint

```sql
-- V8_add_email_validation_check.sql

ALTER TABLE users ADD CONSTRAINT ck_users_email_format 
  CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$');

-- Rollback:
-- ALTER TABLE users DROP CONSTRAINT ck_users_email_format;
```

### Backfill Data

```sql
-- V9_backfill_status_from_legacy_field.sql
-- Populates new status column based on legacy_active flag

UPDATE students 
SET status = CASE 
  WHEN legacy_active = true THEN 'ACTIVE'
  WHEN legacy_active = false THEN 'SUSPENDED'
  ELSE 'UNKNOWN'
END
WHERE status IS NULL OR status = 'ACTIVE';

-- Verify counts
-- SELECT status, COUNT(*) FROM students GROUP BY status;

-- Rollback:
-- UPDATE students SET status = 'ACTIVE' WHERE status IN ('SUSPENDED', 'UNKNOWN');
```

### Drop Unused Column

```sql
-- V10_remove_legacy_active_field.sql
-- Cleans up legacy_active column no longer needed

-- Step 1: Drop dependent objects
DROP INDEX IF EXISTS idx_students_legacy_active;
ALTER TABLE students DROP CONSTRAINT IF EXISTS ck_legacy_active;

-- Step 2: Drop column
ALTER TABLE students DROP COLUMN IF EXISTS legacy_active;

-- Rollback:
-- ALTER TABLE students ADD COLUMN legacy_active BOOLEAN DEFAULT FALSE;
-- CREATE INDEX idx_students_legacy_active ON students(legacy_active);
```

### Create Junction Table (Many-to-Many)

```sql
-- V11_create_student_course_junction.sql
-- Implements many-to-many relationship between students and courses

CREATE TABLE IF NOT EXISTS student_courses (
  id SERIAL PRIMARY KEY,
  student_id INT NOT NULL,
  course_id INT NOT NULL,
  enrolled_at TIMESTAMP DEFAULT NOW(),
  
  CONSTRAINT fk_student_courses_student 
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  CONSTRAINT fk_student_courses_course 
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
  CONSTRAINT uq_student_courses_unique 
    UNIQUE (student_id, course_id)
);

CREATE INDEX idx_student_courses_student_id ON student_courses(student_id);
CREATE INDEX idx_student_courses_course_id ON student_courses(course_id);

-- Rollback:
-- DROP TABLE IF EXISTS student_courses CASCADE;
```

## Common Patterns for Students/Reports Project

### Track Entity Changes (Audit Columns)

```sql
-- V12_add_audit_columns_to_reports.sql

ALTER TABLE reports
  ADD COLUMN created_by VARCHAR(255),
  ADD COLUMN created_at TIMESTAMP DEFAULT NOW(),
  ADD COLUMN updated_by VARCHAR(255),
  ADD COLUMN updated_at TIMESTAMP DEFAULT NOW();

-- Rollback:
-- ALTER TABLE reports DROP COLUMN updated_at;
-- ALTER TABLE reports DROP COLUMN updated_by;
-- ALTER TABLE reports DROP COLUMN created_at;
-- ALTER TABLE reports DROP COLUMN created_by;
```

### Enable Full-Text Search

```sql
-- V13_add_report_search_index.sql
-- Enables full-text search on report titles and descriptions

CREATE INDEX idx_reports_search ON reports 
  USING gin(to_tsvector('english', title || ' ' || COALESCE(description, '')));

-- Rollback:
-- DROP INDEX idx_reports_search;
```

## Migration Validation Queries

### Check Migration History

```sql
SELECT * FROM flyway_schema_history 
ORDER BY installed_rank DESC;
```

### Identify Pending Migrations

```sql
-- Migrations in code but not in database
SELECT version FROM flyway_schema_history 
WHERE success = false;
```

### Verify Table Structure

```sql
-- Check columns of a table
\d students

-- Or via information schema:
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'students'
ORDER BY ordinal_position;
```

### Find Unused Indexes

```sql
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes 
WHERE idx_scan = 0 AND indexname NOT LIKE 'pg_%'
ORDER BY pg_relation_size(indexrelid) DESC;
```
