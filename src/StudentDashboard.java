import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StudentDashboard extends JFrame {

    private static final Color NAVY = new Color(23, 43, 77);
    private static final Color TEAL = new Color(15, 157, 145);
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(45, 55, 72);
    private static final Color LIGHT_BORDER = new Color(220, 226, 235);

    private ArrayList<Event> allEvents = new ArrayList<Event>();
    private ArrayList<Event> displayedEvents = new ArrayList<Event>();

    private DefaultListModel<String> eventModel =
            new DefaultListModel<String>();

    private JList<String> eventList = new JList<String>(eventModel);

    private JTextField searchField;
    private JLabel eventCountLabel;
    private JLabel statusLabel;

    private String studentName;
    private String studentId;
    private String department;
    private String semester;

    public StudentDashboard(
            String name,
            String id,
            String department,
            String semester) {

        studentName = name;
        studentId = id;
        this.department = department;
        this.semester = semester;

        setTitle("College Event Management - Student Dashboard");
        setSize(900, 680);
        setMinimumSize(new Dimension(750, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(18, 18));
        main.setBackground(BACKGROUND);
        main.setBorder(
                BorderFactory.createEmptyBorder(22, 25, 22, 25)
        );

        // Dashboard heading
        JLabel heading = new JLabel("Student Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 27));
        heading.setForeground(NAVY);

        JLabel subtitle = new JLabel(
                "Explore events and check your registration status."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 110, 125));

        JPanel headingPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        headingPanel.setBackground(BACKGROUND);
        headingPanel.add(heading);
        headingPanel.add(subtitle);

        // Student profile
        JPanel profile = new JPanel(new GridLayout(2, 2, 15, 10));
        profile.setBackground(WHITE);
        profile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BORDER),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));

        profile.add(createProfileLabel("Name: " + safe(studentName)));
        profile.add(createProfileLabel("Student ID: " + safe(studentId)));
        profile.add(createProfileLabel("Department: " + safe(department)));
        profile.add(createProfileLabel("Semester: " + safe(semester)));

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setBackground(BACKGROUND);
        top.add(headingPanel, BorderLayout.NORTH);
        top.add(profile, BorderLayout.CENTER);

        // Search field and buttons
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 15));
        searchField.setToolTipText("Enter event name or category");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton searchButton = createButton("Search", TEAL, WHITE);
        JButton refreshButton = createButton(
                "Refresh Events", NAVY, WHITE
        );

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        JLabel searchLabel = new JLabel("Search Events:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(TEXT);

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel searchButtons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        searchButtons.setBackground(WHITE);
        searchButtons.add(searchButton);
        searchButtons.add(refreshButton);

        searchPanel.add(searchButtons, BorderLayout.EAST);

        // Event list
        eventList.setFont(new Font("Arial", Font.PLAIN, 15));
        eventList.setForeground(TEXT);
        eventList.setBackground(WHITE);
        eventList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        eventList.setFixedCellHeight(38);
        eventList.setSelectionBackground(new Color(220, 243, 240));
        eventList.setSelectionForeground(NAVY);
        eventList.setBorder(
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );

        JScrollPane scrollPane = new JScrollPane(eventList);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
        scrollPane.getViewport().setBackground(WHITE);

        // Event details and status buttons
        JButton detailsButton = createButton(
                "View Details", TEAL, WHITE
        );
        detailsButton.setFont(new Font("Arial", Font.BOLD, 15));

        JButton logoutButton = createButton(
                "Logout", new Color(190, 55, 55), WHITE
        );

        eventCountLabel = new JLabel("Available Events: 0");
        eventCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        eventCountLabel.setForeground(NAVY);

        statusLabel = new JLabel(
                "Registration status is displayed beside each event."
        );
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 110, 125));

        JPanel listHeading = new JPanel(new GridLayout(0, 1, 4, 4));
        listHeading.setBackground(WHITE);
        listHeading.add(eventCountLabel);
        listHeading.add(statusLabel);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBackground(WHITE);
        bottom.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
        bottom.add(listHeading, BorderLayout.CENTER);

        JPanel bottomButtons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        bottomButtons.setBackground(WHITE);
        bottomButtons.add(detailsButton);
        bottomButtons.add(logoutButton);

        bottom.add(bottomButtons, BorderLayout.SOUTH);

        // Event panel
        JPanel eventPanel = new JPanel(new BorderLayout(10, 10));
        eventPanel.setBackground(WHITE);
        eventPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel eventHeading = new JLabel("Events Added by Admin");
        eventHeading.setFont(new Font("Arial", Font.BOLD, 17));
        eventHeading.setForeground(NAVY);

        JPanel eventTop = new JPanel(new BorderLayout(8, 8));
        eventTop.setBackground(WHITE);
        eventTop.add(eventHeading, BorderLayout.NORTH);
        eventTop.add(searchPanel, BorderLayout.CENTER);

        eventPanel.add(eventTop, BorderLayout.NORTH);
        eventPanel.add(scrollPane, BorderLayout.CENTER);
        eventPanel.add(bottom, BorderLayout.SOUTH);

        main.add(top, BorderLayout.NORTH);
        main.add(eventPanel, BorderLayout.CENTER);

        setContentPane(main);

        // Search button
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEvents(searchField.getText());
            }
        });

        // Search when Enter is pressed
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEvents(searchField.getText());
            }
        });

        // Refresh event list from database
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadEvents();
            }
        });

        // Open selected event
        detailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSelectedEvent();
            }
        });

        // Double-click on event list to open details
        eventList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedEvent();
                }
            }
        });

        // Return to login page
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        StudentDashboard.this,
                        "Are you sure you want to logout?",
                        "Confirm Logout",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    new Main();
                }
            }
        });

        loadEvents();
        setVisible(true);
    }

    private JLabel createProfileLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(TEXT);
        return label;
    }

    private JButton createButton(
            String text,
            Color backgroundColor,
            Color foregroundColor) {

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        );

        return button;
    }

    private void loadEvents() {

        allEvents.clear();

        String sql =
                "SELECT e.event_id, e.event_name, e.category, " +
                "e.description, e.event_date, e.event_time, " +
                "e.venue, e.fee, e.prizes, e.payment_location, " +
                "e.payment_hours, e.payment_deadline, " +
                "r.payment_status " +
                "FROM events e " +
                "LEFT JOIN registrations r " +
                "ON e.event_id = r.event_id " +
                "AND r.student_id = ? " +
                "ORDER BY e.event_date, e.event_id";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DBConnection.getConnection();

            statement = connection.prepareStatement(sql);

            // Student IDs in the database are integers.
            statement.setInt(1, Integer.parseInt(studentId));

            result = statement.executeQuery();

            while (result.next()) {

                Event event = new Event();

                event.id = result.getInt("event_id");
                event.name = result.getString("event_name");
                event.category = result.getString("category");
                event.description = result.getString("description");

                Date date = result.getDate("event_date");

                if (date == null) {
                    event.date = "Not specified";
                } else {
                    event.date = new SimpleDateFormat("dd-MM-yyyy")
                            .format(date);
                }

                event.time = result.getString("event_time");
                event.venue = result.getString("venue");

                double fee = result.getDouble("fee");

                if (result.wasNull()) {
                    event.fee = "Not specified";
                } else {
                    event.fee = "Rs. " + String.format("%.2f", fee);
                }

                event.prizes = result.getString("prizes");

                event.paymentLocation =
                        result.getString("payment_location");

                event.paymentHours =
                        result.getString("payment_hours");

                Date deadline = result.getDate("payment_deadline");

                if (deadline == null) {
                    event.paymentDeadline = "";
                } else {
                    event.paymentDeadline =
                            new SimpleDateFormat("dd-MM-yyyy")
                                    .format(deadline);
                }

                // Read the current registration status from MySQL.
                event.registrationStatus =
                        result.getString("payment_status");

                if (event.registrationStatus == null) {
                    event.registrationStatus = "Not Registered";
                }

                allEvents.add(event);
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load events from the database.\n" +
                    "Please check your database connection.\n\n" +
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "The student ID must be a number.",
                    "Student ID Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        showEvents(searchField.getText());
    }

    private void showEvents(String keyword) {

        eventModel.clear();
        displayedEvents.clear();

        String searchText = keyword.trim().toLowerCase();

        for (int i = 0; i < allEvents.size(); i++) {

            Event event = allEvents.get(i);

            String name = event.name == null ? "" : event.name;

            String category =
                    event.category == null ? "" : event.category;

            if (name.toLowerCase().contains(searchText)
                    || category.toLowerCase().contains(searchText)) {

                displayedEvents.add(event);

                String displayText =
                        name + " | Registration: "
                        + event.registrationStatus;

                eventModel.addElement(displayText);
            }
        }

        eventCountLabel.setText(
                "Available Events: " + displayedEvents.size()
        );

        if (displayedEvents.isEmpty()) {
            eventModel.addElement("No events available");
        }
    }

    private void openSelectedEvent() {

        int selectedIndex = eventList.getSelectedIndex();

        if (selectedIndex < 0
                || selectedIndex >= displayedEvents.size()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an event first.",
                    "No Event Selected",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        Event event = displayedEvents.get(selectedIndex);

        String dateTime = event.date + " | " + safe(event.time);

        String prizes = safe(event.prizes);

        String paymentInfo =
                "Payment location: " + safe(event.paymentLocation)
                + " | Payment hours: " + safe(event.paymentHours);

        if (event.paymentDeadline != null
                && !event.paymentDeadline.isEmpty()) {

            paymentInfo += " | Payment deadline: "
                    + event.paymentDeadline;
        }

        // Pass the student ID and event ID to the event details page.
        // The event details page must read the latest status from MySQL.
        EventDetailsPage detailsPage = new EventDetailsPage(
                event.name + " - " + safe(event.category),
                safe(event.description),
                dateTime,
                safe(event.venue),
                event.fee,
                "Prizes: " + prizes,
                paymentInfo,
                studentId,
                event.id,
                safe(event.paymentLocation),
                safe(event.paymentHours),
                safe(event.paymentDeadline)
        );

        detailsPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadEvents();
            }
        });
    }

    private String safe(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }

        return value;
    }

    private class Event {

        int id;
        String name;
        String category;
        String description;
        String date;
        String time;
        String venue;
        String fee;
        String prizes;
        String paymentLocation;
        String paymentHours;
        String paymentDeadline;
        String registrationStatus;
    }
}