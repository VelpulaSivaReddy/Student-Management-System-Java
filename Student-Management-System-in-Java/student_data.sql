-- ============================================================
-- Student Management System — Database Setup Script
-- ============================================================
-- Run this script in MySQL to set up the database and
-- populate it with sample data.
--
-- USAGE:
--   Option A (MySQL Workbench): File > Run SQL Script > select this file
--   Option B (Terminal):
--       mysql -u root -p < student_data.sql
-- ============================================================

-- Create and select the database
CREATE DATABASE IF NOT EXISTS studentdb;
USE studentdb;

-- Drop the table if it already exists (clean start)
DROP TABLE IF EXISTS sdata;

-- Create the students table
CREATE TABLE sdata (
    student_id  VARCHAR(10)   NOT NULL PRIMARY KEY,
    first_name  VARCHAR(50)   NOT NULL,
    last_name   VARCHAR(50)   NOT NULL,
    major       VARCHAR(50)   NOT NULL,
    phone       VARCHAR(15),
    gpa         DECIMAL(4, 2) CHECK (gpa >= 0.0 AND gpa <= 10.0),
    dob         DATE
);

-- Insert sample student records
INSERT INTO sdata (student_id, first_name, last_name, major, phone, gpa, dob) VALUES
('S001', 'Sunnyth',   'Sheelam',    'AIML',  '9876543210', 9.2,  '2003-05-15'),
('S002', 'Sankar',    'Nagarapu',   'EEE',   '9398555299', 7.0,  '2002-06-24'),
('S003', 'Dharani',   'Nagarapu',   'CSE',   '9133401412', 9.0,  '2004-06-05'),
('S004', 'Sindhu',    'Makarapu',   'CSE',   '9133505025', 10.0, '2004-06-25'),
('S005', 'Sneha',     'Kurma',      'CSE',   '9548625756', 9.5,  '2004-06-27'),
('S006', 'Meghana',   'Reddy',      'ECE',   '9876512345', 8.8,  '2003-11-10'),
('S007', 'Arjun',     'Verma',      'MECH',  '9012345678', 7.5,  '2002-03-22'),
('S008', 'Priya',     'Sharma',     'AIML',  '9988776655', 9.8,  '2004-01-18'),
('S009', 'Rahul',     'Singh',      'IT',    '9123456789', 8.2,  '2003-07-30'),
('S010', 'Ananya',    'Patel',      'CSE',   '9654321098', 9.1,  '2004-09-05');

-- Confirm setup
SELECT CONCAT('Database setup complete. Total students: ', COUNT(*)) AS status
FROM sdata;
