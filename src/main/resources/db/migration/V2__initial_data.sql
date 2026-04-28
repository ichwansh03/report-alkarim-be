-- V2__initial_data.sql
-- Idempotent initial data insertion for students-report-alkarim
-- Uses WHERE NOT EXISTS checks to avoid duplicate inserts when run multiple times

-- INSERT LEVELS (if not present)
INSERT INTO levels (level_type)
SELECT v
FROM (VALUES
  ('I'), ('II'), ('III'), ('IV'), ('V'), ('VI'),
  ('VII'), ('VIII'), ('IX'), ('X'), ('XI'), ('XII')
) AS t(v)
WHERE NOT EXISTS (SELECT 1 FROM levels WHERE level_type = t.v);

-- INSERT CATEGORIES
INSERT INTO categories (name, created_at, updated_at)
SELECT v, now(), now()
FROM (VALUES
  ('Mathematics'),
  ('Science'),
  ('English'),
  ('History'),
  ('Programming')
) AS t(v)
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = t.v);

-- INSERT TEACHERS (regnumber unique guard)
INSERT INTO users (name, regnumber, gender, roles, password, class_room_id, created_at, updated_at)
SELECT name, regnum, gender, roles, pass, NULL, now(), now()
FROM (VALUES
  ('Teacher Alice','TCH-001','FEMALE','TEACHER','$2a$10$hashedpassword1'),
  ('Teacher Bob','TCH-002','MALE','TEACHER','$2a$10$hashedpassword2'),
  ('Teacher Charlie','TCH-003','MALE','TEACHER','$2a$10$hashedpassword3')
) AS t(name, regnum, gender, roles, pass)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE regnumber = t.regnum);

-- INSERT CLASS_ROOMS (use teacher regnumber and level_type lookups)
INSERT INTO class_rooms (name, student_count, teacher_id, level_id, created_at, updated_at)
SELECT name, scount,
  (SELECT id FROM users WHERE regnumber = teacher_reg),
  (SELECT id FROM levels WHERE level_type = lvl),
  now(), now()
FROM (VALUES
  ('Class Alpha', 30, 'TCH-001', 'I'),
  ('Class Beta', 25,  'TCH-002', 'II'),
  ('Class Gamma',28,  'TCH-003', 'III')
) AS t(name, scount, teacher_reg, lvl)
WHERE NOT EXISTS (SELECT 1 FROM class_rooms WHERE name = t.name);

-- INSERT STUDENTS
INSERT INTO users (name, regnumber, gender, roles, password, class_room_id, created_at, updated_at)
SELECT name, regnum, gender, roles, pass,
  (SELECT id FROM class_rooms WHERE name = cls_name), now(), now()
FROM (VALUES
  ('David Brown','STU-001','MALE','STUDENT','$2a$10$hashedpassword4','Class Alpha'),
  ('Eva Martinez','STU-002','FEMALE','STUDENT','$2a$10$hashedpassword5','Class Alpha'),
  ('Frank Lee','STU-003','MALE','STUDENT','$2a$10$hashedpassword6','Class Beta'),
  ('Grace Kim','STU-004','FEMALE','STUDENT','$2a$10$hashedpassword7','Class Beta'),
  ('Henry Wilson','STU-005','MALE','STUDENT','$2a$10$hashedpassword8','Class Gamma'),
  ('Isla Thomas','STU-006','FEMALE','STUDENT','$2a$10$hashedpassword9','Class Gamma'),
  ('Jack Anderson','STU-007','MALE','STUDENT','$2a$10$hashedpassword10','Class Alpha')
) AS t(name, regnum, gender, roles, pass, cls_name)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE regnumber = t.regnum);

-- INSERT QUESTIONS (use category and class_room lookups)
INSERT INTO questions (question, options, class_room_id, category_id, created_at, updated_at)
SELECT q, opts,
  (SELECT id FROM class_rooms WHERE name = cls_name),
  (SELECT id FROM categories WHERE name = cat_name), now(), now()
FROM (VALUES
  ('What is 2 + 2?','MULTIPLE_CHOICE','Class Alpha','Mathematics'),
  ('Solve for x: 3x = 9','MULTIPLE_CHOICE','Class Alpha','Mathematics'),
  ('What is the chemical symbol for water?','MULTIPLE_CHOICE','Class Beta','Science'),
  ('What planet is closest to the sun?','MULTIPLE_CHOICE','Class Beta','Science'),
  ('What is a synonym for "happy"?','MULTIPLE_CHOICE','Class Gamma','English'),
  ('Identify the noun in: "The dog barked."','MULTIPLE_CHOICE','Class Gamma','English'),
  ('Who was the first US president?','ESSAY','Class Alpha','History'),
  ('What year did WW2 end?','MULTIPLE_CHOICE','Class Beta','History'),
  ('What does HTML stand for?','MULTIPLE_CHOICE','Class Gamma','Programming'),
  ('What is a variable in programming?','ESSAY','Class Alpha','Programming')
) AS t(q, opts, cls_name, cat_name)
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question = t.q AND class_room_id = (SELECT id FROM class_rooms WHERE name = t.cls_name));

-- INSERT REPORTS (use user regnumber, category name, and question lookup)
INSERT INTO reports (content, answer, score, user_id, category_id, question_id, created_at, updated_at)
SELECT content, ans, score::INT,
  (SELECT id FROM users WHERE regnumber = regnum),
  (SELECT id FROM categories WHERE name = cat_name),
  (SELECT id FROM questions WHERE question = q_text LIMIT 1), now(), now()
FROM (VALUES
  ('Answered in exam session 1','4','100','STU-002','Mathematics','What is 2 + 2?'),
  ('Answered in exam session 1','3','100','STU-002','Mathematics','Solve for x: 3x = 9'),
  ('Answered in exam session 1','H2O','100','STU-003','Science','What is the chemical symbol for water?'),
  ('Answered in exam session 1','Mercury','100','STU-003','Science','What planet is closest to the sun?'),
  ('Answered in exam session 2','Joyful','80','STU-004','English','What is a synonym for "happy"?'),
  ('Answered in exam session 2','Dog','100','STU-004','English','Identify the noun in: "The dog barked."'),
  ('Answered in exam session 2','George Washington','100','STU-005','History','Who was the first US president?'),
  ('Answered in exam session 2','1945','100','STU-005','History','What year did WW2 end?'),
  ('Answered in exam session 3','Hyper Text Markup Language','90','STU-006','Programming','What does HTML stand for?'),
  ('Answered in exam session 3','A named storage location','85','STU-006','Programming','What is a variable in programming?')
) AS t(content, ans, score, regnum, cat_name, q_text)
WHERE NOT EXISTS (
  SELECT 1 FROM reports r
  WHERE r.user_id = (SELECT id FROM users WHERE regnumber = t.regnum)
    AND r.question_id = (SELECT id FROM questions WHERE question = t.q_text LIMIT 1)
);

-- INSERT a sample refresh token (if needed)
INSERT INTO refresh_tokens (token, user_id, expire_at, created_at, updated_at)
SELECT tkn, (SELECT id FROM users WHERE regnumber = regnum), now() + INTERVAL '30 days', now(), now()
FROM (VALUES
  ('sample-refresh-token-123','TCH-001')
) AS t(tkn, regnum)
WHERE NOT EXISTS (SELECT 1 FROM refresh_tokens WHERE token = t.tkn);

