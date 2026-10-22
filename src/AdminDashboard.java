import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class AdminDashboard extends JFrame {

    private final Color NAVY = new Color(23, 43, 77);
    private final Color TEAL = new Color(15, 157, 145);
    private final Color LIGHT_BG = new Color(245, 247, 250);
    private final Color TEXT_COLOR = new Color(45, 55, 72);
    private final Color BORDER_COLOR = new Color(220, 226, 235);
    private final Color WHITE = Color.WHITE;
    private final Color RED = new Color(190, 60, 65);
    private final Color GREY = new Color(90, 105, 125);

    private String role;
    private String username;

    private JPanel contentPanel;

    private JTable eventTable;
    private DefaultTableModel eventModel;

    private JTable registrationTable;
    private DefaultTableModel registrationModel;

    private JTable venueTable;
    private DefaultTableModel venueModel;

    private JTextField nameField;
    private JComboBox<String> categoryBox;
    private JTextArea descriptionArea;
    private JTextField dateField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JComboBox<String> venueBox;
    private JTextField feeField;
    private JTextField prizesField;
    private JTextField paymentLocationField;
    private JTextField paymentHoursField;
    private JTextField paymentDeadlineField;

    private int selectedEventId = -1;

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TEXT_COLOR);
        table.setBackground(WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(214, 239, 235));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(
                new Dimension(0, 36)
        );
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(WHITE);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEAL);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)
        ));
    }

    private void styleHeading(JLabel heading) {
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(NAVY);
        heading.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );
    }

    public AdminDashboard(String role, String username) {

        this.role = role;
        this.username = username;

        setTitle("College Event Management System - Admin");
        setSize(1200, 700);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(
                BorderFactory.createEmptyBorder(18, 24, 18, 24)
        );

        JLabel titleLabel = new JLabel(
                "College Event Management System"
        );
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel userLabel = new JLabel(
                "Welcome, " + username + "   "
        );
        userLabel.setForeground(WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        header.add(titleLabel, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Sidebar with 5 navigation buttons
        JPanel sidebar = new JPanel(new GridLayout(5, 1, 5, 12));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(LIGHT_BG);
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(20, 12, 20, 12)
        );

        JButton dashboardButton = new JButton("Dashboard");
        JButton manageEventsButton = new JButton("Manage Events");
        JButton manageVenuesButton = new JButton("Manage Venues");
        JButton registrationsButton = new JButton("Registrations");
        JButton logoutButton = new JButton("Logout");

        styleButton(dashboardButton, NAVY);
        styleButton(manageEventsButton, TEAL);
        styleButton(manageVenuesButton, NAVY);
        styleButton(registrationsButton, NAVY);
        styleButton(logoutButton, RED);

        sidebar.add(dashboardButton);
        sidebar.add(manageEventsButton);
        sidebar.add(manageVenuesButton);
        sidebar.add(registrationsButton);
        sidebar.add(logoutButton);

        add(sidebar, BorderLayout.WEST);

        // Main content area
        contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
        );

        add(contentPanel, BorderLayout.CENTER);

        dashboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDashboard();
            }
        });

        manageEventsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showManageEvents();
            }
        });

        manageVenuesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showManageVenues();
            }
        });

        registrationsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRegistrations();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int choice = JOptionPane.showConfirmDialog(
                        AdminDashboard.this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    new Main();
                }
            }
        });

        showDashboard();
        setVisible(true);
    }

    private String formatDate(java.sql.Date date) {
        if (date == null) {
            return "";
        }

        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    private java.sql.Date parseDate(String dateText)
            throws ParseException {

        if (dateText == null) {
            throw new ParseException("Date cannot be null", 0);
        }

        String trimmed = dateText.trim();
        String[] patterns = {"dd-MM-yyyy", "yyyy-MM-dd", "dd/MM/yyyy"};

        for (String pattern : patterns) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                formatter.setLenient(false);
                java.util.Date parsedDate = formatter.parse(trimmed);
                return new java.sql.Date(parsedDate.getTime());
            } catch (ParseException ignored) {}
        }

        throw new ParseException("Invalid date format. Expected DD-MM-YYYY or YYYY-MM-DD", 0);
    }

    // Helper to parse time strings like "10:00 AM", "10:00", or "14:30" into java.sql.Time
    private java.sql.Time parseTime(String timeText) throws ParseException {
        timeText = timeText.trim().toUpperCase();
        String[] patterns = {"hh:mm a", "h:mm a", "HH:mm:ss", "HH:mm"};
        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                java.util.Date parsed = sdf.parse(timeText);
                return new java.sql.Time(parsed.getTime());
            } catch (ParseException ignored) {
            }
        }
        throw new ParseException("Invalid time format: " + timeText, 0);
    }

    // Checks whether the venue has reached its maximum concurrent event limit
    private boolean checkVenueConflict(String venue, java.sql.Date eventDate, 
                                       java.sql.Time startTime, java.sql.Time endTime, 
                                       int currentEventId, Component parent) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();

            // 1. Get venue's max concurrent events limit
            int maxAllowed = 1;
            String limitSql = "SELECT max_concurrent_events FROM venues WHERE venue_name = ?";
            ps = con.prepareStatement(limitSql);
            ps.setString(1, venue);
            rs = ps.executeQuery();
            if (rs.next()) {
                maxAllowed = rs.getInt("max_concurrent_events");
            }
            rs.close();
            ps.close();

            // 2. Count overlapping events at the same venue on the same date
            // Exclude currentEventId when updating an already scheduled event
            String overlapSql = "SELECT COUNT(*) AS overlap_count FROM events "
                    + "WHERE venue = ? AND event_date = ? AND event_id != ? "
                    + "AND (start_time < ? AND end_time > ?)";

            ps = con.prepareStatement(overlapSql);
            ps.setString(1, venue);
            ps.setDate(2, eventDate);
            ps.setInt(3, currentEventId);
            ps.setTime(4, endTime);
            ps.setTime(5, startTime);

            rs = ps.executeQuery();
            int currentOverlaps = 0;
            if (rs.next()) {
                currentOverlaps = rs.getInt("overlap_count");
            }

            // 3. Enforce limit
            if (currentOverlaps >= maxAllowed) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Scheduling Conflict!\n\n"
                        + "Venue: \"" + venue + "\"\n"
                        + "Maximum simultaneous events allowed: " + maxAllowed + "\n"
                        + "Already scheduled active events in this time slot: " + currentOverlaps + "\n\n"
                        + "Please choose a different time or venue.",
                        "Venue Capacity Exceeded",
                        JOptionPane.ERROR_MESSAGE
                );
                return false; // Conflict found, block booking
            }

            return true; // OK to schedule

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Database error while checking venue availability:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {}
        }
    }

    // Loads all registered venues from the database into the venue dropdown
    private void loadVenueOptions(JComboBox<String> box) {
        box.removeAllItems();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("SELECT venue_name FROM venues ORDER BY venue_name");
            rs = ps.executeQuery();
            while (rs.next()) {
                box.addItem(rs.getString("venue_name"));
            }
        } catch (SQLException ex) {
            // Fallback default venues if table query fails
            box.addItem("College Auditorium");
            box.addItem("Main Block");
            box.addItem("College Ground");
            box.addItem("Computer Lab 2");
            box.addItem("CSE Lab");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {}
        }
    }

    private void showDashboard() {

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(15, 15));

        JLabel heading = new JLabel("Admin Dashboard");
        styleHeading(heading);

        JLabel welcome = new JLabel(
                "<html><div style='width:520px;'>"
                + "<h2 style='color:#172B4D;'>Welcome to your dashboard!</h2>"
                + "<p style='font-size:14px;color:#2D3748;'>"
                + "Manage college events, configure venue concurrency limits, view student registration requests, "
                + "and confirm offline payments after receiving them."
                + "</p>"
                + "<p style='font-size:13px;color:#0F9D91;'>"
                + "Use the navigation panel to get started."
                + "</p></div></html>"
        );

        welcome.setFont(new Font("Arial", Font.PLAIN, 16));
        welcome.setForeground(TEXT_COLOR);
        welcome.setVerticalAlignment(SwingConstants.TOP);
        welcome.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 10, 10)
        );

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        welcomePanel.add(welcome, BorderLayout.NORTH);

        contentPanel.add(heading, BorderLayout.NORTH);
        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageEvents() {

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(12, 12));

        JLabel heading = new JLabel("Manage Events");
        styleHeading(heading);

        contentPanel.add(heading, BorderLayout.NORTH);

        String[] columns = {
            "ID", "Event Name", "Category", "Event Date",
            "Time", "Venue", "Fee", "Payment Venue",
            "Payment Hours", "Payment Deadline"
        };

        eventModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventTable = new JTable(eventModel);
        styleTable(eventTable);
        eventTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {
            50, 170, 110, 110, 100, 150, 80, 170, 145, 135
        };

        for (int i = 0; i < widths.length; i++) {
            eventTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER_COLOR)
        );

        JButton addButton = new JButton("Add Event");
        JButton editButton = new JButton("Edit Selected");
        JButton viewButton = new JButton("View Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        styleButton(addButton, TEAL);
        styleButton(editButton, NAVY);
        styleButton(viewButton, NAVY);
        styleButton(deleteButton, RED);
        styleButton(refreshButton, GREY);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        buttonPanel.setBackground(LIGHT_BG);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEventForm(false);
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSelectedEventForEditing();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewSelectedEvent();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedEvent();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadEvents();
            }
        });

        loadEvents();

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadEvents() {

        eventModel.setRowCount(0);

        String sql =
                "SELECT event_id, event_name, category, event_date, "
                + "event_time, venue, fee, payment_location, "
                + "payment_hours, payment_deadline "
                + "FROM events ORDER BY event_id";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("event_id"),
                    rs.getString("event_name"),
                    rs.getString("category"),
                    formatDate(rs.getDate("event_date")),
                    rs.getString("event_time"),
                    rs.getString("venue"),
                    rs.getBigDecimal("fee"),
                    rs.getString("payment_location"),
                    rs.getString("payment_hours"),
                    formatDate(rs.getDate("payment_deadline"))
                };

                eventModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load events:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error closing database connection."
                );
            }
        }
    }

    private void showEventForm(boolean editing) {

        final JDialog dialog = new JDialog(
                this,
                editing ? "Edit Event" : "Add Event",
                true
        );

        dialog.setSize(580, 700);
        dialog.setMinimumSize(new Dimension(520, 620));
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(LIGHT_BG);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        nameField = new JTextField(20);
        styleTextField(nameField);

        categoryBox = new JComboBox<String>(new String[] {
            "Technical", "Cultural", "Sports", "Workshop", "Other"
        });
        categoryBox.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryBox.setBackground(WHITE);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setForeground(TEXT_COLOR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        );

        dateField = new JTextField(20);
        styleTextField(dateField);

        startTimeField = new JTextField(20);
        styleTextField(startTimeField);

        endTimeField = new JTextField(20);
        styleTextField(endTimeField);

        venueBox = new JComboBox<String>();
        venueBox.setFont(new Font("Arial", Font.PLAIN, 14));
        venueBox.setBackground(WHITE);
        loadVenueOptions(venueBox);

        feeField = new JTextField(20);
        styleTextField(feeField);

        prizesField = new JTextField(20);
        styleTextField(prizesField);

        paymentLocationField = new JTextField(20);
        styleTextField(paymentLocationField);

        paymentHoursField = new JTextField(20);
        styleTextField(paymentHoursField);

        paymentDeadlineField = new JTextField(20);
        styleTextField(paymentDeadlineField);

        int row = 0;

        addFormRow(formPanel, gbc, row++, "Event Name:", nameField);
        addFormRow(formPanel, gbc, row++, "Category:", categoryBox);

        addFormRow(
                formPanel, gbc, row++, "Description:",
                new JScrollPane(descriptionArea)
        );

        addFormRow(
                formPanel, gbc, row++, "Event Date (DD-MM-YYYY):",
                dateField
        );

        addFormRow(formPanel, gbc, row++, "Start Time (e.g. 10:00 AM):", startTimeField);
        addFormRow(formPanel, gbc, row++, "End Time (e.g. 01:00 PM):", endTimeField);
        addFormRow(formPanel, gbc, row++, "Event Venue:", venueBox);
        addFormRow(formPanel, gbc, row++, "Fee:", feeField);
        addFormRow(formPanel, gbc, row++, "Prizes:", prizesField);

        addFormRow(
                formPanel, gbc, row++, "Payment Venue:",
                paymentLocationField
        );

        addFormRow(
                formPanel, gbc, row++, "Payment Hours:",
                paymentHoursField
        );

        addFormRow(
                formPanel, gbc, row++,
                "Payment Deadline (DD-MM-YYYY):",
                paymentDeadlineField
        );

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        dialog.add(formScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottomPanel.setBackground(LIGHT_BG);
        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 5, 10)
        );

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton, TEAL);
        styleButton(cancelButton, GREY);

        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);

        dialog.add(bottomPanel, BorderLayout.SOUTH);

        if (editing) {
            fillFormFromSelectedRow();
        } else {
            nameField.setText("");
            categoryBox.setSelectedIndex(0);
            descriptionArea.setText("");
            dateField.setText("");
            startTimeField.setText("");
            endTimeField.setText("");
            if (venueBox.getItemCount() > 0) venueBox.setSelectedIndex(0);
            feeField.setText("0");
            prizesField.setText("");
            paymentLocationField.setText("");
            paymentHoursField.setText("");
            paymentDeadlineField.setText("");
        }

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveEvent(editing, dialog);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component component) {

        JLabel formLabel = new JLabel(label);
        formLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formLabel.setForeground(TEXT_COLOR);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.40;
        panel.add(formLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.60;
        panel.add(component, gbc);
    }

    private void loadSelectedEventForEditing() {

        int selectedRow = eventTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an event first."
            );
            return;
        }

        selectedEventId = Integer.parseInt(
                eventModel.getValueAt(selectedRow, 0).toString()
        );

        showEventForm(true);
    }

    private void fillFormFromSelectedRow() {

        String sql = "SELECT * FROM events WHERE event_id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, selectedEventId);
            rs = ps.executeQuery();

            if (rs.next()) {

                nameField.setText(rs.getString("event_name"));
                categoryBox.setSelectedItem(rs.getString("category"));
                descriptionArea.setText(rs.getString("description"));

                dateField.setText(
                        formatDate(rs.getDate("event_date"))
                );

                Time st = rs.getTime("start_time");
                Time et = rs.getTime("end_time");
                startTimeField.setText(st != null ? new SimpleDateFormat("hh:mm a").format(st) : "");
                endTimeField.setText(et != null ? new SimpleDateFormat("hh:mm a").format(et) : "");
                venueBox.setSelectedItem(rs.getString("venue"));

                BigDecimal fee = rs.getBigDecimal("fee");
                feeField.setText(fee == null ? "0" : fee.toString());

                String prizes = rs.getString("prizes");
                prizesField.setText(prizes == null ? "" : prizes);

                paymentLocationField.setText(
                        rs.getString("payment_location")
                );

                paymentHoursField.setText(
                        rs.getString("payment_hours")
                );

                paymentDeadlineField.setText(
                        formatDate(rs.getDate("payment_deadline"))
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load event details:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                // Connection cleanup
            }
        }
    }

    private void saveEvent(boolean editing, JDialog dialog) {

        String eventName = nameField.getText().trim();
        String category = categoryBox.getSelectedItem().toString();
        String description = descriptionArea.getText().trim();
        String eventDateText = dateField.getText().trim();
        String startTimeText = startTimeField.getText().trim();
        String endTimeText = endTimeField.getText().trim();
        String venue = venueBox.getSelectedItem() != null ? venueBox.getSelectedItem().toString().trim() : "";
        String feeText = feeField.getText().trim();
        String prizes = prizesField.getText().trim();
        String paymentLocation = paymentLocationField.getText().trim();
        String paymentHours = paymentHoursField.getText().trim();
        String deadlineText = paymentDeadlineField.getText().trim();

        if (eventName.isEmpty()
                || eventDateText.isEmpty()
                || startTimeText.isEmpty()
                || endTimeText.isEmpty()
                || venue.isEmpty()
                || feeText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    dialog,
                    "Fill in the event name, date, start time, end time, venue and fee."
            );
            return;
        }

        java.sql.Date parsedEventDate;
        java.sql.Time parsedStartTime;
        java.sql.Time parsedEndTime;
        java.sql.Date parsedDeadline = null;
        BigDecimal fee;

                try {
            parsedEventDate = parseDate(eventDateText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Invalid Event Date: \"" + eventDateText + "\"\nPlease use DD-MM-YYYY format, e.g. 25-10-2026.");
            return;
        }

        try {
            parsedStartTime = parseTime(startTimeText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Invalid Start Time: \"" + startTimeText + "\"\nPlease use formats like 10:00 AM or 14:00.");
            return;
        }

        try {
            parsedEndTime = parseTime(endTimeText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Invalid End Time: \"" + endTimeText + "\"\nPlease use formats like 01:00 PM or 16:00.");
            return;
        }

        if (!parsedEndTime.after(parsedStartTime)) {
            JOptionPane.showMessageDialog(
                    dialog,
                    "End time must be after start time.",
                    "Invalid Time",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            String cleanFee = feeText.replaceAll("[^0-9.]", "");
            if (cleanFee.isEmpty()) cleanFee = "0";
            fee = new BigDecimal(cleanFee);
            if (fee.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(dialog, "Fee cannot be negative.");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Invalid Fee amount.");
            return;
        }

        if (!deadlineText.isEmpty()) {
            try {
                parsedDeadline = parseDate(deadlineText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Invalid Payment Deadline: \"" + deadlineText + "\"\n\nPayment Deadline requires a DATE in DD-MM-YYYY format (e.g. 25-10-2026),\nor you can leave it blank.",
                        "Invalid Deadline Date",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        // Check venue concurrency limit before saving
        if (!checkVenueConflict(venue, parsedEventDate, parsedStartTime, parsedEndTime, editing ? selectedEventId : -1, dialog)) {
            return; // Venue limit exceeded! Stop booking.
        }

        // Formatted display string for event_time (maintains compatibility with student views)
        SimpleDateFormat timeDisplay = new SimpleDateFormat("hh:mm a");
        String eventTime = timeDisplay.format(parsedStartTime) + " - " + timeDisplay.format(parsedEndTime);

        String sql;

        if (editing) {
            sql = "UPDATE events SET event_name=?, category=?, "
                    + "description=?, event_date=?, start_time=?, end_time=?, event_time=?, "
                    + "venue=?, fee=?, prizes=?, payment_location=?, "
                    + "payment_hours=?, payment_deadline=? "
                    + "WHERE event_id=?";
        } else {
            sql = "INSERT INTO events (event_name, category, "
                    + "description, event_date, start_time, end_time, event_time, venue, "
                    + "fee, prizes, payment_location, payment_hours, "
                    + "payment_deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, eventName);
            ps.setString(2, category);
            ps.setString(3, description);
            ps.setDate(4, parsedEventDate);
            ps.setTime(5, parsedStartTime);
            ps.setTime(6, parsedEndTime);
            ps.setString(7, eventTime);
            ps.setString(8, venue);
            ps.setBigDecimal(9, fee);
            ps.setString(10, prizes.isEmpty() ? "Exciting Prizes & Certificates" : prizes);
            ps.setString(11, paymentLocation);
            ps.setString(12, paymentHours);

            if (parsedDeadline == null) {
                ps.setNull(13, Types.DATE);
            } else {
                ps.setDate(13, parsedDeadline);
            }

            if (editing) {
                ps.setInt(14, selectedEventId);
            }

            int result = ps.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(
                        dialog,
                        editing
                                ? "Event updated successfully!"
                                : "Event added successfully!"
                );

                dialog.dispose();
                loadEvents();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    dialog,
                    "Unable to save event:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                // Connection cleanup
            }
        }
    }

    private void viewSelectedEvent() {

        int selectedRow = eventTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an event first."
            );
            return;
        }

        int eventId = Integer.parseInt(
                eventModel.getValueAt(selectedRow, 0).toString()
        );

        String sql = "SELECT * FROM events WHERE event_id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, eventId);
            rs = ps.executeQuery();

            if (rs.next()) {

                String details =
                        "Event ID: " + rs.getInt("event_id")
                        + "\nEvent Name: " + rs.getString("event_name")
                        + "\nCategory: " + rs.getString("category")
                        + "\nDescription: " + rs.getString("description")
                        + "\nEvent Date: " + formatDate(rs.getDate("event_date"))
                        + "\nEvent Time: " + rs.getString("event_time")
                        + "\nVenue: " + rs.getString("venue")
                        + "\nFee: " + rs.getBigDecimal("fee")
                        + "\nPrizes: " + (rs.getString("prizes") == null ? "Not specified" : rs.getString("prizes"))
                        + "\nPayment Venue: " + rs.getString("payment_location")
                        + "\nPayment Hours: " + rs.getString("payment_hours")
                        + "\nPayment Deadline: "
                        + formatDate(rs.getDate("payment_deadline"));

                JTextArea textArea = new JTextArea(details, 15, 40);
                textArea.setEditable(false);
                textArea.setFont(new Font("Arial", Font.PLAIN, 14));
                textArea.setForeground(TEXT_COLOR);
                textArea.setBackground(LIGHT_BG);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setBorder(
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                );

                JOptionPane.showMessageDialog(
                        this,
                        new JScrollPane(textArea),
                        "Event Details",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to view event:\n" + ex.getMessage()
            );

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                // Connection cleanup
            }
        }
    }

    private void deleteSelectedEvent() {

        int selectedRow = eventTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an event first."
            );
            return;
        }

        int eventId = Integer.parseInt(
                eventModel.getValueAt(selectedRow, 0).toString()
        );

        String eventName =
                eventModel.getValueAt(selectedRow, 1).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete event \"" + eventName + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM events WHERE event_id = ?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, eventId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Event deleted successfully."
            );

            loadEvents();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete event. It may have registrations.\n"
                    + ex.getMessage()
            );

        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                // Connection cleanup
            }
        }
    }

    private void showManageVenues() {

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(12, 12));

        JLabel heading = new JLabel("Manage Venues & Limits");
        styleHeading(heading);

        JLabel subtitle = new JLabel(
                "Configure the maximum number of simultaneous events permitted at each venue."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(GREY);

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        topPanel.setBackground(LIGHT_BG);
        topPanel.add(heading);
        topPanel.add(subtitle);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {
            "Venue ID", "Venue Name", "Max Concurrent Events"
        };

        venueModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        venueTable = new JTable(venueModel);
        styleTable(venueTable);

        int[] widths = {80, 400, 220};
        for (int i = 0; i < widths.length; i++) {
            venueTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(venueTable);
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JButton addVenueButton = new JButton("Add Venue");
        JButton editLimitButton = new JButton("Edit Concurrency Limit");
        JButton refreshButton = new JButton("Refresh");

        styleButton(addVenueButton, TEAL);
        styleButton(editLimitButton, NAVY);
        styleButton(refreshButton, GREY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttonPanel.setBackground(LIGHT_BG);
        buttonPanel.add(addVenueButton);
        buttonPanel.add(editLimitButton);
        buttonPanel.add(refreshButton);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        addVenueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddVenueDialog();
            }
        });

        editLimitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEditVenueLimitDialog();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadVenues();
            }
        });

        loadVenues();

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadVenues() {
        if (venueModel == null) return;
        venueModel.setRowCount(0);

        String sql = "SELECT venue_id, venue_name, max_concurrent_events FROM venues ORDER BY venue_id";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("venue_id"),
                    rs.getString("venue_name"),
                    rs.getInt("max_concurrent_events")
                };
                venueModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load venues:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {}
        }
    }

    private void showAddVenueDialog() {
        JTextField nameInput = new JTextField(20);
        styleTextField(nameInput);
        JTextField limitInput = new JTextField("1", 20);
        styleTextField(limitInput);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Venue Name:"));
        panel.add(nameInput);
        panel.add(new JLabel("Max Concurrent Events:"));
        panel.add(limitInput);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Venue",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameInput.getText().trim();
            String limitText = limitInput.getText().trim();

            if (name.isEmpty() || limitText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both venue name and capacity limit.");
                return;
            }

            int limit;
            try {
                limit = Integer.parseInt(limitText);
                if (limit < 1) {
                    JOptionPane.showMessageDialog(this, "Limit must be at least 1.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Concurrency limit must be a valid positive integer.");
                return;
            }

            String sql = "INSERT INTO venues (venue_name, max_concurrent_events) VALUES (?, ?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setInt(2, limit);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Venue \"" + name + "\" added successfully!");
                loadVenues();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error adding venue (it may already exist):\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showEditVenueLimitDialog() {
        int selectedRow = venueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a venue from the table first.");
            return;
        }

        int venueId = (int) venueModel.getValueAt(selectedRow, 0);
        String venueName = venueModel.getValueAt(selectedRow, 1).toString();
        int currentLimit = (int) venueModel.getValueAt(selectedRow, 2);

        String newLimitStr = JOptionPane.showInputDialog(
                this,
                "Enter new maximum concurrent events limit for:\n\"" + venueName + "\"",
                currentLimit
        );

        if (newLimitStr == null || newLimitStr.trim().isEmpty()) {
            return; // Cancelled
        }

        int newLimit;
        try {
            newLimit = Integer.parseInt(newLimitStr.trim());
            if (newLimit < 1) {
                JOptionPane.showMessageDialog(this, "Limit must be at least 1.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive whole number.");
            return;
        }

        String sql = "UPDATE venues SET max_concurrent_events = ? WHERE venue_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newLimit);
            ps.setInt(2, venueId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Concurrency limit for \"" + venueName + "\" updated to " + newLimit + "!");
            loadVenues();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error updating venue limit:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showRegistrations() {

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(12, 12));

        JLabel heading =
                new JLabel("Student Registration Requests");
        styleHeading(heading);

        contentPanel.add(heading, BorderLayout.NORTH);

        String[] columns = {
            "Registration ID", "Student ID", "Student Name",
            "Event Name", "Registration Date", "Payment Status"
        };

        registrationModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        registrationTable = new JTable(registrationModel);
        styleTable(registrationTable);

        JButton confirmButton = new JButton("Confirm Payment");
        JButton refreshButton = new JButton("Refresh Registrations");

        styleButton(confirmButton, TEAL);
        styleButton(refreshButton, NAVY);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttonPanel.setBackground(LIGHT_BG);

        buttonPanel.add(confirmButton);
        buttonPanel.add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(registrationTable);
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER_COLOR)
        );

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmSelectedPayment();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadRegistrations();
            }
        });

        loadRegistrations();

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadRegistrations() {

        registrationModel.setRowCount(0);

        String sql =
                "SELECT r.registration_id, r.student_id, s.full_name, "
                + "e.event_name, r.registration_date, r.payment_status "
                + "FROM registrations r "
                + "JOIN students s ON r.student_id = s.student_id "
                + "JOIN events e ON r.event_id = e.event_id "
                + "ORDER BY r.registration_id DESC";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("registration_id"),
                    rs.getInt("student_id"),
                    rs.getString("full_name"),
                    rs.getString("event_name"),
                    formatDate(rs.getDate("registration_date")),
                    rs.getString("payment_status")
                };

                registrationModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load registrations:\n" + ex.getMessage()
            );

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                // Connection cleanup
            }
        }
    }

    private void confirmSelectedPayment() {

        int selectedRow = registrationTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a registration first."
            );
            return;
        }

        int registrationId = Integer.parseInt(
                registrationModel.getValueAt(selectedRow, 0).toString()
        );

        String currentStatus =
                registrationModel.getValueAt(selectedRow, 5).toString();

        if ("Confirmed".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Payment has already been confirmed."
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Have you received the offline payment for this registration?",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "UPDATE registrations SET payment_status = 'Confirmed' "
                + "WHERE registration_id = ?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, registrationId);

            int result = ps.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Payment confirmed successfully!\n"
                        + "The registration status has been updated."
                );

                loadRegistrations();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Registration not found."
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to confirm payment:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                // Connection cleanup
            }
        }
    }
}

