package view;

import controller.VehicleController;
import controller.RentalController;
import controller.LoginController;
import model.Vehicle;
import model.Rental;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modern Admin Dashboard Frame for the Vehicle Rental System.
 * Enhanced with improved UI, better responsiveness, and professional styling.
 */
public class AdminFrame extends JFrame {
    private VehicleController vehicleController;
    private RentalController rentalController;
    private LoginController loginController;
    
    private JTable vehicleTable;
    private JTable rentalTable;
    private DefaultTableModel vehicleTableModel;
    private DefaultTableModel rentalTableModel;
    
    // Stat card labels for real-time updates
    private JLabel availableCountLabel;
    private JLabel totalVehiclesLabel;
    private JLabel totalRentalsLabel;
    private JLabel totalRevenueLabel;
    private JPanel statsPanel;
    
    private int mouseX, mouseY;
    
    // Color scheme
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
    
    public AdminFrame(VehicleController vehicleController, RentalController rentalController, 
                      LoginController loginController) {
        this.vehicleController = vehicleController;
        this.rentalController = rentalController;
        this.loginController = loginController;
        
        // Register as listeners for data changes
        vehicleController.addVehicleDataListener(this::onVehicleDataChanged);
        rentalController.addRentalDataListener(this::onRentalDataChanged);
        
        setupUI();
        loadVehicleData();
        loadRentalData();  // Load rental data on initialization
    }
    
    /**
     * Called when vehicle data changes - refresh the UI
     */
    private void onVehicleDataChanged() {
        SwingUtilities.invokeLater(() -> {
            loadVehicleData();
            refreshStats();
        });
    }
    
    /**
     * Called when rental data changes - refresh the UI
     */
    private void onRentalDataChanged() {
        SwingUtilities.invokeLater(() -> {
            loadRentalData();
            refreshStats();
        });
    }
    
    /**
     * Refresh the statistics panel with current counts
     */
    private void refreshStats() {
        if (availableCountLabel != null) {
            availableCountLabel.setText(String.valueOf(vehicleController.getAvailableCount()));
        }
        if (totalVehiclesLabel != null) {
            totalVehiclesLabel.setText(String.valueOf(vehicleController.getAllVehicles().size()));
        }
        if (totalRentalsLabel != null) {
            totalRentalsLabel.setText(String.valueOf(rentalController.getTotalRentalCount()));
        }
        if (totalRevenueLabel != null) {
            totalRevenueLabel.setText("$" + String.format("%.2f", rentalController.getTotalRevenue()));
        }
        
        // Force stats panel to repaint
        if (statsPanel != null) {
            statsPanel.revalidate();
            statsPanel.repaint();
        }
    }
    
    private void setupUI() {
        setTitle("Admin Dashboard - Vehicle Rental System");
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
        
        // Main panel with modern background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createLineBorder(PRIMARY_DARK, 2));
        
        // Modern Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane with custom styling
        JTabbedPane tabbedPane = createStyledTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add drag to move window functionality
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
        
        // Left side - Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(" 🏠 Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Vehicle Rental Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        titlePanel.add(textPanel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Right side - Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);
        
        // Minimize button
        JButton minimizeButton = createWindowButton("─", new Color(241, 196, 15));
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
        
        JPanel vehiclePanel = createVehicleManagementPanel();
        tabbedPane.addTab("  🚗 Vehicles  ", vehiclePanel);
        
        JPanel historyPanel = createRentalHistoryPanel();
        tabbedPane.addTab("  📋 Rental History  ", historyPanel);
        
        JPanel statsPanel = createStatisticsPanel();
        tabbedPane.addTab("  📊 Statistics  ", statsPanel);
        
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
    
    private JPanel createVehicleManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Button panel with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setBackground(BACKGROUND);
        
        JButton addButton = createModernButton("➕ Add Vehicle", SUCCESS_COLOR);
        addButton.setPreferredSize(new Dimension(150, 42));
        addButton.addActionListener(e -> showAddVehicleDialog());
        buttonPanel.add(addButton);
        
        JButton editButton = createModernButton("✏️ Edit Vehicle", WARNING_COLOR);
        editButton.setPreferredSize(new Dimension(150, 42));
        editButton.addActionListener(e -> showEditVehicleDialog());
        buttonPanel.add(editButton);
        
        JButton deleteButton = createModernButton("🗑️ Delete", DANGER_COLOR);
        deleteButton.setPreferredSize(new Dimension(130, 42));
        deleteButton.addActionListener(e -> deleteSelectedVehicle());
        buttonPanel.add(deleteButton);
        
        buttonPanel.add(Box.createHorizontalStrut(20));
        
        JButton refreshButton = createModernButton("🔄 Refresh", INFO_COLOR);
        refreshButton.setPreferredSize(new Dimension(120, 42));
        refreshButton.addActionListener(e -> loadVehicleData());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        // Table with enhanced styling
        String[] columns = {"ID", "Vehicle Name", "Type", "Price/Day ($)", "Status"};
        vehicleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vehicleTable = new JTable(vehicleTableModel);
        styleTable(vehicleTable);
        
        // Custom renderer for status column
        vehicleTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if ("Available".equals(value)) {
                        c.setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Rented".equals(value)) {
                        c.setForeground(DANGER_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Under Maintenance".equals(value)) {
                        c.setForeground(WARNING_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        vehicleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditVehicleDialog();
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createRentalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(BACKGROUND);
        
        JButton refreshButton = createModernButton("🔄 Refresh", INFO_COLOR);
        refreshButton.setPreferredSize(new Dimension(120, 42));
        refreshButton.addActionListener(e -> loadRentalData());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        String[] columns = {"Rental ID", "Customer", "Vehicle", "Duration", "Total Cost ($)", 
                           "Start Date/Time", "Expected Return", "Status"};
        rentalTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        rentalTable = new JTable(rentalTableModel);
        styleTable(rentalTable);
        
        // Custom renderer for status column
        rentalTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
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
                    } else if ("Lost".equals(value)) {
                        c.setForeground(DANGER_COLOR);
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
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Store reference for real-time updates
        statsPanel = panel;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        int availableCount = vehicleController.getAvailableCount();
        int totalVehicles = vehicleController.getAllVehicles().size();
        int totalRentals = rentalController.getTotalRentalCount();
        double totalRevenue = rentalController.getTotalRevenue();
        
        // Create stat cards with labels
        JPanel availableCard = createStatCard("🚗 Available Vehicles", String.valueOf(availableCount), 
            SUCCESS_COLOR, "Ready to rent");
        availableCountLabel = getValueLabelFromCard(availableCard);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(availableCard, gbc);
        
        JPanel totalVehiclesCard = createStatCard("🚙 Total Vehicles", String.valueOf(totalVehicles), 
            INFO_COLOR, "In fleet");
        totalVehiclesLabel = getValueLabelFromCard(totalVehiclesCard);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(totalVehiclesCard, gbc);
        
        JPanel totalRentalsCard = createStatCard("📋 Total Rentals", String.valueOf(totalRentals), 
            WARNING_COLOR, "All time");
        totalRentalsLabel = getValueLabelFromCard(totalRentalsCard);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(totalRentalsCard, gbc);
        
        JPanel totalRevenueCard = createStatCard("💰 Total Revenue", "$" + String.format("%.2f", totalRevenue), 
            SUCCESS_COLOR, "Earned");
        totalRevenueLabel = getValueLabelFromCard(totalRevenueCard);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(totalRevenueCard, gbc);
        
        return panel;
    }
    
    /**
     * Helper method to extract the value JLabel from a stat card
     */
    private JLabel getValueLabelFromCard(JPanel card) {
        // Get all components and find the JLabel with the value (largest font)
        for (Component comp : card.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel innerPanel = (JPanel) comp;
                for (Component innerComp : innerPanel.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel) innerComp;
                        // The value label has font size 38
                        if (label.getFont().getSize() == 38) {
                            return label;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private JPanel createStatCard(String title, String value, Color color, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Subtle shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 20, 20);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 4, true),
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        ));
        card.setPreferredSize(new Dimension(280, 180));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(color);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        card.add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(centerPanel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return card;
    }
    
    private void showAddVehicleDialog() {
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 20));
        dialogPanel.setBackground(CARD_BG);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Add New Vehicle");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_PRIMARY);
        dialogPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField nameField = createModernTextField();
        String[] types = {"Car", "Motorbike", "Truck"};
        JComboBox<String> typeCombo = createModernComboBox(types);
        JTextField priceField = createModernTextField();
        String[] statuses = {"Available", "Rented", "Under Maintenance"};
        JComboBox<String> statusCombo = createModernComboBox(statuses);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Vehicle Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(typeCombo, gbc);
        typeCombo.setPreferredSize(new Dimension(200, 50));

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Price Per Day ($):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(statusCombo, gbc);
        statusCombo.setPreferredSize(new Dimension(200, 50));
        
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Add New Vehicle",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String priceStr = priceField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();
            
            if (name.isEmpty()) {
                showErrorDialog("Please enter vehicle name");
                return;
            }
            
            if (name.length() < 2 || name.length() > 50) {
                showErrorDialog("Vehicle name must be 2-50 characters");
                return;
            }
            
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    showErrorDialog("Price must be greater than 0");
                    return;
                }
                if (price > 10000) {
                    showErrorDialog("Price seems too high (max $10,000)");
                    return;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid price");
                return;
            }
            
            vehicleController.addVehicle(name, type, price, status);
            loadVehicleData();
            showSuccessDialog("Vehicle added successfully!");
        }
    }
    
    private void showEditVehicleDialog() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningDialog("Please select a vehicle to edit");
            return;
        }
        
        String vehicleId = (String) vehicleTableModel.getValueAt(selectedRow, 0);
        Vehicle vehicle = vehicleController.getVehicleById(vehicleId);
        
        if (vehicle == null) {
            showErrorDialog("Vehicle not found");
            return;
        }
        
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 20));
        dialogPanel.setBackground(CARD_BG);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Edit Vehicle - " + vehicleId);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_PRIMARY);
        dialogPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField nameField = createModernTextField();
        nameField.setText(vehicle.getVehicleName());
        String[] types = {"Car", "Motorbike", "Truck"};
        JComboBox<String> typeCombo = createModernComboBox(types);
        typeCombo.setSelectedItem(vehicle.getVehicleType());
        JTextField priceField = createModernTextField();
        priceField.setText(String.valueOf(vehicle.getPricePerDay()));
        String[] statuses = {"Available", "Rented", "Under Maintenance"};
        JComboBox<String> statusCombo = createModernComboBox(statuses);
        statusCombo.setSelectedItem(vehicle.getStatus());
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Vehicle Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(typeCombo, gbc);
        typeCombo.setPreferredSize(new Dimension(200, 50));
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Price Per Day ($):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createFieldLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(statusCombo, gbc);
        statusCombo.setPreferredSize(new Dimension(200, 50));
        
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Edit Vehicle",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String priceStr = priceField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();
            
            if (name.isEmpty()) {
                showErrorDialog("Please enter vehicle name");
                return;
            }
            
            if (name.length() < 2 || name.length() > 50) {
                showErrorDialog("Vehicle name must be 2-50 characters");
                return;
            }
            
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    showErrorDialog("Price must be greater than 0");
                    return;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid price");
                return;
            }
            
            vehicleController.updateVehicle(vehicleId, name, type, price, status);
            loadVehicleData();
            showSuccessDialog("Vehicle updated successfully!");
        }
    }
    
    private void deleteSelectedVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningDialog("Please select a vehicle to delete");
            return;
        }
        
        String vehicleId = (String) vehicleTableModel.getValueAt(selectedRow, 0);
        String vehicleName = (String) vehicleTableModel.getValueAt(selectedRow, 1);
        String status = (String) vehicleTableModel.getValueAt(selectedRow, 4);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>Delete <b>" + vehicleName + "</b> (ID: " + vehicleId + ")?<br><br>" +
            "<span style='color: #e74c3c;'>This action cannot be undone.</span></html>",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (vehicleController.deleteVehicle(vehicleId)) {
                showSuccessDialog("Vehicle deleted successfully!");
                loadVehicleData();
            } else {
                showErrorDialog("Failed to delete vehicle");
            }
        }
    }
    
    private void loadVehicleData() {
        vehicleTableModel.setRowCount(0);
        List<Vehicle> vehicles = vehicleController.getAllVehicles();
        
        for (Vehicle vehicle : vehicles) {
            Object[] row = {
                vehicle.getVehicleId(),
                vehicle.getVehicleName(),
                vehicle.getVehicleType(),
                String.format("%.2f", vehicle.getPricePerDay()),
                vehicle.getStatus()
            };
            vehicleTableModel.addRow(row);
        }
    }
    
    private void loadRentalData() {
        rentalTableModel.setRowCount(0);
        List<Rental> rentals = rentalController.getAllRentals();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Rental rental : rentals) {
            Object[] row = {
                rental.getRentalId(),
                rental.getCustomerUsername(),
                rental.getVehicleName(),
                rental.getFormattedDuration(),
                String.format("%.2f", rental.getTotalCost()),
                rental.getRentalDate() != null ? rental.getRentalDate().format(formatter) : "-",
                rental.getExpectedReturnDate() != null ? rental.getExpectedReturnDate().format(formatter) : "-",
                rental.getStatus()
            };
            rentalTableModel.addRow(row);
        }
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
    
    @SuppressWarnings("unchecked")
    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(CARD_BG);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    setBackground(new Color(INFO_COLOR.getRed(), INFO_COLOR.getGreen(), INFO_COLOR.getBlue(), 40));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(isSelected ? new Color(INFO_COLOR.getRed(), INFO_COLOR.getGreen(), INFO_COLOR.getBlue(), 20) : CARD_BG);
                    setForeground(TEXT_PRIMARY);
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
                return this;
            }
        });
        
        return comboBox;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, "✅ " + message,
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
