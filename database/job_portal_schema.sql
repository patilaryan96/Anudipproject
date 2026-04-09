CREATE DATABASE IF NOT EXISTS job_portal_db;
USE job_portal_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('JOB_SEEKER', 'RECRUITER') NOT NULL
);

CREATE TABLE IF NOT EXISTS profiles (
    user_id INT PRIMARY KEY,
    skills VARCHAR(255) NOT NULL,
    experience_years INT NOT NULL,
    headline VARCHAR(150) NOT NULL,
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jobs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recruiter_id INT NOT NULL,
    title VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    salary DECIMAL(12,2) NOT NULL,
    salary_type ENUM('LPA', 'Monthly') NOT NULL DEFAULT 'LPA',
    skills VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jobs_recruiter FOREIGN KEY (recruiter_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    job_id INT NOT NULL,
    user_id INT NOT NULL,
    status ENUM('Applied', 'Reviewed', 'Selected', 'Rejected') NOT NULL DEFAULT 'Applied',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_job_user UNIQUE (job_id, user_id),
    CONSTRAINT fk_app_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_app_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
