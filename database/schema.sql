-- =========================================================================
-- College Event Management System - Database Schema
-- Compatible with MySQL 8.x / 9.x
-- =========================================================================

CREATE DATABASE IF NOT EXISTS college_event_db;
USE college_event_db;

-- -------------------------------------------------------------------------
-- Table 1: students
-- Represents student users who log in and register for college events.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    department VARCHAR(100) DEFAULT 'Computer Science & Engineering',
    semester VARCHAR(20) DEFAULT 'S3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Table 2: venues
-- Represents campus venues and their maximum concurrent event limits.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS venues (
    venue_id INT AUTO_INCREMENT PRIMARY KEY,
    venue_name VARCHAR(100) NOT NULL UNIQUE,
    max_concurrent_events INT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Table 3: events
-- Represents college events added and managed by the administrator.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    start_time TIME DEFAULT NULL,
    end_time TIME DEFAULT NULL,
    event_time VARCHAR(50) NOT NULL,
    venue VARCHAR(100) NOT NULL,
    fee DECIMAL(10,2) DEFAULT 0.00,
    prizes VARCHAR(255) DEFAULT 'Exciting Cash Prizes & Certificates',
    payment_location VARCHAR(100) DEFAULT 'Admin Office',
    payment_hours VARCHAR(100) DEFAULT '10:00 AM - 3:00 PM',
    payment_deadline DATE DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Table 4: registrations
-- Connects students with events and tracks registration and offline payment status.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    event_id INT NOT NULL,
    payment_status VARCHAR(50) DEFAULT 'Pending',
    registration_date DATE NOT NULL,
    UNIQUE KEY unique_student_event (student_id, event_id),
    CONSTRAINT fk_reg_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
