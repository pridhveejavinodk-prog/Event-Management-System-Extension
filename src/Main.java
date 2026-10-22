import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends JFrame {

    private Color navy = new Color(23, 43, 77);
    private Color teal = new Color(15, 157, 145);
    private Color background = new Color(245, 247, 250);
    private Color textColor = new Color(45, 55, 72);

    public Main() {
        setTitle("College Event Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBackground(background);

        JLabel title = new JLabel(
                "College Event Management System",
                JLabel.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(navy);
        title.setBorder(BorderFactory.createEmptyBorder(22, 10, 22, 10));

        JLabel subtitle = new JLabel(
                "Sign in to continue",
                JLabel.CENTER
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(textColor);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel roleLabel = new JLabel("Select Role");
        String[] roles = {"Admin", "Student"};
        JComboBox<String> roleBox = new JComboBox<String>(roles);

        JLabel userLabel = new JLabel("Username");
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(300, 42));
        usernameField.setEditable(true);
        usernameField.setFocusable(true);

        JLabel passLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(300, 42));
        passwordField.setEditable(true);
        passwordField.setFocusable(true);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(teal);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        loginButton.setOpaque(true);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        roleLabel.setFont(labelFont);
        userLabel.setFont(labelFont);
        passLabel.setFont(labelFont);

        roleLabel.setForeground(textColor);
        userLabel.setForeground(textColor);
        passLabel.setForeground(textColor);

        roleBox.setFont(new Font("Arial", Font.PLAIN, 15));
        roleBox.setBackground(Color.WHITE);
        roleBox.setPreferredSize(new Dimension(300, 38));
        roleBox.setFocusable(true);

        usernameField.setBackground(Color.WHITE);
        passwordField.setBackground(Color.WHITE);

        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        form.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        form.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(passwordField, gbc);

        JPanel formCard = new JPanel(new BorderLayout(10, 12));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        formCard.add(subtitle, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.add(loginButton);

        formCard.add(buttonPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(background);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        centerPanel.add(formCard, BorderLayout.CENTER);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String role = (String) roleBox.getSelectedItem();
                String username = usernameField.getText().trim();
                String password =
                        new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            Main.this,
                            "Please enter both username and password.",
                            "Missing Information",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                if ("Admin".equals(role)) {

                    if (username.equals("admin")
                            && password.equals("admin123")) {

                        JOptionPane.showMessageDialog(
                                Main.this,
                                "Admin login successful!"
                        );

                        dispose();

                        new AdminDashboard(
                                "Administrator",
                                username
                        );

                    } else {
                        showLoginError();
                    }

                } else if ("Student".equals(role)) {

                    String sql =
                            "SELECT student_id, full_name, department, semester, password " +
                            "FROM students WHERE username = ?";

                    try (Connection connection =
                                 DBConnection.getConnection();
                         PreparedStatement statement =
                                 connection.prepareStatement(sql)) {

                        statement.setString(1, username);

                        try (ResultSet result =
                                     statement.executeQuery()) {

                            if (result.next()) {
                                String dbPass = result.getString("password");
                                boolean valid = (dbPass != null && dbPass.equals(password))
                                        || isValidStudentPassword(username, password);

                                if (!valid) {
                                    showLoginError();
                                    return;
                                }

                                String studentId =
                                        result.getString("student_id");

                                String name =
                                        result.getString("full_name");

                                String department =
                                        result.getString("department");

                                String semester =
                                        result.getString("semester");

                                if (name == null || studentId == null) {
                                    JOptionPane.showMessageDialog(
                                            Main.this,
                                            "Student information is incomplete."
                                    );
                                    return;
                                }

                                dispose();

                                new StudentDashboard(
                                        name,
                                        studentId,
                                        department == null
                                                ? "Not specified"
                                                : department,
                                        semester == null
                                                ? "Not specified"
                                                : semester
                                );

                            } else if (isValidStudentPassword(username, password)) {
                                String defaultName = getStudentDefaultName(username);
                                String insertSql = "INSERT INTO students (username, password, full_name, department, semester) VALUES (?, ?, ?, ?, ?)";
                                try (PreparedStatement ins = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                                    ins.setString(1, username);
                                    ins.setString(2, password);
                                    ins.setString(3, defaultName);
                                    ins.setString(4, "Computer Science & Engineering");
                                    ins.setString(5, "S3");
                                    ins.executeUpdate();
                                    try (ResultSet gk = ins.getGeneratedKeys()) {
                                        String studentId = gk.next() ? String.valueOf(gk.getInt(1)) : "1";
                                        dispose();
                                        new StudentDashboard(
                                                defaultName,
                                                studentId,
                                                "Computer Science & Engineering",
                                                "S3"
                                        );
                                    }
                                }
                            } else {
                                showLoginError();
                            }
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(
                                Main.this,
                                "Could not connect to database or retrieve student information.\n" +
                                "Please ensure MySQL is running and db.properties has valid credentials.\n\n" +
                                ex.getMessage(),
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                } else {
                    showLoginError();
                }
            }
        });

        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }

    private String getStudentDefaultName(String username) {
        if ("student1".equals(username)) return "DEVANARAYANAN";
        if ("student2".equals(username)) return "ASHIKH S";
        if ("student3".equals(username)) return "PRIDHVEEJA VINOD K";
        if ("student4".equals(username)) return "AL AMEEN";
        return "Student";
    }

    private boolean isValidStudentPassword(
            String username,
            String password) {

        if ("student1".equals(username)) {
            return "devan123".equals(password);
        }

        if ("student2".equals(username)) {
            return "ashikh123".equals(password);
        }

        if ("student3".equals(username)) {
            return "pridhveeja123".equals(password);
        }

        if ("student4".equals(username)) {
            return "alameen123".equals(password);
        }

        return false;
    }

    private void showLoginError() {
        JOptionPane.showMessageDialog(
                this,
                "Invalid username or password!",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}