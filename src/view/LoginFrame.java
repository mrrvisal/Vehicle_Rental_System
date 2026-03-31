package view;

import controller.LoginController;
import controller.VehicleController;
import controller.RentalController;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Login Frame for the Vehicle Rental System.
 * Enhanced with improved UI, animations, and professional styling.
 */
public class LoginFrame extends JFrame {
    // Static shared controllers to persist data across sessions
    private static LoginController sharedLoginController;
    private static VehicleController sharedVehicleController;
    private static RentalController sharedRentalController;
    
    private LoginController loginController;
    private VehicleController vehicleController;
    private RentalController rentalController;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    private int mouseX, mouseY;
    
    // Color scheme matching the main application
    private static final Color PRIMARY_DARK = new Color(25, 42, 86);
    private static final Color PRIMARY_LIGHT = new Color(42, 82, 152);
    private static final Color ACCENT_COLOR = new Color(255, 107, 53);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color INFO_COLOR = new Color(52, 152, 219);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    
    public LoginFrame() {
        // Initialize shared controllers if they don't exist (first login)
        if (sharedLoginController == null) {
            sharedLoginController = new LoginController();
        }
        if (sharedVehicleController == null) {
            sharedVehicleController = new VehicleController();
        }
        if (sharedRentalController == null) {
            sharedRentalController = new RentalController(sharedVehicleController);
        }
        
        // Use shared controllers
        this.loginController = sharedLoginController;
        this.vehicleController = sharedVehicleController;
        this.rentalController = sharedRentalController;
        
        setupUI();
        setupEventHandlers();
        
        // Set window shape for rounded corners
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
        } catch (Exception e) {
            // Fallback if shape is not supported
        }
    }
    
    private void setupUI() {
        setTitle("Vehicle Rental System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        
        // Main container with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background with animation effect
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_DARK,
                    getWidth() * 0.7f, getHeight() * 0.7f, PRIMARY_LIGHT
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern overlay with diagonal lines
                g2d.setColor(new Color(255, 255, 255, 3));
                for (int i = -getHeight(); i < getWidth(); i += 15) {
                    g2d.drawLine(i, 0, i + getHeight(), getHeight());
                }
                
                // Add glow effect at the center
                RadialGradientPaint glow = new RadialGradientPaint(
                    new Point(getWidth()/2, getHeight()/4),
                    getWidth() * 0.6f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 10), new Color(255, 255, 255, 0)}
                );
                g2d.setPaint(glow);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2));
        
        // Header panel with controls
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Title panel with icon and animation
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Card layout for login/register switching
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Login panel
        JPanel loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "login");
        
        // Register panel
        JPanel registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "register");
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        // Footer with version info
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add drag support to title panel
        titlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        
        titlePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
            }
        });
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // System title
        JLabel sysTitle = new JLabel("VRS");
        sysTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sysTitle.setForeground(new Color(255, 255, 255, 180));
        headerPanel.add(sysTitle, BorderLayout.WEST);
        
        // Window controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controlsPanel.setOpaque(false);
        
        // Minimize button
        JButton minimizeButton = createWindowButton("−", new Color(255, 255, 255, 150));
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        controlsPanel.add(minimizeButton);
        
        // Close button
        JButton closeButton = createWindowButton("×", DANGER_COLOR);
        closeButton.addActionListener(e -> {
            Timer fadeOut = new Timer(10, null);
            fadeOut.addActionListener(new ActionListener() {
                private float opacity = 1.0f;
                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity -= 0.05f;
                    if (opacity <= 0) {
                        fadeOut.stop();
                        System.exit(0);
                    }
                    LoginFrame.this.setOpacity(opacity);
                }
            });
            fadeOut.start();
        });
        controlsPanel.add(closeButton);
        
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Icon with animation
        JLabel iconLabel = new JLabel("🚗") {
            private float rotation = 0f;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.rotate(Math.toRadians(rotation), getWidth()/2, getHeight()/2);
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setForeground(new Color(255, 255, 255, 220));
        
        // Animate the car icon
        Timer animationTimer = new Timer(50, e -> {
            iconLabel.repaint();
        });
        animationTimer.start();
        
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        titlePanel.add(iconLabel, gbc);
        
        // Title with gradient text
        GradientLabel titleLabel = new GradientLabel("VEHICLE RENTAL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        titlePanel.add(titleLabel, gbc);
        
        return titlePanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        
        // Version info
        JLabel versionLabel = new JLabel("v2.1.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(255, 255, 255, 120));
        footerPanel.add(versionLabel, BorderLayout.WEST);
        
        // Copyright
        JLabel copyrightLabel = new JLabel("© 2024 Vehicle Rental System");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(new Color(255, 255, 255, 120));
        footerPanel.add(copyrightLabel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private JButton createWindowButton(String text, Color color) {
        JButton button = new JButton(text) {
            private boolean hovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                if (hovered) {
                    g2d.setColor(color);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 40));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(30, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Welcome text
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(welcomeLabel, gbc);
        
        // Username field
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPanel userPanel = createInputPanel("👤 Username");
        usernameField = createModernTextField("Enter your username");
        userPanel.add(usernameField, BorderLayout.CENTER);
        panel.add(userPanel, gbc);
        
        // Password field
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        JPanel passPanel = createInputPanel("🔒 Password");
        passwordField = createModernPasswordField("Enter your password");
        passPanel.add(passwordField, BorderLayout.CENTER);
        panel.add(passPanel, gbc);
        
        // Message label
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(255, 100, 100, 220));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, gbc);
        
        // Login button
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        loginButton = createModernButton("SIGN IN", SUCCESS_COLOR);
        loginButton.setPreferredSize(new Dimension(300, 50));
        panel.add(loginButton, gbc);
        
        // Divider
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        JPanel divider = createDivider("OR");
        panel.add(divider, gbc);
        
        // Register link
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton switchToRegister = createLinkButton("Don't have an account? Create one");
        switchToRegister.addActionListener(e -> {
            messageLabel.setText("");
            cardLayout.show(cardPanel, "register");
        });
        panel.add(switchToRegister, gbc);
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Title
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        JLabel regTitle = new JLabel("Create Account");
        regTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        regTitle.setForeground(Color.WHITE);
        regTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(regTitle, gbc);
        
        // Username
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPanel userPanel = createInputPanel("👤 Username");
        JTextField regUsernameField = createModernTextField("Choose username (3-20 chars)");
        userPanel.add(regUsernameField, BorderLayout.CENTER);
        panel.add(userPanel, gbc);
        
        // Password
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPanel passPanel = createInputPanel("🔒 Password");
        JPasswordField regPasswordField = createModernPasswordField("Choose password (min 4 chars)");
        passPanel.add(regPasswordField, BorderLayout.CENTER);
        panel.add(passPanel, gbc);
        
        // Confirm Password
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        JPanel confirmPanel = createInputPanel("✅ Confirm Password");
        JPasswordField confirmPasswordField = createModernPasswordField("Re-enter your password");
        confirmPanel.add(confirmPasswordField, BorderLayout.CENTER);
        panel.add(confirmPanel, gbc);
        
        // Message
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        JLabel regMessageLabel = new JLabel("");
        regMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regMessageLabel.setForeground(new Color(255, 100, 100, 220));
        regMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(regMessageLabel, gbc);
        
        // Register button
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        registerButton = createModernButton("CREATE ACCOUNT", INFO_COLOR);
        registerButton.setPreferredSize(new Dimension(300, 50));
        registerButton.addActionListener(e -> {
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());
            
            if (username.isEmpty()) {
                regMessageLabel.setText("❌ Username is required");
                regUsernameField.requestFocus();
                return;
            }
            
            if (password.isEmpty()) {
                regMessageLabel.setText("❌ Password is required");
                regPasswordField.requestFocus();
                return;
            }
            
            if (!password.equals(confirm)) {
                regMessageLabel.setText("❌ Passwords do not match");
                confirmPasswordField.setText("");
                confirmPasswordField.requestFocus();
                return;
            }
            
            if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
                regMessageLabel.setText("❌ Username: 3-20 chars (letters, numbers, _)");
                return;
            }
            
            if (password.length() < 4) {
                regMessageLabel.setText("❌ Password must be at least 4 characters");
                return;
            }
            
            // Show loading
            registerButton.setText("CREATING...");
            registerButton.setEnabled(false);
            
            Timer timer = new Timer(500, evt -> {
                if (loginController.registerCustomer(username, password)) {
                    regMessageLabel.setText("✅ Registration successful!");
                    Timer successTimer = new Timer(1000, e2 -> {
                        cardLayout.show(cardPanel, "login");
                        usernameField.setText(username);
                        passwordField.setText("");
                        messageLabel.setText("");
                        regUsernameField.setText("");
                        regPasswordField.setText("");
                        confirmPasswordField.setText("");
                        regMessageLabel.setText("");
                        registerButton.setText("CREATE ACCOUNT");
                        registerButton.setEnabled(true);
                    });
                    successTimer.setRepeats(false);
                    successTimer.start();
                } else {
                    regMessageLabel.setText("❌ Username already exists");
                    registerButton.setText("CREATE ACCOUNT");
                    registerButton.setEnabled(true);
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
        panel.add(registerButton, gbc);
        
        // Divider
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel divider = createDivider("OR");
        panel.add(divider, gbc);
        
        // Login link
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton switchToLogin = createLinkButton("Already have an account? Sign in");
        switchToLogin.addActionListener(e -> {
            regMessageLabel.setText("");
            cardLayout.show(cardPanel, "login");
        });
        panel.add(switchToLogin, gbc);
        
        return panel;
    }
    
    private JPanel createInputPanel(String label) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(new Color(255, 255, 255, 200));
        panel.add(textLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createDivider(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        
        JSeparator leftSep = new JSeparator();
        leftSep.setForeground(new Color(255, 255, 255, 60));
        leftSep.setBackground(new Color(255, 255, 255, 60));
        gbc.gridx = 0;
        panel.add(leftSep, gbc);
        
        JLabel orLabel = new JLabel(text);
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        orLabel.setForeground(new Color(255, 255, 255, 120));
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel.add(orLabel, gbc);
        
        JSeparator rightSep = new JSeparator();
        rightSep.setForeground(new Color(255, 255, 255, 60));
        rightSep.setBackground(new Color(255, 255, 255, 60));
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel.add(rightSep, gbc);
        
        return panel;
    }
    
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setFont(getFont().deriveFont(Font.ITALIC));
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawString(placeholder, getInsets().left, 
                        (getHeight() + g2d.getFontMetrics().getAscent()) / 2 - 1);
                    g2d.dispose();
                }
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(255, 255, 255, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setCaretColor(ACCENT_COLOR);
        
        // Focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBackground(new Color(255, 255, 255, 25));
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(11, 14, 11, 14)
                ));
                field.repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBackground(new Color(255, 255, 255, 15));
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                field.repaint();
            }
        });
        
        return field;
    }
    
    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setFont(getFont().deriveFont(Font.ITALIC));
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawString(placeholder, getInsets().left, 
                        (getHeight() + g2d.getFontMetrics().getAscent()) / 2 - 1);
                    g2d.dispose();
                }
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(255, 255, 255, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setCaretColor(ACCENT_COLOR);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBackground(new Color(255, 255, 255, 25));
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(11, 14, 11, 14)
                ));
                field.repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBackground(new Color(255, 255, 255, 15));
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                field.repaint();
            }
        });
        
        return field;
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                Color currentColor = bgColor;
                if (getModel().isPressed()) {
                    currentColor = darkenColor(bgColor, 0.2f);
                } else if (getModel().isRollover()) {
                    currentColor = brightenColor(bgColor, 0.1f);
                }
                
                // Button background with gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, currentColor,
                    getWidth(), getHeight(), darkenColor(currentColor, 0.15f)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Inner glow
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                
                // Text with shadow
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                
                // Text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), textX + 1, textY + 1);
                
                // Main text
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 50));
        
        return button;
    }
    
    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(new Color(255, 255, 255, 160));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(ACCENT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(255, 255, 255, 160));
            }
        });
        
        return button;
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
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
        
        // Enter key navigation
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            messageLabel.setText("❌ Username is required");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            messageLabel.setText("❌ Password is required");
            passwordField.requestFocus();
            return;
        }
        
        // Show loading state
        loginButton.setEnabled(false);
        loginButton.setText("SIGNING IN...");
        messageLabel.setText("");
        
        // Simulate slight delay for better UX
        Timer timer = new Timer(500, e -> {
            User user = loginController.login(username, password);
            if (user != null) {
                messageLabel.setText("✅ Login successful!");
                Timer successTimer = new Timer(800, e2 -> {
                    dispose();
                    if ("Admin".equals(user.getRole())) {
                        new AdminFrame(vehicleController, rentalController, loginController).setVisible(true);
                    } else {
                        new CustomerFrame(vehicleController, rentalController, loginController, user).setVisible(true);
                    }
                });
                successTimer.setRepeats(false);
                successTimer.start();
            } else {
                messageLabel.setText("❌ Invalid username or password");
                passwordField.setText("");
                passwordField.requestFocus();
                loginButton.setEnabled(true);
                loginButton.setText("SIGN IN");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    // Custom Gradient Label class
    private class GradientLabel extends JLabel {
        public GradientLabel(String text) {
            super(text);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            
            GradientPaint gradient = new GradientPaint(
                (getWidth() - textWidth) / 2, 0, ACCENT_COLOR,
                (getWidth() + textWidth) / 2, getHeight(), Color.WHITE
            );
            
            g2d.setFont(getFont());
            g2d.setPaint(gradient);
            g2d.drawString(getText(), (getWidth() - textWidth) / 2, 
                (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            
            g2d.dispose();
        }
    }
}