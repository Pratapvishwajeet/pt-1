// Enhanced Data Structures and Algorithms Visualization Tool
// Multi-concept learning platform with interactive visualizations

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class DSAVisualizationMain extends JFrame implements ActionListener {
    
    private JPanel mainPanel;
    private JLabel titleLabel, subtitleLabel, welcomeLabel;
    private UserManager userManager;
    
    // Menu buttons for different DSA concepts
    private JButton btnBST, btnAVL, btnHeap, btnGraph, btnSort, btnTrie, btnHash, btnDP, btnAbout, btnLogout;
    
    public DSAVisualizationMain() {
        userManager = UserManager.getInstance();
        initializeMainMenu();
    }
    
    private void initializeMainMenu() {
        setTitle("DSA Visualization - Interactive Learning Platform");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception e) {
            // Use default look and feel if Nimbus is not available
        }
        
        // Main panel with gradient background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(135, 206, 235), // Sky blue
                    0, getHeight(), new Color(255, 255, 255) // White
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        titleLabel = new JLabel("DSA Visualization Platform");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(25, 25, 112)); // Midnight blue
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 0, 10, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Welcome message
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            welcomeLabel.setForeground(new Color(34, 139, 34)); // Forest green
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 5, 0);
            mainPanel.add(welcomeLabel, gbc);
        }
        
        // Subtitle
        subtitleLabel = new JLabel("Choose a Data Structure or Algorithm to Explore");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(70, 70, 70));
        gbc.gridy = currentUser != null ? 2 : 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        mainPanel.add(subtitleLabel, gbc);
        
        // Create buttons grid
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 10, 15);
        int startRow = currentUser != null ? 3 : 2;
        
        // Row 1: BST, AVL Tree, Heap
        btnBST = createMenuButton("Binary Search Tree", "Interactive BST with search, insert, delete", new Color(60, 179, 113));
        gbc.gridx = 0; gbc.gridy = startRow;
        mainPanel.add(btnBST, gbc);
        
        btnAVL = createMenuButton("AVL Tree", "Self-balancing BST with rotations", new Color(30, 144, 255));
        gbc.gridx = 1;
        mainPanel.add(btnAVL, gbc);
        
        btnHeap = createMenuButton("Heap", "Min/Max heap with heapify operations", new Color(255, 140, 0));
        gbc.gridx = 2;
        mainPanel.add(btnHeap, gbc);
        
        // Row 2: Graph, Sorting, Trie
        btnGraph = createMenuButton("Graph Algorithms", "BFS, DFS, Shortest Path algorithms", new Color(138, 43, 226));
        gbc.gridx = 0; gbc.gridy = startRow + 1;
        mainPanel.add(btnGraph, gbc);
        
        btnSort = createMenuButton("Sorting Algorithms", "Visualize various sorting techniques", new Color(255, 69, 0));
        gbc.gridx = 1;
        mainPanel.add(btnSort, gbc);
        
        btnTrie = createMenuButton("Trie", "Prefix tree for string operations", new Color(46, 139, 87));
        gbc.gridx = 2;
        mainPanel.add(btnTrie, gbc);
        
        // Row 3: Hash Table, Dynamic Programming, About
        btnHash = createMenuButton("Hash Table", "Hash functions and collision handling", new Color(184, 134, 11));
        gbc.gridx = 0; gbc.gridy = startRow + 2;
        mainPanel.add(btnHash, gbc);
        
        btnDP = createMenuButton("Dynamic Programming", "Common DP problems visualization", new Color(75, 0, 130));
        gbc.gridx = 1;
        mainPanel.add(btnDP, gbc);
        
        // About & Help button
        btnAbout = createMenuButton("About & Help", "Learn about this platform", new Color(105, 105, 105));
        gbc.gridx = 2;
        mainPanel.add(btnAbout, gbc);
        
        // Logout button (if user is logged in)
        if (currentUser != null) {
            btnLogout = createMenuButton("Logout", "Sign out and return to login", new Color(220, 20, 60));
            gbc.gridx = 1; gbc.gridy = startRow + 3;
            gbc.insets = new Insets(20, 15, 10, 15);
            mainPanel.add(btnLogout, gbc);
        }
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JButton createMenuButton(String title, String description, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(240, 100));
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);
        
        // Title label
        JLabel buttonTitleLabel = new JLabel(title, SwingConstants.CENTER);
        buttonTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        buttonTitleLabel.setForeground(Color.WHITE);
        
        // Description label
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(Color.WHITE);
        
        button.add(buttonTitleLabel, BorderLayout.CENTER);
        button.add(descLabel, BorderLayout.SOUTH);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBST) {
            dispose();
            SwingUtilities.invokeLater(BSTVisualization::new);
        } else if (e.getSource() == btnAVL) {
            dispose();
            SwingUtilities.invokeLater(AVLTreeVisualization::new);
        } else if (e.getSource() == btnHeap) {
            dispose();
            SwingUtilities.invokeLater(HeapVisualization::new);
        } else if (e.getSource() == btnGraph) {
            dispose();
            SwingUtilities.invokeLater(GraphVisualization::new);
        } else if (e.getSource() == btnSort) {
            dispose();
            SwingUtilities.invokeLater(SortingVisualization::new);
        } else if (e.getSource() == btnTrie) {
            dispose();
            SwingUtilities.invokeLater(TrieVisualization::new);
        } else if (e.getSource() == btnHash) {
            dispose();
            SwingUtilities.invokeLater(HashTableVisualization::new);
        } else if (e.getSource() == btnDP) {
            dispose();
            SwingUtilities.invokeLater(DynamicProgrammingVisualization::new);
        } else if (e.getSource() == btnAbout) {
            showAbout();
        } else if (e.getSource() == btnLogout) {
            handleLogout();
        }
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            userManager.logout();
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginRegisterFrame().setVisible(true);
            });
        }
    }
    
    private void showAbout() {
        String aboutText = "<html><body style='font-family: Arial; font-size: 12px; width: 450px;'>" +
            "<h2 style='color: #2E4057;'>DSA Visualization Platform</h2>" +
            "<p><b>Version:</b> 2.0 Enhanced Edition</p>" +
            "<p><b>Purpose:</b> Interactive learning tool for Data Structures and Algorithms</p>" +
            "<h3 style='color: #048A81;'>Currently Available:</h3>" +
            "<ul>" +
            "<li><b>Binary Search Tree:</b> Complete implementation with search, insert, delete, validation</li>" +
            "<li><b>AVL Tree:</b> Self-balancing BST with rotation animations</li>" +
            "<li><b>Heap:</b> Min/Max heap data structure with heapify operations</li>" +
            "<li><b>Graph Algorithms:</b> BFS, DFS, Dijkstra's shortest path</li>" +
            "<li><b>Sorting Algorithms:</b> Bubble, Quick, Selection sort visualizations</li>" +
            "<li><b>Trie:</b> Prefix tree with autocomplete functionality</li>" +
            "<li><b>Hash Table:</b> Multiple hash functions with collision handling</li>" +
            "<li><b>Dynamic Programming:</b> Fibonacci, Knapsack, LCS problems</li>" +
            "</ul>" +
            "<h3 style='color: #048A81;'>Educational Features:</h3>" +
            "<ul>" +
            "<li>Step-by-step algorithm execution</li>" +
            "<li>Visual feedback and animations</li>" +
            "<li>Interactive controls and keyboard shortcuts</li>" +
            "<li>Real-time complexity analysis</li>" +
            "<li>Comprehensive help system</li>" +
            "</ul>" +
            "<p><i>Perfect for computer science students and algorithm enthusiasts!</i></p>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(
            this,
            aboutText,
            "About DSA Visualization Platform",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new NimbusLookAndFeel());
            } catch (Exception e) {
                // Use default look and feel
            }
            // Show login screen first
            new LoginRegisterFrame().setVisible(true);
        });
    }
}