-- V1__init_schema.sql
-- Consolidated schema for students-report-alkarim (schema only)

-- ========== TABLES ==========

-- LEVELS TABLE
CREATE TABLE IF NOT EXISTS levels (
  id SERIAL PRIMARY KEY,
  level_type VARCHAR(50) UNIQUE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_levels_level_type ON levels(level_type);

-- CATEGORIES TABLE
CREATE TABLE IF NOT EXISTS categories (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_categories_name ON categories(name);

-- CLASS_ROOMS TABLE
CREATE TABLE IF NOT EXISTS class_rooms (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  student_count INT DEFAULT 0,
  teacher_id INT,
  level_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_class_rooms_name ON class_rooms(name);
CREATE INDEX IF NOT EXISTS idx_class_rooms_teacher_id ON class_rooms(teacher_id);
CREATE INDEX IF NOT EXISTS idx_class_rooms_level_id ON class_rooms(level_id);

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL,
  regnumber VARCHAR(255) UNIQUE NOT NULL,
  gender VARCHAR(50),
  roles VARCHAR(50) NOT NULL,
  password VARCHAR(255),
  class_room_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_name ON users(name);
CREATE INDEX IF NOT EXISTS idx_users_regnumber ON users(regnumber);
CREATE INDEX IF NOT EXISTS idx_users_roles ON users(roles);
CREATE INDEX IF NOT EXISTS idx_users_class_room_id ON users(class_room_id);

-- QUESTIONS TABLE
CREATE TABLE IF NOT EXISTS questions (
  id SERIAL PRIMARY KEY,
  question TEXT NOT NULL,
  options VARCHAR(50),
  class_room_id INT,
  category_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_questions_class_room_id ON questions(class_room_id);
CREATE INDEX IF NOT EXISTS idx_questions_category_id ON questions(category_id);

-- REPORTS TABLE
CREATE TABLE IF NOT EXISTS reports (
  id SERIAL PRIMARY KEY,
  content TEXT,
  answer TEXT,
  score INT,
  user_id INT,
  category_id INT,
  question_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_reports_user_id ON reports(user_id);
CREATE INDEX IF NOT EXISTS idx_reports_category_id ON reports(category_id);
CREATE INDEX IF NOT EXISTS idx_reports_question_id ON reports(question_id);

-- REFRESH_TOKENS TABLE
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id SERIAL PRIMARY KEY,
  token VARCHAR(500) UNIQUE NOT NULL,
  user_id INT,
  expire_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- ========== CONSTRAINTS ==========

-- users -> class_rooms
ALTER TABLE IF EXISTS users ADD CONSTRAINT IF NOT EXISTS fk_users_class_room
  FOREIGN KEY (class_room_id) REFERENCES class_rooms(id)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- class_rooms -> users (teacher)
ALTER TABLE IF EXISTS class_rooms ADD CONSTRAINT IF NOT EXISTS fk_class_rooms_teacher
  FOREIGN KEY (teacher_id) REFERENCES users(id)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- class_rooms -> levels
ALTER TABLE IF EXISTS class_rooms ADD CONSTRAINT IF NOT EXISTS fk_class_rooms_level
  FOREIGN KEY (level_id) REFERENCES levels(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- questions -> class_rooms
ALTER TABLE IF EXISTS questions ADD CONSTRAINT IF NOT EXISTS fk_questions_class_room
  FOREIGN KEY (class_room_id) REFERENCES class_rooms(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- questions -> categories
ALTER TABLE IF EXISTS questions ADD CONSTRAINT IF NOT EXISTS fk_questions_category
  FOREIGN KEY (category_id) REFERENCES categories(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- reports -> users
ALTER TABLE IF EXISTS reports ADD CONSTRAINT IF NOT EXISTS fk_reports_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- reports -> categories
ALTER TABLE IF EXISTS reports ADD CONSTRAINT IF NOT EXISTS fk_reports_category
  FOREIGN KEY (category_id) REFERENCES categories(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- reports -> questions
ALTER TABLE IF EXISTS reports ADD CONSTRAINT IF NOT EXISTS fk_reports_question
  FOREIGN KEY (question_id) REFERENCES questions(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- refresh_tokens -> users
ALTER TABLE IF EXISTS refresh_tokens ADD CONSTRAINT IF NOT EXISTS fk_refresh_tokens_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- NOTE: initial data moved to separate migration V2__initial_data.sql to ensure Flyway will run data insertion after schema is present.
