// Login and Registration Frame
// Modern UI with tabbed interface for authentication

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class LoginRegisterFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel loginPanel, registerPanel;
    
    // Login components
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton, loginExitButton;
    
    // Register components
    private JTextField regUsernameField, regEmailField, regFullNameField;
    private JPasswordField regPasswordField, regConfirmPasswordField;
    private JButton registerButton, regCancelButton;
    
    private UserManager userManager;
    private DSAVisualizationMain mainApp;
    
    public LoginRegisterFrame() {
        userManager = UserManager.getInstance();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("DSA Visualization - User Authentication");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
        }
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(74, 144, 226),
                    0, getHeight(), new Color(142, 197, 252)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Header
        createHeader(mainPanel);
        
        // Create tabbed pane
        createTabbedPane(mainPanel);
        
        add(mainPanel);
    }
    
    private void createHeader(JPanel parent) {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("DSA Visualization Platform", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Please login or create a new account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(230, 230, 230));
        
        // Storage mode indicator
        JLabel storageLabel = new JLabel(userManager.getStorageMode(), SwingConstants.CENTER);
        storageLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        storageLabel.setForeground(new Color(200, 200, 200));
        
        headerPanel.setLayout(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(subtitleLabel, BorderLayout.NORTH);
        southPanel.add(storageLabel, BorderLayout.SOUTH);
        
        headerPanel.add(northPanel, BorderLayout.NORTH);
        headerPanel.add(southPanel, BorderLayout.SOUTH);
        
        parent.add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createTabbedPane(JPanel parent) {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        
        // Create login panel
        createLoginPanel();
        tabbedPane.addTab("Login", new ImageIcon(), loginPanel, "Login to existing account");
        
        // Create register panel
        createRegisterPanel();
        tabbedPane.addTab("Register", new ImageIcon(), registerPanel, "Create new account");
        
        parent.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        loginUsernameField = new JTextField(20);
        loginUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginUsernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        loginPanel.add(loginUsernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        loginPasswordField = new JPasswordField(20);
        loginPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        loginPanel.add(loginPasswordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        loginButton = createStyledButton("Login", new Color(34, 139, 34));
        loginButton.addActionListener(this::handleLogin);
        buttonPanel.add(loginButton);
        
        loginExitButton = createStyledButton("Exit", new Color(220, 53, 69));
        loginExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(loginExitButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginPanel.add(buttonPanel, gbc);
        
        // Add Enter key listener for login
        ActionListener loginAction = this::handleLogin;
        loginUsernameField.addActionListener(loginAction);
        loginPasswordField.addActionListener(loginAction);
    }
    
    private void createRegisterPanel() {
        registerPanel = new JPanel();
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setLayout(new GridBagLayout());
        registerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        registerPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        regFullNameField = new JTextField(20);
        regFullNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        regFullNameField.setBorder(createFieldBorder());
        registerPanel.add(regFullNameField, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        regUsernameField = new JTextField(20);
        regUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        regUsernameField.setBorder(createFieldBorder());
        registerPanel.add(regUsernameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        regEmailField = new JTextField(20);
        regEmailField.setFont(new Font("Arial", Font.PLAIN, 14));
        regEmailField.setBorder(createFieldBorder());
        registerPanel.add(regEmailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        regPasswordField = new JPasswordField(20);
        regPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        regPasswordField.setBorder(createFieldBorder());
        registerPanel.add(regPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0;
        registerPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.weightx = 1.0;
        regConfirmPasswordField = new JPasswordField(20);
        regConfirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        regConfirmPasswordField.setBorder(createFieldBorder());
        registerPanel.add(regConfirmPasswordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        registerButton = createStyledButton("Register", new Color(0, 123, 255));
        registerButton.addActionListener(this::handleRegister);
        buttonPanel.add(registerButton);
        
        regCancelButton = createStyledButton("Cancel", new Color(108, 117, 125));
        regCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearRegisterFields();
            }
        });
        buttonPanel.add(regCancelButton);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        registerPanel.add(buttonPanel, gbc);
    }
    
    private javax.swing.border.Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void handleLogin(ActionEvent event) {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        UserManager.LoginResult result = userManager.loginUser(username, password);
        
        if (result == UserManager.LoginResult.SUCCESS) {
            showMessage("Login successful! Welcome " + username + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            openMainApplication();
        } else {
            String message = "";
            switch (result) {
                case USER_NOT_FOUND:
                    message = "User not found. Please check your username.";
                    break;
                case INVALID_PASSWORD:
                    message = "Invalid password. Please try again.";
                    break;
                case EMPTY_FIELDS:
                    message = "Please fill in all fields.";
                    break;
            }
            showMessage(message, "Login Failed", JOptionPane.ERROR_MESSAGE);
            loginPasswordField.setText("");
        }
    }
    
    private void handleRegister(ActionEvent event) {
        String fullName = regFullNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String confirmPassword = new String(regConfirmPasswordField.getPassword());
        
        UserManager.RegistrationResult result = userManager.registerUser(
            username, password, confirmPassword, email, fullName);
        
        if (result == UserManager.RegistrationResult.SUCCESS) {
            showMessage("Registration successful! You can now login.", "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            clearRegisterFields();
            tabbedPane.setSelectedIndex(0); // Switch to login tab
        } else {
            String message = "";
            switch (result) {
                case SUCCESS:
                    // Should not reach here since we handle SUCCESS above
                    break;
                case USERNAME_EXISTS:
                    message = "Username already exists. Please choose another.";
                    break;
                case INVALID_USERNAME:
                    message = "Invalid username. Use 3-20 alphanumeric characters.";
                    break;
                case INVALID_EMAIL:
                    message = "Invalid email format.";
                    break;
                case WEAK_PASSWORD:
                    message = "Password too weak. Use at least 6 characters.";
                    break;
                case EMPTY_FIELDS:
                    message = "Please fill in all fields.";
                    break;
            }
            showMessage(message, "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearRegisterFields() {
        regFullNameField.setText("");
        regUsernameField.setText("");
        regEmailField.setText("");
        regPasswordField.setText("");
        regConfirmPasswordField.setText("");
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private void openMainApplication() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            mainApp = new DSAVisualizationMain();
            mainApp.setVisible(true);
        });
    }
}