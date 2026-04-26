-- V1_create_schema.sql
-- Creates the core schema for students-report-alkarim application
-- Runs FIRST, then V2-V7 insert data in dependency order
--
-- Tables:
--   - levels: Education levels (I-XII)
--   - users: Students and teachers
--   - class_rooms: Classrooms with teacher and level assignment
--   - categories: Report categories (Math, Science, etc.)
--   - questions: Questions assigned to classrooms
--   - reports: Student answers and scores
--   - refresh_tokens: JWT refresh token storage

-- ========== LEVELS TABLE ==========
CREATE TABLE IF NOT EXISTS levels (
  id SERIAL PRIMARY KEY,
  level_type VARCHAR(50) UNIQUE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_levels_level_type ON levels(level_type);

-- ========== USERS TABLE ==========
-- Stores both students and teachers
-- Teachers are identified by references in class_rooms table
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL,
  regnumber VARCHAR(255) UNIQUE NOT NULL,
  gender VARCHAR(50),
  roles VARCHAR(50) NOT NULL,  -- e.g., STUDENT, TEACHER
  password VARCHAR(255),
  class_room_id INT,           -- References classroom (students only)
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_name ON users(name);
CREATE INDEX idx_users_regnumber ON users(regnumber);
CREATE INDEX idx_users_roles ON users(roles);
CREATE INDEX idx_users_class_room_id ON users(class_room_id);

-- ========== CLASS_ROOMS TABLE ==========
-- Classroom with teacher and level assignment
CREATE TABLE IF NOT EXISTS class_rooms (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  student_count INT DEFAULT 0,
  teacher_id INT,              -- References users (teacher)
  level_id INT,                -- References levels
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_class_rooms_name ON class_rooms(name);
CREATE INDEX idx_class_rooms_teacher_id ON class_rooms(teacher_id);
CREATE INDEX idx_class_rooms_level_id ON class_rooms(level_id);

-- ========== FOREIGN KEY: users.class_room_id -> class_rooms ==========
ALTER TABLE users ADD CONSTRAINT fk_users_class_room
  FOREIGN KEY (class_room_id) REFERENCES class_rooms(id)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- ========== FOREIGN KEY: class_rooms.teacher_id -> users ==========
ALTER TABLE class_rooms ADD CONSTRAINT fk_class_rooms_teacher
  FOREIGN KEY (teacher_id) REFERENCES users(id)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- ========== FOREIGN KEY: class_rooms.level_id -> levels ==========
ALTER TABLE class_rooms ADD CONSTRAINT fk_class_rooms_level
  FOREIGN KEY (level_id) REFERENCES levels(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- ========== CATEGORIES TABLE ==========
-- Question categories (Math, Science, English, History, Programming)
CREATE TABLE IF NOT EXISTS categories (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_categories_name ON categories(name);

-- ========== QUESTIONS TABLE ==========
-- Questions assigned to classrooms
CREATE TABLE IF NOT EXISTS questions (
  id SERIAL PRIMARY KEY,
  question TEXT NOT NULL,
  options VARCHAR(50),         -- AnswerType enum: MULTIPLE_CHOICE, ESSAY
  class_room_id INT,
  category_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_questions_class_room_id ON questions(class_room_id);
CREATE INDEX idx_questions_category_id ON questions(category_id);

-- ========== FOREIGN KEY: questions.class_room_id -> class_rooms ==========
ALTER TABLE questions ADD CONSTRAINT fk_questions_class_room
  FOREIGN KEY (class_room_id) REFERENCES class_rooms(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- ========== FOREIGN KEY: questions.category_id -> categories ==========
ALTER TABLE questions ADD CONSTRAINT fk_questions_category
  FOREIGN KEY (category_id) REFERENCES categories(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- ========== REPORTS TABLE ==========
-- Student answers and scores
CREATE TABLE IF NOT EXISTS reports (
  id SERIAL PRIMARY KEY,
  content TEXT,
  answer TEXT,
  score INT,
  user_id INT,                 -- Student who submitted
  category_id INT,
  question_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reports_user_id ON reports(user_id);
CREATE INDEX idx_reports_category_id ON reports(category_id);
CREATE INDEX idx_reports_question_id ON reports(question_id);
CREATE INDEX idx_reports_created_at ON reports(created_at DESC);

-- ========== FOREIGN KEY: reports.user_id -> users ==========
ALTER TABLE reports ADD CONSTRAINT fk_reports_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- ========== FOREIGN KEY: reports.category_id -> categories ==========
ALTER TABLE reports ADD CONSTRAINT fk_reports_category
  FOREIGN KEY (category_id) REFERENCES categories(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- ========== FOREIGN KEY: reports.question_id -> questions ==========
ALTER TABLE reports ADD CONSTRAINT fk_reports_question
  FOREIGN KEY (question_id) REFERENCES questions(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- ========== REFRESH_TOKENS TABLE ==========
-- JWT refresh token storage
-- ⚠️ Current schema has user_id but NO foreign key constraint
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id SERIAL PRIMARY KEY,
  token VARCHAR(500) UNIQUE NOT NULL,
  user_id INT,                 -- CRITICAL: Add foreign key constraint
  expire_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- ========== FOREIGN KEY: refresh_tokens.user_id -> users ==========
ALTER TABLE refresh_tokens ADD CONSTRAINT fk_refresh_tokens_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- ========== MIGRATION NOTES ==========
-- 
-- SCHEMA CONTROL:
-- - This migration brings Flyway schema versioning into effect
-- - Previously tables were created by Hibernate ORM with database.generation=update
-- - Going forward, all schema changes must be version-controlled via migrations
--
-- FOREIGN KEY STRATEGY:
-- - ON DELETE CASCADE: Parent deletion cascades to children (reports, questions)
-- - ON DELETE SET NULL: Optional relationships (users.class_room_id)
-- - ON DELETE RESTRICT: Prevent deletion of referenced rows (levels, categories)
--
-- MISSING BIDIRECTIONAL MAPPINGS:
-- Consider updating JPA entities to include @OneToMany reverse mappings:
--   - ClassRoom → List<User> (students)
--   - User → List<Report>
--   - Category → List<Question>
--   - Category → List<Report>
--   - Question → List<Report>
--
-- Rollback: This migration cannot safely be rolled back in production.
-- In development: DROP all tables and re-apply Hibernate schema generation.
