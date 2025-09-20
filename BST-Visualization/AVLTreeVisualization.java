// AVL Tree Visualization - Self-balancing Binary Search Tree
// Features: Automatic balancing, rotation animations, balance factor display

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class AVLTreeVisualization extends JFrame implements ActionListener, KeyListener {
    private AVLNode root;
    private JPanel topPanel, treePanel, infoPanel;
    private JPanel topLeftPanel, topRightPanel;
    private JButton btnAdd, btnDelete, btnSearch, btnClear, btnHelp, btnBack;
    private JTextField tf;
    private JLabel labelHeight, ansHeight, labelNodes, ansNodes;
    private JLabel ansInorder, ansPreorder, ansPostorder;
    private int X = 350, Y = 80;
    private Graphics2D g2;

    // AVL Node class with balance factor
    private static class AVLNode {
        static int TEXT_WIDTH = 50;
        static int TEXT_HEIGHT = 50;
        
        JLabel data;
        AVLNode left, right;
        int height;
        int balanceFactor;
        Points p;
        JLabel balanceLabel;

        AVLNode(int value) {
            data = new JLabel(String.valueOf(value), SwingConstants.CENTER);
            data.setFont(new Font("Arial", Font.BOLD, 14));
            data.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)
            ));
            data.setOpaque(true);
            data.setBackground(new Color(135, 206, 250)); // Light sky blue
            data.setForeground(Color.BLACK);
            
            // Balance factor label
            balanceLabel = new JLabel("0", SwingConstants.CENTER);
            balanceLabel.setFont(new Font("Arial", Font.BOLD, 10));
            balanceLabel.setForeground(Color.RED);
            
            height = 1;
            balanceFactor = 0;
            p = null;
        }
        
        void updateBalanceFactorColor() {
            if (balanceFactor < -1 || balanceFactor > 1) {
                data.setBackground(new Color(255, 182, 193)); // Light pink for unbalanced
                balanceLabel.setForeground(Color.RED);
            } else {
                data.setBackground(new Color(135, 206, 250)); // Light sky blue for balanced
                balanceLabel.setForeground(Color.BLUE);
            }
            balanceLabel.setText(String.valueOf(balanceFactor));
        }
    }

    private static class Points {
        int x1, y1, x2, y2;
        Points(int x1, int y1, int x2, int y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    public AVLTreeVisualization() {
        initialize();
    }

    private void initialize() {
        setTitle("AVL Tree Visualization - Self-Balancing BST");
        setSize(1400, 800);
        getContentPane().setBackground(new Color(240, 248, 255));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupPanels();
        setupControls();
        setupInfoPanel();
        
        setVisible(true);
    }

    private void setupPanels() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(230, 240, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topLeftPanel.setBackground(new Color(230, 240, 250));
        topPanel.add(topLeftPanel, BorderLayout.WEST);

        topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setBackground(new Color(230, 240, 250));
        topPanel.add(topRightPanel, BorderLayout.EAST);

        treePanel = new JPanel(null);
        treePanel.setBackground(new Color(248, 248, 255));

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(getWidth(), 150));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Tree Information"));

        add(topPanel, BorderLayout.NORTH);
        add(treePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void setupControls() {
        // Statistics
        labelHeight = new JLabel("Height: ");
        labelHeight.setFont(new Font("Arial", Font.BOLD, 16));
        topLeftPanel.add(labelHeight);

        ansHeight = new JLabel("0");
        ansHeight.setFont(new Font("Arial", Font.BOLD, 16));
        topLeftPanel.add(ansHeight);

        labelNodes = new JLabel("  |  Nodes: ");
        labelNodes.setFont(new Font("Arial", Font.BOLD, 16));
        topLeftPanel.add(labelNodes);

        ansNodes = new JLabel("0");
        ansNodes.setFont(new Font("Arial", Font.BOLD, 16));
        topLeftPanel.add(ansNodes);

        // Input field
        tf = new JTextField();
        tf.setFont(new Font("Arial", Font.BOLD, 16));
        tf.setPreferredSize(new Dimension(120, 35));
        tf.addKeyListener(this);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        topRightPanel.add(tf);

        // Buttons
        btnAdd = createStyledButton("Add", new Color(60, 179, 113));
        btnDelete = createStyledButton("Delete", new Color(220, 20, 60));
        btnSearch = createStyledButton("Search", new Color(30, 144, 255));
        btnClear = createStyledButton("Clear", new Color(255, 140, 0));
        btnHelp = createStyledButton("Help", new Color(70, 130, 180));
        btnBack = createStyledButton("← Back", new Color(105, 105, 105));

        btnAdd.addActionListener(this);
        btnDelete.addActionListener(this);
        btnSearch.addActionListener(this);
        btnClear.addActionListener(this);
        btnHelp.addActionListener(this);
        btnBack.addActionListener(this);

        topRightPanel.add(btnAdd);
        topRightPanel.add(btnDelete);
        topRightPanel.add(btnSearch);
        topRightPanel.add(btnClear);
        topRightPanel.add(btnHelp);
        topRightPanel.add(btnBack);
    }

    private void setupInfoPanel() {
        JLabel inorderLabel = new JLabel("Inorder: ");
        inorderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ansInorder = new JLabel("Tree is empty");
        ansInorder.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel preorderLabel = new JLabel("Preorder: ");
        preorderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ansPreorder = new JLabel("Tree is empty");
        ansPreorder.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel postorderLabel = new JLabel("Postorder: ");
        postorderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ansPostorder = new JLabel("Tree is empty");
        ansPostorder.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(inorderLabel);
        infoPanel.add(ansInorder);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(preorderLabel);
        infoPanel.add(ansPreorder);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(postorderLabel);
        infoPanel.add(ansPostorder);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(70, 35));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    // AVL Tree Operations
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private AVLNode insert(AVLNode node, int key) {
        if (node == null)
            return new AVLNode(key);

        int nodeValue = Integer.parseInt(node.data.getText());
        if (key < nodeValue)
            node.left = insert(node.left, key);
        else if (key > nodeValue)
            node.right = insert(node.right, key);
        else
            return node; // Duplicate keys not allowed

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);
        node.balanceFactor = balance;

        // Left Left Case
        if (balance > 1 && key < Integer.parseInt(node.left.data.getText()))
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && key > Integer.parseInt(node.right.data.getText()))
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && key > Integer.parseInt(node.left.data.getText())) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key < Integer.parseInt(node.right.data.getText())) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void add(int value) {
        try {
            root = insert(root, value);
            arrangeNodes();
            updateInfo();
            JOptionPane.showMessageDialog(this, "Added " + value + " to AVL tree!\nTree automatically balanced.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding node: " + e.getMessage());
        }
    }

    private void arrangeNodes() {
        treePanel.removeAll();
        if (root != null) {
            arrangeNode(root, treePanel.getWidth() / 2, 30, treePanel.getWidth() / 4);
            updateBalanceFactors(root);
        }
        treePanel.repaint();
    }

    private void arrangeNode(AVLNode node, int x, int y, int xOffset) {
        if (node == null) return;

        node.data.setBounds(x - 25, y, 50, 40);
        node.balanceLabel.setBounds(x - 10, y - 15, 20, 15);
        treePanel.add(node.data);
        treePanel.add(node.balanceLabel);

        if (node.left != null) {
            arrangeNode(node.left, x - xOffset, y + 80, xOffset / 2);
            node.p = new Points(x, y + 20, x - xOffset + 25, y + 80);
        }
        if (node.right != null) {
            arrangeNode(node.right, x + xOffset, y + 80, xOffset / 2);
            // Store right connection point too
        }
    }

    private void updateBalanceFactors(AVLNode node) {
        if (node == null) return;
        node.balanceFactor = getBalance(node);
        node.updateBalanceFactorColor();
        updateBalanceFactors(node.left);
        updateBalanceFactors(node.right);
    }

    private void updateInfo() {
        if (root == null) {
            ansInorder.setText("Tree is empty");
            ansPreorder.setText("Tree is empty");
            ansPostorder.setText("Tree is empty");
            ansHeight.setText("0");
            ansNodes.setText("0");
        } else {
            ansInorder.setText(inorder(root));
            ansPreorder.setText(preorder(root));
            ansPostorder.setText(postorder(root));
            ansHeight.setText(String.valueOf(height(root)));
            ansNodes.setText(String.valueOf(countNodes(root)));
        }
    }

    private String inorder(AVLNode node) {
        if (node == null) return "";
        return inorder(node.left) + node.data.getText() + " " + inorder(node.right);
    }

    private String preorder(AVLNode node) {
        if (node == null) return "";
        return node.data.getText() + " " + preorder(node.left) + preorder(node.right);
    }

    private String postorder(AVLNode node) {
        if (node == null) return "";
        return postorder(node.left) + postorder(node.right) + node.data.getText() + " ";
    }

    private int countNodes(AVLNode node) {
        return node == null ? 0 : 1 + countNodes(node.left) + countNodes(node.right);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            dispose();
            SwingUtilities.invokeLater(DSAVisualizationMain::new);
            return;
        } else if (e.getSource() == btnClear) {
            root = null;
            treePanel.removeAll();
            treePanel.repaint();
            updateInfo();
            return;
        } else if (e.getSource() == btnHelp) {
            showHelp();
            return;
        }

        String input = tf.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a number!");
            return;
        }

        try {
            int value = Integer.parseInt(input);
            if (e.getSource() == btnAdd) {
                add(value);
            } else if (e.getSource() == btnDelete) {
                JOptionPane.showMessageDialog(this, "Delete operation coming soon!");
            } else if (e.getSource() == btnSearch) {
                JOptionPane.showMessageDialog(this, "Search operation coming soon!");
            }
            tf.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer!");
        }
    }

    private void showHelp() {
        String helpText = "<html><body style='width: 500px;'>" +
            "<h2>AVL Tree Visualization</h2>" +
            "<p><b>AVL Tree</b> is a self-balancing binary search tree where the heights of " +
            "the two child subtrees of any node differ by at most one.</p>" +
            "<h3>Features:</h3>" +
            "<ul>" +
            "<li><b>Automatic Balancing:</b> Tree maintains balance after every insertion</li>" +
            "<li><b>Balance Factor:</b> Red numbers show balance factor (left height - right height)</li>" +
            "<li><b>Color Coding:</b> Pink nodes indicate temporary imbalance during rotations</li>" +
            "<li><b>Rotations:</b> Left/Right rotations maintain AVL property</li>" +
            "</ul>" +
            "<h3>Balance Factor Rules:</h3>" +
            "<ul>" +
            "<li>-1, 0, 1: Balanced (blue background)</li>" +
            "<li>&lt;-1 or &gt;1: Unbalanced (pink background)</li>" +
            "</ul>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "AVL Tree Help", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            btnAdd.doClick();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void paint(Graphics g) {
        super.paintComponents(g);
        drawConnections(g);
    }

    private void drawConnections(Graphics g) {
        if (root != null) {
            g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(Color.BLACK);
            drawNodeConnections(root);
        }
    }

    private void drawNodeConnections(AVLNode node) {
        if (node == null) return;
        
        int offset = topPanel.getHeight();
        if (node.left != null && node.p != null) {
            g2.drawLine(node.p.x1, node.p.y1 + offset, node.p.x2, node.p.y2 + offset);
        }
        
        drawNodeConnections(node.left);
        drawNodeConnections(node.right);
    }
}