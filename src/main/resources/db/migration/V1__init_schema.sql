-- V1__init_schema.sql
-- Consolidated schema and initial data for students-report-alkarim

-- ========== TABLES ==========

-- LEVELS TABLE
CREATE TABLE IF NOT EXISTS levels (
  id SERIAL PRIMARY KEY,
  level_type VARCHAR(50) UNIQUE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_levels_level_type ON levels(level_type);

-- CATEGORIES TABLE
CREATE TABLE IF NOT EXISTS categories (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_categories_name ON categories(name);

-- CLASS_ROOMS TABLE
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

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL,
  regnumber VARCHAR(255) UNIQUE NOT NULL,
  gender VARCHAR(50),
  roles VARCHAR(50) NOT NULL,
  password VARCHAR(255),
  class_room_id INT,           -- References class_rooms (students only)
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_name ON users(name);
CREATE INDEX idx_users_regnumber ON users(regnumber);
CREATE INDEX idx_users_roles ON users(roles);
CREATE INDEX idx_users_class_room_id ON users(class_room_id);

-- QUESTIONS TABLE
CREATE TABLE IF NOT EXISTS questions (
  id SERIAL PRIMARY KEY,
  question TEXT NOT NULL,
  options VARCHAR(50),         -- AnswerType enum
  class_room_id INT,
  category_id INT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_questions_class_room_id ON questions(class_room_id);
CREATE INDEX idx_questions_category_id ON questions(category_id);

-- REPORTS TABLE
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

-- REFRESH_TOKENS TABLE
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id SERIAL PRIMARY KEY,
  token VARCHAR(500) UNIQUE NOT NULL,
  user_id INT,
  expire_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- ========== CONSTRAINTS ==========

-- users -> class_rooms
ALTER TABLE users ADD CONSTRAINT fk_users_class_room
  FOREIGN KEY (class_room_id) REFERENCES class_rooms(id)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- class_rooms -> users (teacher)
ALTER TABLE class_rooms ADD CONSTRAINT fk_class_rooms_teacher
  FOREIGN KEY (teacher_id) REFERENCES users(id)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- class_rooms -> levels
ALTER TABLE class_rooms ADD CONSTRAINT fk_class_rooms_level
  FOREIGN KEY (level_id) REFERENCES levels(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- questions -> class_rooms
ALTER TABLE questions ADD CONSTRAINT fk_questions_class_room
  FOREIGN KEY (class_room_id) REFERENCES class_rooms(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- questions -> categories
ALTER TABLE questions ADD CONSTRAINT fk_questions_category
  FOREIGN KEY (category_id) REFERENCES categories(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- reports -> users
ALTER TABLE reports ADD CONSTRAINT fk_reports_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- reports -> categories
ALTER TABLE reports ADD CONSTRAINT fk_reports_category
  FOREIGN KEY (category_id) REFERENCES categories(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

-- reports -> questions
ALTER TABLE reports ADD CONSTRAINT fk_reports_question
  FOREIGN KEY (question_id) REFERENCES questions(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

-- refresh_tokens -> users
ALTER TABLE refresh_tokens ADD CONSTRAINT fk_refresh_tokens_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;


-- ========== INITIAL DATA ==========

-- INSERT LEVELS
INSERT INTO levels (level_type) VALUES
('I'), ('II'), ('III'), ('IV'), ('V'), ('VI'), 
('VII'), ('VIII'), ('IX'), ('X'), ('XI'), ('XII');

-- INSERT CATEGORIES
INSERT INTO categories (name, created_at, updated_at) VALUES
('Mathematics', NOW(), NOW()),
('Science',     NOW(), NOW()),
('English',     NOW(), NOW()),
('History',     NOW(), NOW()),
('Programming', NOW(), NOW());

-- INSERT TEACHERS (Need users for class_rooms)
INSERT INTO users (name, regnumber, gender, roles, password, class_room_id, created_at, updated_at) VALUES
('Teacher Alice', 'TCH-001', 'FEMALE', 'TEACHER', '$2a$10$hashedpassword1', null, NOW(), NOW()),
('Teacher Bob',   'TCH-002', 'MALE',   'TEACHER', '$2a$10$hashedpassword2', null, NOW(), NOW()),
('Teacher Charlie', 'TCH-003', 'MALE', 'TEACHER', '$2a$10$hashedpassword3', null, NOW(), NOW());

-- INSERT CLASS_ROOMS
INSERT INTO class_rooms (name, student_count, teacher_id, level_id, created_at, updated_at) VALUES
('Class Alpha', 30, 1, 1, NOW(), NOW()),
('Class Beta',  25, 2, 2, NOW(), NOW()),
('Class Gamma', 28, 3, 3, NOW(), NOW());

-- INSERT STUDENTS
INSERT INTO users (name, regnumber, gender, roles, password, class_room_id, created_at, updated_at) VALUES
('David Brown',   'STU-001', 'MALE',   'STUDENT', '$2a$10$hashedpassword4',  1, NOW(), NOW()),
('Eva Martinez',  'STU-002', 'FEMALE', 'STUDENT', '$2a$10$hashedpassword5',  1, NOW(), NOW()),
('Frank Lee',     'STU-003', 'MALE',   'STUDENT', '$2a$10$hashedpassword6',  2, NOW(), NOW()),
('Grace Kim',     'STU-004', 'FEMALE', 'STUDENT', '$2a$10$hashedpassword7',  2, NOW(), NOW()),
('Henry Wilson',  'STU-005', 'MALE',   'STUDENT', '$2a$10$hashedpassword8',  3, NOW(), NOW()),
('Isla Thomas',   'STU-006', 'FEMALE', 'STUDENT', '$2a$10$hashedpassword9',  3, NOW(), NOW()),
('Jack Anderson', 'STU-007', 'MALE',   'STUDENT', '$2a$10$hashedpassword10', 1, NOW(), NOW());

-- INSERT QUESTIONS
INSERT INTO questions (question, options, class_room_id, category_id, created_at, updated_at) VALUES
('What is 2 + 2?',                          'MULTIPLE_CHOICE', 1, 1, NOW(), NOW()),
('Solve for x: 3x = 9',                     'MULTIPLE_CHOICE', 1, 1, NOW(), NOW()),
('What is the chemical symbol for water?',   'MULTIPLE_CHOICE', 2, 2, NOW(), NOW()),
('What planet is closest to the sun?',       'MULTIPLE_CHOICE', 2, 2, NOW(), NOW()),
('What is a synonym for "happy"?',           'MULTIPLE_CHOICE', 3, 3, NOW(), NOW()),
('Identify the noun in: "The dog barked."',  'MULTIPLE_CHOICE', 3, 3, NOW(), NOW()),
('Who was the first US president?',          'ESSAY',           1, 4, NOW(), NOW()),
('What year did WW2 end?',                   'MULTIPLE_CHOICE', 2, 4, NOW(), NOW()),
('What does HTML stand for?',                'MULTIPLE_CHOICE', 3, 5, NOW(), NOW()),
('What is a variable in programming?',       'ESSAY',           1, 5, NOW(), NOW());

-- INSERT REPORTS
INSERT INTO reports (content, answer, score, user_id, category_id, question_id, created_at, updated_at) VALUES
('Answered in exam session 1', '4',                          '100', 4, 1, 1,  NOW(), NOW()),
('Answered in exam session 1', '3',                          '100', 4, 1, 2,  NOW(), NOW()),
('Answered in exam session 1', 'H2O',                        '100', 5, 2, 3,  NOW(), NOW()),
('Answered in exam session 1', 'Mercury',                    '100', 5, 2, 4,  NOW(), NOW()),
('Answered in exam session 2', 'Joyful',                     '80',  6, 3, 5,  NOW(), NOW()),
('Answered in exam session 2', 'Dog',                        '100', 6, 3, 6,  NOW(), NOW()),
('Answered in exam session 2', 'George Washington',          '100', 7, 4, 7,  NOW(), NOW()),
('Answered in exam session 2', '1945',                       '100', 7, 4, 8,  NOW(), NOW()),
('Answered in exam session 3', 'Hyper Text Markup Language', '90',  8, 5, 9,  NOW(), NOW()),
('Answered in exam session 3', 'A named storage location',   '85',  8, 5, 10, NOW(), NOW());
