
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class EventDetailsPage extends JFrame {

    private static final Color NAVY = new Color(23, 37, 84);
    private static final Color TEAL = new Color(13, 148, 136);
    private static final Color TEAL_DARK = new Color(15, 118, 110);
    private static final Color BACKGROUND = new Color(244, 247, 251);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(45, 55, 72);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color GREEN_BG = new Color(220, 252, 231);
    private static final Color GREEN_TEXT = new Color(22, 101, 52);
    private static final Color ORANGE_BG = new Color(255, 237, 213);
    private static final Color ORANGE_TEXT = new Color(154, 52, 18);
    private static final Color GRAY_BG = new Color(241, 245, 249);

    private String status = "Not Registered";
    private JLabel statusLabel;
    private JButton registerButton;

    private String studentId;
    private int eventId;
    private String eventName;
    private String fee;
    private String paymentLocation;
    private String paymentHours;
    private String paymentDeadline;

    public EventDetailsPage(
            String name,
            String description,
            String dateTime,
            String venue,
            String fee,
            String prizes,
            String paymentInfo,
            String studentId,
            int eventId,
            String paymentLocation,
            String paymentHours,
            String paymentDeadline) {

        this.studentId = studentId;
        this.eventId = eventId;
        this.eventName = name;
        this.fee = fee;
        this.paymentLocation = paymentLocation;
        this.paymentHours = paymentHours;
        this.paymentDeadline = paymentDeadline;

        // Always retrieve the latest deadline saved by the admin.
        loadLatestPaymentDeadline();

        setTitle(name + " - Event Details");
        setSize(900, 760);
        setMinimumSize(new Dimension(760, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BACKGROUND);

        // ---------------- HEADER ----------------

        JPanel header = new JPanel(new BorderLayout(10, 8));
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(24, 30, 24, 30));

        JLabel pageLabel = new JLabel("EVENT DETAILS");
        pageLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pageLabel.setForeground(new Color(153, 246, 228));

        JLabel heading = new JLabel(
                name == null ? "Event Details" : name);
        heading.setFont(new Font("Arial", Font.BOLD, 27));
        heading.setForeground(WHITE);

        JLabel subtitle = new JLabel(
                "Explore the event and manage your registration");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(203, 213, 225));

        JPanel headingContent = new JPanel();
        headingContent.setLayout(
                new BoxLayout(headingContent, BoxLayout.Y_AXIS));
        headingContent.setOpaque(false);

        pageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headingContent.add(pageLabel);
        headingContent.add(Box.createVerticalStrut(8));
        headingContent.add(heading);
        headingContent.add(Box.createVerticalStrut(7));
        headingContent.add(subtitle);

        header.add(headingContent, BorderLayout.CENTER);

        // ---------------- EVENT DESCRIPTION ----------------

        JTextArea descriptionArea = new JTextArea(
                description == null || description.trim().isEmpty()
                        ? "No description available."
                        : description);

        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descriptionArea.setForeground(TEXT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel descriptionPanel = createCardPanel();
        descriptionPanel.setLayout(new BorderLayout(0, 12));

        JLabel descriptionTitle = createSectionTitle("About the Event");
        descriptionPanel.add(descriptionTitle, BorderLayout.NORTH);
        descriptionPanel.add(descriptionArea, BorderLayout.CENTER);

        // ---------------- EVENT INFORMATION ----------------

        JPanel info = createCardPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel infoTitle = createSectionTitle("Event Information");
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(infoTitle);
        info.add(Box.createVerticalStrut(12));

        addInfo(info, "Event Name", name);
        addInfo(info, "Date and Time", formatDateTime(dateTime));
        addInfo(info, "Venue", venue);
        addInfo(info, "Registration Fee", fee);
        addInfo(info, "Prizes", prizes);
        addInfo(info, "Payment Venue", paymentLocation);
        addInfo(info, "Payment Hours", paymentHours);
        addInfo(info, "Payment Deadline", formatDate(paymentDeadline));

        // ---------------- CENTER CONTENT ----------------

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(22, 28, 22, 28));

        descriptionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 180));

        info.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 500));

        content.add(descriptionPanel);
        content.add(Box.createVerticalStrut(18));
        content.add(info);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ---------------- REGISTRATION STATUS ----------------

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(WHITE);
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(16, 28, 18, 28)));

        statusLabel = new JLabel("Registration Status: Checking...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(new EmptyBorder(10, 14, 10, 14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45));

        registerButton = createButton(
                "Register for This Event", TEAL_DARK, WHITE);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveRegistration();
            }
        });

        JButton refreshButton = createButton(
                "Refresh Status", WHITE, NAVY);
        refreshButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(10, 18, 10, 18)));

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadLatestPaymentDeadline();
                loadRegistrationStatus();
            }
        });

        JButton closeButton = createButton(
                "Back to Events", WHITE, TEAL_DARK);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(TEAL_DARK, 1, true),
                new EmptyBorder(10, 18, 10, 18)));

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonPanel.add(registerButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        bottom.add(statusLabel);
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(buttonPanel);

        main.add(header, BorderLayout.NORTH);
        main.add(scrollPane, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        setContentPane(main);

        loadRegistrationStatus();

        setVisible(true);
    }

    // ---------------- UI HELPER METHODS ----------------

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)));
        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(NAVY);
        return label;
    }

    private JButton createButton(
            String text, Color background, Color foreground) {

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(background, 1, true),
                new EmptyBorder(10, 18, 10, 18)));

        return button;
    }

    private void updateStatusAppearance(String text) {
        statusLabel.setText(text);

        if (status.equalsIgnoreCase("Confirmed")) {
            statusLabel.setBackground(GREEN_BG);
            statusLabel.setForeground(GREEN_TEXT);
        } else if (status.equalsIgnoreCase("Pending")) {
            statusLabel.setBackground(ORANGE_BG);
            statusLabel.setForeground(ORANGE_TEXT);
        } else {
            statusLabel.setBackground(GRAY_BG);
            statusLabel.setForeground(TEXT);
        }
    }

    private void addInfo(
            JPanel panel, String label, String value) {

        JPanel row = new JPanel(new BorderLayout(15, 5));
        row.setBackground(WHITE);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 65));

        JLabel title = new JLabel(label);
        title.setFont(new Font("Arial", Font.BOLD, 13));
        title.setForeground(MUTED);
        title.setPreferredSize(new Dimension(155, 30));

        String text = value == null || value.trim().isEmpty()
                ? "Not specified" : value;

        JLabel content = new JLabel(
                "<html><div style='width:450px'>"
                + escapeHtml(text)
                + "</div></html>");

        content.setFont(new Font("Arial", Font.PLAIN, 14));
        content.setForeground(TEXT);

        row.add(title, BorderLayout.WEST);
        row.add(content, BorderLayout.CENTER);

        panel.add(row);

        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
    }

    // ---------------- PAYMENT DEADLINE ----------------

    private void loadLatestPaymentDeadline() {

        String sql =
                "SELECT payment_deadline FROM events WHERE event_id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, eventId);

            rs = ps.executeQuery();

            if (rs.next()) {
                java.sql.Date deadline =
                        rs.getDate("payment_deadline");

                if (deadline != null) {
                    paymentDeadline =
                            new SimpleDateFormat("dd-MM-yyyy")
                                    .format(deadline);
                } else {
                    paymentDeadline = "Not specified";
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load the latest payment deadline.\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // ---------------- REGISTRATION STATUS ----------------

    private void loadRegistrationStatus() {

        String sql =
                "SELECT payment_status FROM registrations "
                + "WHERE student_id = ? AND event_id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(studentId));
            ps.setInt(2, eventId);

            rs = ps.executeQuery();

            if (rs.next()) {
                status = rs.getString("payment_status");

                if (status == null || status.trim().isEmpty()) {
                    status = "Pending";
                }

                registerButton.setEnabled(false);

                updateStatusAppearance(
                        "Registration Status: "
                        + getDisplayStatus(status));

            } else {
                status = "Not Registered";
                registerButton.setEnabled(true);

                updateStatusAppearance(
                        "Registration Status: Not Registered");
            }

        } catch (SQLException e) {
            updateStatusAppearance(
                    "Registration Status: Could not load");

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load registration status.\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException e) {
            updateStatusAppearance(
                    "Registration Status: Invalid Student ID");

            JOptionPane.showMessageDialog(
                    this,
                    "The student ID must be a number.",
                    "Student ID Error",
                    JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String getDisplayStatus(String databaseStatus) {

        if (databaseStatus == null
                || databaseStatus.trim().isEmpty()) {
            return "Pending - Waiting for Payment Confirmation";
        }

        if (databaseStatus.equalsIgnoreCase("Confirmed")) {
            return "Confirmed - Payment Approved";
        }

        if (databaseStatus.equalsIgnoreCase("Pending")) {
            return "Pending - Waiting for Payment Confirmation";
        }

        return databaseStatus;
    }

    // ---------------- SAVE REGISTRATION ----------------

    private void saveRegistration() {

        if (studentId == null || studentId.trim().isEmpty()
                || eventId <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Student or event information is missing. "
                    + "Please reopen this event from the Student Dashboard.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadRegistrationStatus();

        if (!status.equals("Not Registered")) {
            JOptionPane.showMessageDialog(
                    this,
                    "You have already registered for this event.\n"
                    + "Current status: " + getDisplayStatus(status),
                    "Already Registered",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String sql =
                "INSERT INTO registrations "
                + "(student_id, event_id, payment_status, registration_date) "
                + "VALUES (?, ?, ?, CURRENT_DATE)";

        Connection con = null;
        PreparedStatement ps = null;

        registerButton.setEnabled(false);

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(studentId));
            ps.setInt(2, eventId);
            ps.setString(3, "Pending");

            ps.executeUpdate();

            status = "Pending";

            updateStatusAppearance(
                    "Registration Status: "
                    + getDisplayStatus(status));

            JOptionPane.showMessageDialog(
                    this,
                    "Your registration request has been submitted!\n\n"
                    + "Event: " + eventName + "\n"
                    + "Registration Fee: " + fee + "\n"
                    + "Payment Venue: " + paymentLocation + "\n"
                    + "Payment Hours: " + paymentHours + "\n"
                    + "Payment Deadline: "
                    + formatDate(paymentDeadline) + "\n\n"
                    + "Please complete the offline payment.\n"
                    + "Your status will remain Pending until the admin "
                    + "confirms the payment.",
                    "Registration Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                loadRegistrationStatus();

                JOptionPane.showMessageDialog(
                        this,
                        "You have already registered for this event.\n"
                        + "Your existing status has been loaded.",
                        "Already Registered",
                        JOptionPane.INFORMATION_MESSAGE);

            } else {
                registerButton.setEnabled(true);

                JOptionPane.showMessageDialog(
                        this,
                        "Could not save your registration.\n"
                        + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            registerButton.setEnabled(true);

            JOptionPane.showMessageDialog(
                    this,
                    "The student ID must be a number.",
                    "Student ID Error",
                    JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // ---------------- DATE FORMATTING ----------------

    private String formatDate(String value) {

        if (value == null || value.trim().isEmpty()
                || value.equalsIgnoreCase("null")
                || value.equalsIgnoreCase("Not specified")) {
            return "Not specified";
        }

        value = value.trim();

        // MySQL DATE format: yyyy-MM-dd
        try {
            SimpleDateFormat input =
                    new SimpleDateFormat("yyyy-MM-dd");
            input.setLenient(false);

            java.util.Date date = input.parse(value);

            return new SimpleDateFormat("dd-MM-yyyy").format(date);

        } catch (ParseException e) {
            // The value might already be in dd-MM-yyyy format.
        }

        // Accept a date already written as dd-MM-yyyy.
        try {
            SimpleDateFormat input =
                    new SimpleDateFormat("dd-MM-yyyy");
            input.setLenient(false);

            java.util.Date date = input.parse(value);

            return new SimpleDateFormat("dd-MM-yyyy").format(date);

        } catch (ParseException e) {
            return value;
        }
    }

    private String formatDateTime(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }

        // Change only the date part, preserving the time.
        if (value.matches("^\\d{4}-\\d{2}-\\d{2}.*")) {
            String datePart = value.substring(0, 10);
            String rest = value.substring(10);

            return formatDate(datePart) + rest;
        }

        return value;
    }

    private String escapeHtml(String text) {

        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}
