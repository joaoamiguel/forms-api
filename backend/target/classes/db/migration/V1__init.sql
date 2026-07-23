CREATE TABLE IF NOT EXISTS roles (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS forms (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(100) NOT NULL UNIQUE,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS form_allowed_roles (
  form_id BIGINT NOT NULL,
  role_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (form_id, role_name),
  CONSTRAINT fk_form_allowed_roles_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS questions (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  form_id BIGINT NOT NULL,
  question_key VARCHAR(100) NOT NULL,
  label TEXT NOT NULL,
  type VARCHAR(50) NOT NULL,
  required BOOLEAN NOT NULL,
  reversed BOOLEAN NOT NULL,
  correct_option_index INT NULL,
  display_order INT NULL,
  section_title VARCHAR(255) NULL,
  help_text TEXT NULL,
  CONSTRAINT fk_questions_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_options (
  question_id BIGINT NOT NULL,
  option_order INT NOT NULL,
  option_label TEXT NOT NULL,
  PRIMARY KEY (question_id, option_order),
  CONSTRAINT fk_question_options_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS submissions (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  form_id BIGINT NOT NULL,
  submitted_at TIMESTAMP NOT NULL,
  status VARCHAR(50) NOT NULL,
  CONSTRAINT uq_submission_user_form UNIQUE (user_id, form_id),
  CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_submissions_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS answers (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  submission_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  answer_json TEXT NULL,
  CONSTRAINT fk_answers_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
  CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS scores (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  submission_id BIGINT NOT NULL UNIQUE,
  readiness_score INT NULL,
  literacy_score INT NULL,
  opportunity_score INT NULL,
  quadrant VARCHAR(120) NULL,
  technical_score INT NULL,
  technical_band VARCHAR(120) NULL,
  result_json TEXT NULL,
  CONSTRAINT fk_scores_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
);
