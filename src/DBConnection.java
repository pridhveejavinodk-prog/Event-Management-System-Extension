import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

public class DBConnection {
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        InputStream is = null;
        try {
            File localFile = new File("db.properties");
            if (localFile.exists()) {
                is = new FileInputStream(localFile);
            } else {
                is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
                if (is == null) {
                    is = DBConnection.class.getResourceAsStream("/db.properties");
                }
            }

            if (is != null) {
                // Read properties with UTF-8 encoding and strip BOM if present
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                Properties rawProps = new Properties();
                rawProps.load(reader);
                for (String key : rawProps.stringPropertyNames()) {
                    String cleanKey = key.replace("\uFEFF", "").trim();
                    String cleanVal = rawProps.getProperty(key).trim();
                    properties.setProperty(cleanKey, cleanVal);
                }
                reader.close();
            } else {
                // Default fallback configuration
                properties.setProperty("db.url", "jdbc:mysql://localhost:3306/college_event_db?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false");
                properties.setProperty("db.user", "root");
                properties.setProperty("db.password", "");
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read db.properties file: " + e.getMessage());
            properties.setProperty("db.url", "jdbc:mysql://localhost:3306/college_event_db?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false");
            properties.setProperty("db.user", "root");
            properties.setProperty("db.password", "");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {}
            }
        }
    }

    private static boolean initialized = false;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {}
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        if (url == null || user == null) {
            throw new SQLException("Database configuration is incomplete. Check db.properties.");
        }
        if (password == null) {
            password = "";
        }

        Connection con = DriverManager.getConnection(url, user, password);

        // Auto-initialize schema and default seed data on first successful connection
        if (!initialized) {
            synchronized (DBConnection.class) {
                if (!initialized) {
                    initDatabase(con);
                    initialized = true;
                }
            }
        }

        return con;
    }

    private static void initDatabase(Connection con) {
        try (Statement stmt = con.createStatement()) {
            // 1. Create students table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS students (" +
                "  student_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  username VARCHAR(50) NOT NULL UNIQUE," +
                "  password VARCHAR(100) NOT NULL," +
                "  full_name VARCHAR(100) NOT NULL," +
                "  department VARCHAR(100) DEFAULT 'Computer Science & Engineering'," +
                "  semester VARCHAR(20) DEFAULT 'S3'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );

            // 2. Create events table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS events (" +
                "  event_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  event_name VARCHAR(150) NOT NULL," +
                "  category VARCHAR(50) NOT NULL," +
                "  description TEXT," +
                "  event_date DATE NOT NULL," +
                "  start_time TIME DEFAULT NULL," +
                "  end_time TIME DEFAULT NULL," +
                "  event_time VARCHAR(50) NOT NULL," +
                "  venue VARCHAR(100) NOT NULL," +
                "  fee DECIMAL(10,2) DEFAULT 0.00," +
                "  prizes VARCHAR(255) DEFAULT 'Exciting Cash Prizes & Certificates'," +
                "  payment_location VARCHAR(100) DEFAULT 'Admin Office'," +
                "  payment_hours VARCHAR(100) DEFAULT '10:00 AM - 3:00 PM'," +
                "  payment_deadline DATE DEFAULT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );

            // Ensure start_time and end_time columns exist if events table already existed
            try {
                stmt.executeUpdate("ALTER TABLE events ADD COLUMN start_time TIME AFTER event_date");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE events ADD COLUMN end_time TIME AFTER start_time");
            } catch (SQLException ignored) {}

            // 3. Create venues table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS venues (" +
                "  venue_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  venue_name VARCHAR(100) NOT NULL UNIQUE," +
                "  max_concurrent_events INT NOT NULL DEFAULT 1" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );

            // Seed default venues if empty
            try (ResultSet rsVenueCount = stmt.executeQuery("SELECT COUNT(*) FROM venues")) {
                if (rsVenueCount.next() && rsVenueCount.getInt(1) == 0) {
                    stmt.executeUpdate(
                        "INSERT INTO venues (venue_name, max_concurrent_events) VALUES " +
                        "('College Auditorium', 1), " +
                        "('Main Block', 2), " +
                        "('College Ground', 2), " +
                        "('Computer Lab 2', 1), " +
                        "('CSE Lab', 1)"
                    );
                }
            } catch (SQLException ignored) {}

            // 4. Create registrations table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS registrations (" +
                "  registration_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  student_id INT NOT NULL," +
                "  event_id INT NOT NULL," +
                "  payment_status VARCHAR(50) DEFAULT 'Pending'," +
                "  registration_date DATE NOT NULL," +
                "  UNIQUE KEY unique_student_event (student_id, event_id)," +
                "  CONSTRAINT fk_reg_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE," +
                "  CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );

            // Seed default students if empty
            ResultSet rsStudentCount = stmt.executeQuery("SELECT COUNT(*) FROM students");
            if (rsStudentCount.next() && rsStudentCount.getInt(1) == 0) {
                stmt.executeUpdate(
                    "INSERT INTO students (student_id, username, password, full_name, department, semester) VALUES " +
                    "(1, 'student1', 'devan123', 'DEVANARAYANAN', 'Computer Science & Engineering', 'S3'), " +
                    "(2, 'student2', 'ashikh123', 'ASHIKH S', 'Computer Science & Engineering', 'S3'), " +
                    "(3, 'student3', 'pridhveeja123', 'PRIDHVEEJA VINOD K', 'Computer Science & Engineering', 'S3'), " +
                    "(4, 'student4', 'alameen123', 'AL AMEEN', 'Computer Science & Engineering', 'S3')"
                );
            }
            rsStudentCount.close();

            // Seed default events if empty
            ResultSet rsEventCount = stmt.executeQuery("SELECT COUNT(*) FROM events");
            if (rsEventCount.next() && rsEventCount.getInt(1) == 0) {
                stmt.executeUpdate(
                    "INSERT INTO events (event_name, category, description, event_date, event_time, venue, fee, prizes, payment_location, payment_hours, payment_deadline) VALUES " +
                    "('Tech Fest', 'Technical', 'A technical festival featuring coding contests, project exhibitions and technical activities.', '2026-10-25', '10:00 AM - 4:00 PM', 'Main Block', 100.00, 'Cash Prizes and Certificates for Winners', 'Admin Office - Counter 1', '10:00 AM - 3:00 PM', '2026-10-24'), " +
                    "('Arts Fest', 'Cultural', 'A cultural festival featuring music, dance, artistic performances and exciting competitions.', '2026-10-28', '10:00 AM - 4:00 PM', 'College Auditorium', 500.00, 'Trophies and Certificates', 'Cultural Committee Room', '11:00 AM - 4:00 PM', '2026-10-27'), " +
                    "('Football Tournament', 'Sports', 'A college football tournament for participating teams across departments.', '2026-10-30', '9:00 AM - 3:00 PM', 'College Ground', 50.00, 'Trophy, Medals & Certificates', 'Sports Dept Room 12', '9:00 AM - 1:00 PM', '2026-10-29'), " +
                    "('Java Workshop', 'Workshop', 'A practical hands-on workshop introducing Java and Object-Oriented Programming concepts.', '2026-11-02', '10:00 AM - 1:00 PM', 'Computer Lab 2', 100.00, 'Participation Certificates for All', 'CS Department Office', '10:00 AM - 3:00 PM', '2026-11-01'), " +
                    "('Coding Competition', 'Technical', 'A competitive programming contest to test algorithmic problem-solving skills.', '2026-11-05', '10:00 AM - 2:00 PM', 'CSE Lab', 75.00, 'Exciting Cash Prizes for Top 3 Coders', 'Lab Coordinator Desk', '10:00 AM - 2:00 PM', '2026-11-04')"
                );
            }
            rsEventCount.close();

        } catch (SQLException e) {
            System.err.println("Database auto-init notice: " + e.getMessage());
        }
    }
}
