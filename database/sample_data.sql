-- =========================================================================
-- College Event Management System - Sample Seed Data
-- =========================================================================

USE college_event_db;

-- -------------------------------------------------------------------------
-- Seed Students
-- -------------------------------------------------------------------------
INSERT INTO students (student_id, username, password, full_name, department, semester) VALUES
(1, 'student1', 'devan123', 'DEVANARAYANAN', 'Computer Science & Engineering', 'S3'),
(2, 'student2', 'ashikh123', 'ASHIKH S', 'Computer Science & Engineering', 'S3'),
(3, 'student3', 'pridhveeja123', 'PRIDHVEEJA VINOD K', 'Computer Science & Engineering', 'S3'),
(4, 'student4', 'alameen123', 'AL AMEEN', 'Computer Science & Engineering', 'S3')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name), password=VALUES(password);

-- -------------------------------------------------------------------------
-- Seed Venues
-- -------------------------------------------------------------------------
INSERT INTO venues (venue_name, max_concurrent_events) VALUES
('College Auditorium', 1),
('Main Block', 2),
('College Ground', 2),
('Computer Lab 2', 1),
('CSE Lab', 1)
ON DUPLICATE KEY UPDATE max_concurrent_events = VALUES(max_concurrent_events);

-- -------------------------------------------------------------------------
-- Seed Events
-- -------------------------------------------------------------------------
INSERT INTO events (event_id, event_name, category, description, event_date, start_time, end_time, event_time, venue, fee, prizes, payment_location, payment_hours, payment_deadline) VALUES
(1, 'Tech Fest', 'Technical', 'A technical festival featuring coding contests, project exhibitions and technical activities.', '2026-10-25', '10:00:00', '16:00:00', '10:00 AM - 4:00 PM', 'Main Block', 100.00, 'Cash Prizes and Certificates for Winners', 'Admin Office - Counter 1', '10:00 AM - 3:00 PM', '2026-10-24'),
(2, 'Arts Fest', 'Cultural', 'A cultural festival featuring music, dance, artistic performances and exciting competitions.', '2026-10-28', '10:00:00', '16:00:00', '10:00 AM - 4:00 PM', 'College Auditorium', 500.00, 'Trophies and Certificates', 'Cultural Committee Room', '11:00 AM - 4:00 PM', '2026-10-27'),
(3, 'Football Tournament', 'Sports', 'A college football tournament for participating teams across departments.', '2026-10-30', '09:00:00', '15:00:00', '9:00 AM - 3:00 PM', 'College Ground', 50.00, 'Trophy, Medals & Certificates', 'Sports Dept Room 12', '9:00 AM - 1:00 PM', '2026-10-29'),
(4, 'Java Workshop', 'Workshop', 'A practical hands-on workshop introducing Java and Object-Oriented Programming concepts.', '2026-11-02', '10:00:00', '13:00:00', '10:00 AM - 1:00 PM', 'Computer Lab 2', 100.00, 'Participation Certificates for All', 'CS Department Office', '10:00 AM - 3:00 PM', '2026-11-01'),
(5, 'Coding Competition', 'Technical', 'A competitive programming contest to test algorithmic problem-solving skills.', '2026-11-05', '10:00:00', '14:00:00', '10:00 AM - 2:00 PM', 'CSE Lab', 75.00, 'Exciting Cash Prizes for Top 3 Coders', 'Lab Coordinator Desk', '10:00 AM - 2:00 PM', '2026-11-04')
ON DUPLICATE KEY UPDATE event_name=VALUES(event_name);
