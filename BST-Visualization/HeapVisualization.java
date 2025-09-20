// Heap Visualization - Min/Max Heap with Heapify Operations
// Features: Insert, Extract, Heapify, Build Heap, Heap Sort

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class HeapVisualization extends JFrame implements ActionListener, KeyListener {
    private java.util.List<Integer> heap;
    private boolean isMinHeap;
    private JPanel topPanel, heapPanel, infoPanel;
    private JPanel topLeftPanel, topRightPanel;
    private JButton btnInsert, btnExtract, btnHeapify, btnBuildHeap, btnHeapSort, btnClear, btnToggleType, btnBack, btnHelp;
    private JTextField tf;
    private JLabel labelSize, ansSize, labelType, ansType;
    private JLabel heapOrderLabel, heapArrayLabel;
    private JTextArea logArea;
    private Graphics2D g2;

    public HeapVisualization() {
        heap = new ArrayList<>();
        isMinHeap = true;
        initialize();
    }

    private void initialize() {
        setTitle("Heap Visualization - Min/Max Heap Operations");
        setSize(1400, 800);
        getContentPane().setBackground(new Color(240, 248, 255));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupPanels();
        setupControls();
        setupInfoPanel();
        updateDisplay();
        
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

        heapPanel = new JPanel(null);
        heapPanel.setBackground(new Color(248, 248, 255));

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(getWidth(), 200));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Heap Information & Operations Log"));

        add(topPanel, BorderLayout.NORTH);
        add(heapPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void setupControls() {
        // Statistics
        labelSize = new JLabel("Size: ");
        labelSize.setFont(new Font("Arial", Font.BOLD, 16));
        topLeftPanel.add(labelSize);

        ansSize = new JLabel("0");
        ansSize.setFont(new Font("Arial", Font.BOLD, 16));
        topLeftPanel.add(ansSize);

        labelType = new JLabel("  |  Type: ");
        labelType.setFont(new Font("Arial", Font.BOLD, 16));
        topLeftPanel.add(labelType);

        ansType = new JLabel("Min Heap");
        ansType.setFont(new Font("Arial", Font.BOLD, 16));
        ansType.setForeground(new Color(0, 128, 0));
        topLeftPanel.add(ansType);

        // Input field
        tf = new JTextField();
        tf.setFont(new Font("Arial", Font.BOLD, 16));
        tf.setPreferredSize(new Dimension(100, 35));
        tf.addKeyListener(this);
        topRightPanel.add(tf);

        // Buttons
        btnInsert = createStyledButton("Insert", new Color(60, 179, 113));
        btnExtract = createStyledButton("Extract", new Color(220, 20, 60));
        btnHeapify = createStyledButton("Heapify", new Color(255, 140, 0));
        btnBuildHeap = createStyledButton("Build", new Color(138, 43, 226));
        btnHeapSort = createStyledButton("Sort", new Color(30, 144, 255));
        btnToggleType = createStyledButton("Min⇄Max", new Color(184, 134, 11));
        btnClear = createStyledButton("Clear", new Color(255, 69, 0));
        btnHelp = createStyledButton("Help", new Color(70, 130, 180));
        btnBack = createStyledButton("← Back", new Color(105, 105, 105));

        btnInsert.addActionListener(this);
        btnExtract.addActionListener(this);
        btnHeapify.addActionListener(this);
        btnBuildHeap.addActionListener(this);
        btnHeapSort.addActionListener(this);
        btnToggleType.addActionListener(this);
        btnClear.addActionListener(this);
        btnHelp.addActionListener(this);
        btnBack.addActionListener(this);

        topRightPanel.add(btnInsert);
        topRightPanel.add(btnExtract);
        topRightPanel.add(btnHeapify);
        topRightPanel.add(btnBuildHeap);
        topRightPanel.add(btnHeapSort);
        topRightPanel.add(btnToggleType);
        topRightPanel.add(btnClear);
        topRightPanel.add(btnHelp);
        topRightPanel.add(btnBack);
    }

    private void setupInfoPanel() {
        heapOrderLabel = new JLabel("Heap Order: ");
        heapOrderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        heapArrayLabel = new JLabel("Array Representation: []");
        heapArrayLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        logArea = new JTextArea(5, 50);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(heapOrderLabel);
        infoPanel.add(heapArrayLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(new JLabel("Operations Log:"));
        infoPanel.add(scrollPane);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setPreferredSize(new Dimension(65, 35));
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

    // Heap Operations
    private void heapifyUp(int index) {
        if (index == 0) return;
        
        int parentIndex = (index - 1) / 2;
        boolean shouldSwap = isMinHeap ? 
            heap.get(index) < heap.get(parentIndex) : 
            heap.get(index) > heap.get(parentIndex);
            
        if (shouldSwap) {
            Collections.swap(heap, index, parentIndex);
            logOperation("Swapped " + heap.get(index) + " with parent " + heap.get(parentIndex));
            heapifyUp(parentIndex);
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int targetIndex = index;

        if (leftChild < size) {
            boolean leftBetter = isMinHeap ? 
                heap.get(leftChild) < heap.get(targetIndex) : 
                heap.get(leftChild) > heap.get(targetIndex);
            if (leftBetter) targetIndex = leftChild;
        }

        if (rightChild < size) {
            boolean rightBetter = isMinHeap ? 
                heap.get(rightChild) < heap.get(targetIndex) : 
                heap.get(rightChild) > heap.get(targetIndex);
            if (rightBetter) targetIndex = rightChild;
        }

        if (targetIndex != index) {
            Collections.swap(heap, index, targetIndex);
            logOperation("Heapify: Swapped " + heap.get(index) + " with " + heap.get(targetIndex));
            heapifyDown(targetIndex);
        }
    }

    private void insert(int value) {
        heap.add(value);
        logOperation("Inserted " + value + " at position " + (heap.size() - 1));
        heapifyUp(heap.size() - 1);
        updateDisplay();
        logOperation("Heap property maintained after insertion");
    }

    private Integer extract() {
        if (heap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Heap is empty!");
            return null;
        }

        int root = heap.get(0);
        int lastElement = heap.remove(heap.size() - 1);
        
        if (!heap.isEmpty()) {
            heap.set(0, lastElement);
            logOperation("Moved last element " + lastElement + " to root");
            heapifyDown(0);
        }
        
        logOperation("Extracted " + (isMinHeap ? "minimum" : "maximum") + " value: " + root);
        updateDisplay();
        return root;
    }

    private void buildHeap(String input) {
        try {
            heap.clear();
            String[] numbers = input.split("[,\\s]+");
            for (String num : numbers) {
                if (!num.trim().isEmpty()) {
                    heap.add(Integer.parseInt(num.trim()));
                }
            }
            
            logOperation("Building heap from array: " + Arrays.toString(numbers));
            
            // Build heap bottom-up
            for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
                heapifyDown(i);
            }
            
            updateDisplay();
            logOperation("Heap built successfully using bottom-up approach");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format! Use comma-separated numbers.");
        }
    }

    private void performHeapSort() {
        if (heap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Heap is empty!");
            return;
        }

        java.util.List<Integer> sorted = new ArrayList<>();
        java.util.List<Integer> originalHeap = new ArrayList<>(heap);
        
        logOperation("Starting Heap Sort...");
        
        while (!heap.isEmpty()) {
            sorted.add(extract());
        }
        
        String sortOrder = isMinHeap ? "ascending" : "descending";
        logOperation("Heap Sort completed. Sorted in " + sortOrder + " order: " + sorted);
        
        // Restore original heap
        heap = originalHeap;
        updateDisplay();
        
        JOptionPane.showMessageDialog(this, 
            "Heap Sort Result (" + sortOrder + "):\n" + sorted + 
            "\n\nOriginal heap restored.");
    }

    private void updateDisplay() {
        heapPanel.removeAll();
        
        if (!heap.isEmpty()) {
            drawHeap();
        }
        
        ansSize.setText(String.valueOf(heap.size()));
        ansType.setText(isMinHeap ? "Min Heap" : "Max Heap");
        ansType.setForeground(isMinHeap ? new Color(0, 128, 0) : new Color(128, 0, 0));
        
        heapArrayLabel.setText("Array Representation: " + heap.toString());
        
        heapPanel.repaint();
    }

    private void drawHeap() {
        int size = heap.size();
        if (size == 0) return;

        int startX = heapPanel.getWidth() / 2;
        int startY = 50;
        int levelHeight = 80;
        
        drawNode(0, startX, startY, heapPanel.getWidth() / 4, levelHeight);
    }

    private void drawNode(int index, int x, int y, int xOffset, int levelHeight) {
        if (index >= heap.size()) return;

        // Create node
        JLabel nodeLabel = new JLabel(String.valueOf(heap.get(index)), SwingConstants.CENTER);
        nodeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nodeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        nodeLabel.setOpaque(true);
        
        // Color coding based on heap type and position
        if (index == 0) {
            nodeLabel.setBackground(new Color(255, 215, 0)); // Gold for root
        } else {
            nodeLabel.setBackground(isMinHeap ? 
                new Color(144, 238, 144) :  // Light green for min heap
                new Color(255, 182, 193));  // Light pink for max heap
        }
        
        nodeLabel.setBounds(x - 25, y, 50, 40);
        heapPanel.add(nodeLabel);

        // Draw children
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        if (leftChild < heap.size()) {
            drawNode(leftChild, x - xOffset, y + levelHeight, xOffset / 2, levelHeight);
        }
        if (rightChild < heap.size()) {
            drawNode(rightChild, x + xOffset, y + levelHeight, xOffset / 2, levelHeight);
        }
    }

    private void logOperation(String operation) {
        logArea.append(operation + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            dispose();
            SwingUtilities.invokeLater(DSAVisualizationMain::new);
            return;
        } else if (e.getSource() == btnClear) {
            heap.clear();
            updateDisplay();
            logArea.setText("");
            logOperation("Heap cleared");
            return;
        } else if (e.getSource() == btnToggleType) {
            isMinHeap = !isMinHeap;
            logOperation("Switched to " + (isMinHeap ? "Min" : "Max") + " Heap");
            
            // Rebuild heap with new type
            if (!heap.isEmpty()) {
                for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
                    heapifyDown(i);
                }
            }
            updateDisplay();
            return;
        } else if (e.getSource() == btnExtract) {
            extract();
            return;
        } else if (e.getSource() == btnHeapSort) {
            performHeapSort();
            return;
        } else if (e.getSource() == btnHelp) {
            showHelp();
            return;
        }

        String input = tf.getText().trim();
        if (input.isEmpty() && e.getSource() != btnHeapify) {
            JOptionPane.showMessageDialog(this, "Please enter a number or numbers!");
            return;
        }

        try {
            if (e.getSource() == btnInsert) {
                int value = Integer.parseInt(input);
                insert(value);
            } else if (e.getSource() == btnBuildHeap) {
                buildHeap(input);
            } else if (e.getSource() == btnHeapify) {
                if (!heap.isEmpty()) {
                    for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
                        heapifyDown(i);
                    }
                    updateDisplay();
                    logOperation("Performed heapify on entire heap");
                } else {
                    JOptionPane.showMessageDialog(this, "Heap is empty!");
                }
            }
            tf.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integer(s)!");
        }
    }

    private void showHelp() {
        String helpText = "<html><body style='width: 600px;'>" +
            "<h2>Heap Visualization</h2>" +
            "<p><b>Heap</b> is a complete binary tree that satisfies the heap property.</p>" +
            "<h3>Operations:</h3>" +
            "<ul>" +
            "<li><b>Insert:</b> Add element and maintain heap property</li>" +
            "<li><b>Extract:</b> Remove root (min/max) element</li>" +
            "<li><b>Heapify:</b> Restore heap property for entire heap</li>" +
            "<li><b>Build:</b> Create heap from comma-separated numbers</li>" +
            "<li><b>Sort:</b> Perform heap sort (shows result)</li>" +
            "<li><b>Min⇄Max:</b> Toggle between min and max heap</li>" +
            "</ul>" +
            "<h3>Heap Properties:</h3>" +
            "<ul>" +
            "<li><b>Min Heap:</b> Parent ≤ Children (root is minimum)</li>" +
            "<li><b>Max Heap:</b> Parent ≥ Children (root is maximum)</li>" +
            "<li><b>Complete Tree:</b> All levels filled except possibly last</li>" +
            "<li><b>Array Representation:</b> Parent at i, children at 2i+1, 2i+2</li>" +
            "</ul>" +
            "<h3>Color Coding:</h3>" +
            "<ul>" +
            "<li><b>Gold:</b> Root node (min/max element)</li>" +
            "<li><b>Light Green:</b> Min heap nodes</li>" +
            "<li><b>Light Pink:</b> Max heap nodes</li>" +
            "</ul>" +
            "<p><b>Time Complexity:</b> Insert/Extract O(log n), Build O(n), Sort O(n log n)</p>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Heap Visualization Help", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            btnInsert.doClick();
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
        if (heap.isEmpty()) return;
        
        g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(Color.BLACK);
        
        int offset = topPanel.getHeight();
        drawNodeConnections(0, heapPanel.getWidth() / 2, 50 + offset, heapPanel.getWidth() / 4, 80);
    }

    private void drawNodeConnections(int index, int x, int y, int xOffset, int levelHeight) {
        if (index >= heap.size()) return;

        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        if (leftChild < heap.size()) {
            g2.drawLine(x, y + 20, x - xOffset, y + levelHeight);
            drawNodeConnections(leftChild, x - xOffset, y + levelHeight, xOffset / 2, levelHeight);
        }
        if (rightChild < heap.size()) {
            g2.drawLine(x, y + 20, x + xOffset, y + levelHeight);
            drawNodeConnections(rightChild, x + xOffset, y + levelHeight, xOffset / 2, levelHeight);
        }
    }
}