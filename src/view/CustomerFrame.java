package view;

import controller.VehicleController;
import controller.RentalController;
import controller.LoginController;
import model.Vehicle;
import model.Rental;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Modern Customer Dashboard Frame for the Vehicle Rental System.
 * Enhanced with improved UI, better responsiveness, and professional styling.
 */
public class CustomerFrame extends JFrame {
    private VehicleController vehicleController;
    private RentalController rentalController;
    private LoginController loginController;
    private User currentUser;
    
    private JTable availableTable;
    private JTable rentalTable;
    private JTable historyTable;
    private DefaultTableModel availableTableModel;
    private DefaultTableModel rentalTableModel;
    private DefaultTableModel historyTableModel;
    
    // Stat card labels for real-time updates
    private JLabel carCountLabel;
    private JLabel motorbikeCountLabel;
    private JLabel truckCountLabel;
    private JLabel totalAvailableLabel;
    private JPanel statsPanel;
    
    private int mouseX, mouseY;
    
    // Color scheme - matching AdminFrame
    private static final Color PRIMARY_DARK = new Color(30, 60, 114);
    private static final Color PRIMARY_LIGHT = new Color(42, 82, 152);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color INFO_COLOR = new Color(52, 152, 219);
    private static final Color PURPLE_COLOR = new Color(155, 89, 182);
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color BORDER_COLOR = new Color(220, 225, 230);
    
    public CustomerFrame(VehicleController vehicleController, RentalController rentalController,
                        LoginController loginController, User currentUser) {
        this.vehicleController = vehicleController;
        this.rentalController = rentalController;
        this.loginController = loginController;
        this.currentUser = currentUser;
        
        // Register as listeners for data changes
        vehicleController.addVehicleDataListener(this::onVehicleDataChanged);
        rentalController.addRentalDataListener(this::onRentalDataChanged);
        
        setupUI();
        loadData();
    }
    
    /**
     * Called when vehicle data changes - refresh the UI
     */
    private void onVehicleDataChanged() {
        SwingUtilities.invokeLater(() -> {
            loadAvailableVehicles();
            loadMyRentals();
            refreshStats();
        });
    }
    
    /**
     * Refresh the stats panel with current counts
     */
    private void refreshStats() {
        List<Vehicle> availableVehicles = vehicleController.getAvailableVehicles();
        int totalAvailable = availableVehicles.size();
        
        long carCount = availableVehicles.stream().filter(v -> "Car".equals(v.getVehicleType())).count();
        long motorbikeCount = availableVehicles.stream().filter(v -> "Motorbike".equals(v.getVehicleType())).count();
        long truckCount = availableVehicles.stream().filter(v -> "Truck".equals(v.getVehicleType())).count();
        
        // Update labels
        if (carCountLabel != null) {
            carCountLabel.setText(String.valueOf(carCount));
        }
        if (motorbikeCountLabel != null) {
            motorbikeCountLabel.setText(String.valueOf(motorbikeCount));
        }
        if (truckCountLabel != null) {
            truckCountLabel.setText(String.valueOf(truckCount));
        }
        if (totalAvailableLabel != null) {
            totalAvailableLabel.setText(String.valueOf(totalAvailable));
        }
        
        // Force stats panel to repaint
        if (statsPanel != null) {
            statsPanel.revalidate();
            statsPanel.repaint();
        }
    }
    
    /**
     * Called when rental data changes - refresh the UI
     */
    private void onRentalDataChanged() {
        SwingUtilities.invokeLater(() -> {
            loadMyRentals();
            loadRentalHistory();
            refreshStats();
        });
    }
    
    private void setupUI() {
        setTitle("Customer Dashboard - Vehicle Rental System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(true);
        setUndecorated(true);
        
        // Apply rounded corners
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        } catch (Exception e) {
            // Fallback if shape is not supported
        }
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createLineBorder(PRIMARY_DARK, 2));
        
        // Modern Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane
        JTabbedPane tabbedPane = createStyledTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add drag support
        addDragSupport(headerPanel);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_DARK,
                    getWidth(), 0, PRIMARY_LIGHT
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        // Left side - Welcome with icon
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        welcomePanel.setOpaque(false);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("👤 Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Customer Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        
        textPanel.add(welcomeLabel);
        textPanel.add(subtitleLabel);
        welcomePanel.add(textPanel);
        
        headerPanel.add(welcomePanel, BorderLayout.WEST);
        
        // Right side - Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);
        
        // Minimize button
        JButton minimizeButton = createWindowButton("─", WARNING_COLOR);
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        controlPanel.add(minimizeButton);
        
        // Logout button
        JButton logoutButton = createModernButton("Logout", DANGER_COLOR);
        logoutButton.setPreferredSize(new Dimension(110, 40));
        logoutButton.addActionListener(e -> logout());
        controlPanel.add(logoutButton);
        
        // Close button
        JButton closeButton = createWindowButton("✕", DANGER_COLOR);
        closeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        controlPanel.add(closeButton);
        
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JButton createWindowButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(darkenColor(color, 0.3f));
                } else if (getModel().isRollover()) {
                    g2d.setColor(color);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 100));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(40, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(BORDER_COLOR);
                g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2d.dispose();
            }
        };
        
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CARD_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel availablePanel = createAvailableVehiclesPanel();
        tabbedPane.addTab("  🚗 Available Vehicles  ", availablePanel);
        
        JPanel myRentalsPanel = createMyRentalsPanel();
        tabbedPane.addTab("  📋 My Rentals  ", myRentalsPanel);
        
        JPanel historyPanel = createRentalHistoryPanel();
        tabbedPane.addTab("  📜 History  ", historyPanel);
        
        return tabbedPane;
    }
    
    private void addDragSupport(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
            }
        });
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color currentColor = bgColor;
                if (getModel().isPressed()) {
                    currentColor = darkenColor(bgColor, 0.2f);
                } else if (getModel().isRollover()) {
                    currentColor = brightenColor(bgColor, 0.1f);
                }
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, currentColor,
                    getWidth(), getHeight(), darkenColor(currentColor, 0.1f)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Add subtle shadow
                if (!getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(0, 2, getWidth(), getHeight(), 10, 10);
                }
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private JPanel createAvailableVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Top panel with search and stats
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(BACKGROUND);
        
        // Search panel with improved design
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setBackground(BACKGROUND);
        
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        searchPanel.add(searchLabel);
        
        JTextField searchField = createModernTextField();
        searchField.setPreferredSize(new Dimension(300, 42));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchVehicles(searchField.getText());
                }
            }
        });
        searchPanel.add(searchField);
        
        JButton searchButton = createModernButton("Search", INFO_COLOR);
        searchButton.setPreferredSize(new Dimension(110, 42));
        searchButton.addActionListener(e -> searchVehicles(searchField.getText()));
        searchPanel.add(searchButton);
        
        JButton clearButton = createModernButton("Clear", TEXT_SECONDARY);
        clearButton.setPreferredSize(new Dimension(90, 42));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadAvailableVehicles();
        });
        searchPanel.add(clearButton);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Stats panel
        JPanel statsPanel = createVehicleStatsPanel();
        topPanel.add(statsPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Vehicle Name", "Type", "Price/Day ($)"};
        availableTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        availableTable = new JTable(availableTableModel);
        
        // Hide the ID column (column 0)
        availableTable.getColumnModel().getColumn(0).setMinWidth(0);
        availableTable.getColumnModel().getColumn(0).setMaxWidth(0);
        availableTable.getColumnModel().getColumn(0).setWidth(0);
        styleTable(availableTable);
        
        // Double-click to rent
        availableTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showRentDialog();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(availableTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setBackground(BACKGROUND);
        
        JButton rentButton = createModernButton("🚗 Rent Selected Vehicle", SUCCESS_COLOR);
        rentButton.setPreferredSize(new Dimension(200, 42));
        rentButton.addActionListener(e -> showRentDialog());
        buttonPanel.add(rentButton);
        
        JButton refreshButton = createModernButton("🔄 Refresh", INFO_COLOR);
        refreshButton.setPreferredSize(new Dimension(120, 42));
        refreshButton.addActionListener(e -> loadAvailableVehicles());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createVehicleStatsPanel() {
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        statsPanel.setBackground(BACKGROUND);
        
        List<Vehicle> availableVehicles = vehicleController.getAvailableVehicles();
        int totalAvailable = availableVehicles.size();
        
        long carCount = availableVehicles.stream().filter(v -> "Car".equals(v.getVehicleType())).count();
        long motorbikeCount = availableVehicles.stream().filter(v -> "Motorbike".equals(v.getVehicleType())).count();
        long truckCount = availableVehicles.stream().filter(v -> "Truck".equals(v.getVehicleType())).count();
        
        statsPanel.add(createMiniStatCard("🚗 Cars", String.valueOf(carCount), INFO_COLOR, true));
        statsPanel.add(createMiniStatCard("🏍️ Motorbikes", String.valueOf(motorbikeCount), SUCCESS_COLOR, true));
        statsPanel.add(createMiniStatCard("🚚 Trucks", String.valueOf(truckCount), WARNING_COLOR, true));
        statsPanel.add(createMiniStatCard("📊 Total Available", String.valueOf(totalAvailable), PURPLE_COLOR, true));
        
        return statsPanel;
    }
    
    private JPanel createMiniStatCard(String title, String value, Color color, boolean storeLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        card.setPreferredSize(new Dimension(150, 70));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);
        
        // Store reference to label if requested
        if (storeLabel) {
            if (title.contains("Cars")) {
                carCountLabel = valueLabel;
            } else if (title.contains("Motorbikes")) {
                motorbikeCountLabel = valueLabel;
            } else if (title.contains("Trucks")) {
                truckCountLabel = valueLabel;
            } else if (title.contains("Total")) {
                totalAvailableLabel = valueLabel;
            }
        }
        
        return card;
    }
    
    private JPanel createMyRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Info panel
        JPanel infoPanel = createRentalInfoPanel();
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Table - Updated to show date/time instead of days
        String[] columns = {"Rental ID", "Vehicle", "Start Date/Time", "Expected Return", "Duration", "Status"};
        rentalTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        rentalTable = new JTable(rentalTableModel);
        
        // Hide the Rental ID column (column 0)
        rentalTable.getColumnModel().getColumn(0).setMinWidth(0);
        rentalTable.getColumnModel().getColumn(0).setMaxWidth(0);
        rentalTable.getColumnModel().getColumn(0).setWidth(0);
        styleTable(rentalTable);
        
        // Custom renderer for status
        rentalTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if ("Active".equals(value)) {
                        c.setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(rentalTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setBackground(BACKGROUND);
        
        JButton returnButton = createModernButton("↩️ Return Selected Vehicle", DANGER_COLOR);
        returnButton.setPreferredSize(new Dimension(220, 42));
        returnButton.addActionListener(e -> returnSelectedVehicle());
        buttonPanel.add(returnButton);

        JButton lostButton = createModernButton("🚨 Report Lost", WARNING_COLOR);
        lostButton.setPreferredSize(new Dimension(150, 42));
        lostButton.addActionListener(e -> reportLostVehicle());
        buttonPanel.add(lostButton);

        JButton refreshButton = createModernButton("🔄 Refresh", INFO_COLOR);
        refreshButton.setPreferredSize(new Dimension(120, 42));
        refreshButton.addActionListener(e -> loadMyRentals());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRentalInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(INFO_COLOR.getRed(), INFO_COLOR.getGreen(), INFO_COLOR.getBlue(), 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(INFO_COLOR.getRed(), INFO_COLOR.getGreen(), INFO_COLOR.getBlue(), 100));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2d.dispose();
            }
        };
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel iconLabel = new JLabel("ℹ️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        infoPanel.add(iconLabel, BorderLayout.WEST);
        
        JLabel infoLabel = new JLabel("<html><b>Active Rentals</b> - Maximum 3 vehicles can be rented at once. Double-click a row or select and click Return to return a vehicle.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_PRIMARY);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        return infoPanel;
    }
    
    private JPanel createRentalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(BACKGROUND);
        
        JButton refreshButton = createModernButton("🔄 Refresh", INFO_COLOR);
        refreshButton.setPreferredSize(new Dimension(120, 42));
        refreshButton.addActionListener(e -> loadRentalHistory());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        String[] columns = {"Rental ID", "Vehicle", "Duration", "Total Cost ($)",
                           "Start Date/Time", "Expected Return", "Give-Back Date", "Status"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(historyTableModel);
        
        // Hide the Rental ID column (column 0)
        historyTable.getColumnModel().getColumn(0).setMinWidth(0);
        historyTable.getColumnModel().getColumn(0).setMaxWidth(0);
        historyTable.getColumnModel().getColumn(0).setWidth(0);
        styleTable(historyTable);
        
        // Custom renderer for status
        historyTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if ("Active".equals(value)) {
                        c.setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Returned".equals(value)) {
                        c.setForeground(TEXT_SECONDARY);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 235, 240));
        table.setBackground(CARD_BG);
        table.setSelectionBackground(new Color(INFO_COLOR.getRed(), INFO_COLOR.getGreen(), INFO_COLOR.getBlue(), 40));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Center align all cells by default
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createEmptyBorder());
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(CARD_BG);
        field.setForeground(TEXT_PRIMARY);
        
        // Add focus listener for better UX
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(INFO_COLOR, 2),
                    BorderFactory.createEmptyBorder(9, 14, 9, 14)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        return field;
    }
    
    private void searchVehicles(String searchText) {
        availableTableModel.setRowCount(0);
        
        List<Vehicle> vehicles;
        if (searchText.trim().isEmpty()) {
            vehicles = vehicleController.getAvailableVehicles();
        } else {
            vehicles = vehicleController.searchVehiclesByName(searchText);
            vehicles.removeIf(v -> !"Available".equals(v.getStatus()) && !"Under Maintenance".equals(v.getStatus()));
        }
        
        if (vehicles.isEmpty()) {
            showInfoDialog("No available vehicles found matching your search.");
        }
        
        for (Vehicle vehicle : vehicles) {
            Object[] row = {
                vehicle.getVehicleId(),
                vehicle.getVehicleName(),
                vehicle.getVehicleType(),
                String.format("%.2f", vehicle.getPricePerDay())
            };
            availableTableModel.addRow(row);
        }
    }
    
    private void showRentDialog() {
        int selectedRow = availableTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningDialog("Please select a vehicle to rent");
            return;
        }
        
        String vehicleId = (String) availableTableModel.getValueAt(selectedRow, 0);
        String vehicleName = (String) availableTableModel.getValueAt(selectedRow, 1);
        double pricePerDay = Double.parseDouble((String) availableTableModel.getValueAt(selectedRow, 3));
        
        List<Rental> activeRentals = rentalController.getActiveRentalsByCustomer(currentUser.getUsername());
        for (Rental rental : activeRentals) {
            if (rental.getVehicleId().equals(vehicleId)) {
                showWarningDialog("You already have this vehicle rented!");
                return;
            }
        }
        
        if (activeRentals.size() >= 3) {
            showWarningDialog("Maximum 3 active rentals allowed!\nReturn some vehicles first.");
            return;
        }
        
        // Create modern rental form with date/time pickers
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 20));
        dialogPanel.setBackground(CARD_BG);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("🚗 Rent Vehicle");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TEXT_PRIMARY);
        dialogPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Calculate price per hour
        double pricePerHour = pricePerDay / 24.0;
        
        // Vehicle info
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Vehicle:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel vehicleLabel = new JLabel(vehicleName);
        vehicleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        vehicleLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(vehicleLabel, gbc);
        
        // Price info
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Price per Day:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", pricePerDay) + " ($" + String.format("%.2f", pricePerHour) + "/hr)");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(priceLabel, gbc);
        
        // Start Date/Time picker
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Start Date/Time:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd HH:mm");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startDateSpinner.setPreferredSize(new Dimension(200, 42));
        // Set minimum to now
        startDateSpinner.setValue(new Date());
        formPanel.add(startDateSpinner, gbc);
        
        // Expected Return Date/Time picker
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Expected Return:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd HH:mm");
        endDateSpinner.setEditor(endDateEditor);
        endDateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endDateSpinner.setPreferredSize(new Dimension(200, 42));
        // Default to 24 hours from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 24);
        endDateSpinner.setValue(cal.getTime());
        formPanel.add(endDateSpinner, gbc);
        
        // Duration display
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Duration:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel durationLabel = new JLabel("24h 0m");
        durationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        durationLabel.setForeground(INFO_COLOR);
        formPanel.add(durationLabel, gbc);
        
        // Total Cost display
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Total Cost:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel costLabel = new JLabel("$" + String.format("%.2f", pricePerDay));
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        costLabel.setForeground(SUCCESS_COLOR);
        formPanel.add(costLabel, gbc);
        
        // Update calculation when dates change
        Runnable updateCost = () -> {
            try {
                Date startDate = (Date) startDateSpinner.getValue();
                Date endDate = (Date) endDateSpinner.getValue();
                
                if (startDate != null && endDate != null) {
                    LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    
                    if (endDateTime.isAfter(startDateTime)) {
                        long hours = java.time.temporal.ChronoUnit.HOURS.between(startDateTime, endDateTime);
                        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(startDateTime, endDateTime) % 60;
                        
                        durationLabel.setText(String.format("%dh %dm", hours, minutes));
                        
                        double totalCost = pricePerHour * hours + (pricePerHour / 60 * minutes);
                        costLabel.setText("$" + String.format("%.2f", totalCost));
                        costLabel.setForeground(SUCCESS_COLOR);
                    } else {
                        durationLabel.setText("Invalid dates");
                        durationLabel.setForeground(DANGER_COLOR);
                        costLabel.setText("Invalid");
                        costLabel.setForeground(DANGER_COLOR);
                    }
                }
            } catch (Exception ex) {
                durationLabel.setText("Invalid");
                costLabel.setText("Invalid");
            }
        };
        
        startDateSpinner.addChangeListener(e -> updateCost.run());
        endDateSpinner.addChangeListener(e -> updateCost.run());
        
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Rent Vehicle",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Date startDate = (Date) startDateSpinner.getValue();
                Date endDate = (Date) endDateSpinner.getValue();
                
                if (startDate == null || endDate == null) {
                    showErrorDialog("Please select valid dates");
                    return;
                }
                
                LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                
                if (!endDateTime.isAfter(startDateTime)) {
                    showErrorDialog("Return date must be after start date");
                    return;
                }
                
                long hours = java.time.temporal.ChronoUnit.HOURS.between(startDateTime, endDateTime);
                if (hours > 720) { // 30 days max
                    showErrorDialog("Maximum rental period is 30 days (720 hours)");
                    return;
                }
                
                double totalCost = Double.parseDouble(costLabel.getText().replace("$", ""));
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "<html>Rent <b>" + vehicleName + "</b>?<br><br>" +
                    "Start: " + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "<br>" +
                    "Return: " + endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "<br>" +
                    "Total Cost: <b style='color: #27ae60;'>$" + String.format("%.2f", totalCost) + "</b></html>",
                    "Confirm Rental", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Rental rental = rentalController.rentVehicle(
                        currentUser.getUsername(), vehicleId, startDateTime, endDateTime);
                    
                    if (rental != null) {
                        loadData();
                        showSuccessDialog(
                            "<html><b>🎉 Rental Successful!</b><br><br>" +
                            "<b>Rental ID:</b> " + rental.getRentalId() + "<br>" +
                            "<b>Vehicle:</b> " + vehicleName + "<br>" +
                            "<b>Start:</b> " + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "<br>" +
                            "<b>Return:</b> " + endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "<br>" +
                            "<b>Total:</b> $" + String.format("%.2f", totalCost) + "</html>");
                    } else {
                        showErrorDialog("Failed to rent vehicle. It may have been rented by someone else.");
                        loadAvailableVehicles();
                    }
                }
            } catch (Exception ex) {
                showErrorDialog("An error occurred: " + ex.getMessage());
            }
        }
    }
    
    private void returnSelectedVehicle() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningDialog("Please select a rental to return");
            return;
        }

        String rentalId = (String) rentalTableModel.getValueAt(selectedRow, 0);
        String status = (String) rentalTableModel.getValueAt(selectedRow, 5);
        String vehicleName = (String) rentalTableModel.getValueAt(selectedRow, 1);

        if (!"Active".equals(status)) {
            showWarningDialog("This rental has already been returned");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>Return <b>" + vehicleName + "</b>?<br><br>" +
            "Please confirm the vehicle is in good condition.</html>",
            "Confirm Return", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (rentalController.returnVehicle(rentalId)) {
                loadData();
                showSuccessDialog("Vehicle returned successfully!\nThank you for renting with us.");
            } else {
                showErrorDialog("Failed to process return");
            }
        }
    }

    private void reportLostVehicle() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningDialog("Please select a rental to report as lost");
            return;
        }

        String rentalId = (String) rentalTableModel.getValueAt(selectedRow, 0);
        String status = (String) rentalTableModel.getValueAt(selectedRow, 5);
        String vehicleName = (String) rentalTableModel.getValueAt(selectedRow, 1);

        if (!"Active".equals(status)) {
            showWarningDialog("This rental is not active");
            return;
        }

        // Create date picker dialog
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 20));
        dialogPanel.setBackground(CARD_BG);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("🚨 Report Lost Vehicle");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_PRIMARY);
        dialogPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date picker
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateSpinner.setPreferredSize(new Dimension(200, 42));

        // Set minimum date to today
        dateSpinner.setValue(new Date());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Expected Give-Back Date:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(dateSpinner, gbc);

        dialogPanel.add(formPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Report Lost Vehicle",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Date selectedDate = (Date) dateSpinner.getValue();
            LocalDateTime giveBackDate = LocalDateTime.ofInstant(
                selectedDate.toInstant(), ZoneId.systemDefault());

            int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Report <b>" + vehicleName + "</b> as lost?<br><br>" +
                "Expected give-back date: <b>" + giveBackDate.toLocalDate() + "</b><br><br>" +
                "⚠️ This action cannot be undone.</html>",
                "Confirm Lost Report", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (rentalController.reportRentalAsLost(rentalId, giveBackDate)) {
                    loadData();
                    showSuccessDialog("Lost vehicle reported successfully!\nPlease return the vehicle by the specified date.");
                } else {
                    showErrorDialog("Failed to report vehicle as lost");
                }
            }
        }
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message,
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, "❌ " + message,
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, "⚠️ " + message,
            "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, "ℹ️ " + message,
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void loadData() {
        loadAvailableVehicles();
        loadMyRentals();
        loadRentalHistory();
    }
    
    private void loadAvailableVehicles() {
        availableTableModel.setRowCount(0);
        List<Vehicle> vehicles = vehicleController.getAvailableVehicles();
        
        for (Vehicle vehicle : vehicles) {
            Object[] row = {
                vehicle.getVehicleId(),
                vehicle.getVehicleName(),
                vehicle.getVehicleType(),
                String.format("%.2f", vehicle.getPricePerDay())
            };
            availableTableModel.addRow(row);
        }
    }
    
    private void loadMyRentals() {
        rentalTableModel.setRowCount(0);
        List<Rental> rentals = rentalController.getActiveRentalsByCustomer(currentUser.getUsername());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Rental rental : rentals) {
            Object[] row = {
                rental.getRentalId(),
                rental.getVehicleName(),
                rental.getRentalDate().format(formatter),
                rental.getExpectedReturnDate().format(formatter),
                rental.getFormattedDuration(),
                rental.getStatus()
            };
            rentalTableModel.addRow(row);
        }
    }
    
    private void loadRentalHistory() {
        historyTableModel.setRowCount(0);
        List<Rental> rentals = rentalController.getRentalsByCustomer(currentUser.getUsername());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Rental rental : rentals) {
            Object[] row = {
                rental.getRentalId(),
                rental.getVehicleName(),
                rental.getFormattedDuration(),
                String.format("%.2f", rental.getTotalCost()),
                rental.getRentalDate().format(formatter),
                rental.getExpectedReturnDate() != null ? rental.getExpectedReturnDate().format(formatter) : "-",
                rental.getGiveBackDate() != null ? rental.getGiveBackDate().format(formatter) : "-",
                rental.getStatus()
            };
            historyTableModel.addRow(row);
        }
    }
    
    private Color darkenColor(Color color, float fraction) {
        int red = Math.max(0, (int) (color.getRed() * (1 - fraction)));
        int green = Math.max(0, (int) (color.getGreen() * (1 - fraction)));
        int blue = Math.max(0, (int) (color.getBlue() * (1 - fraction)));
        return new Color(red, green, blue);
    }
    
    private Color brightenColor(Color color, float fraction) {
        int red = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * fraction));
        int green = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * fraction));
        int blue = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * fraction));
        return new Color(red, green, blue);
    }
}