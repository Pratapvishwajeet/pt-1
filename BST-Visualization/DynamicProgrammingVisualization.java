// Dynamic Programming Visualization - Common DP problems with memoization tables
// Features: Fibonacci, Knapsack, LCS with step-by-step solution building

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DynamicProgrammingVisualization extends JFrame implements ActionListener {
    private JPanel topPanel, visualPanel, infoPanel;
    private JButton btnFibonacci, btnKnapsack, btnLCS, btnClear, btnStep, btnSolve, btnBack, btnHelp;
    private JTextField tfN, tfCapacity, tfString1, tfString2;
    private JTextArea logArea;
    private JLabel statusLabel, complexityLabel;
    private JTable memoTable;
    private javax.swing.table.DefaultTableModel tableModel;
    
    private String currentProblem;
    private int[][] dp;
    private boolean[][] computed;
    private int n, capacity;
    private String str1, str2;
    private int[] weights, values;
    private javax.swing.Timer stepTimer;
    private int currentStep;
    private boolean isAnimating;
    
    // Colors for visualization
    private static final Color COMPUTED_COLOR = new Color(144, 238, 144); // Light green
    private static final Color CURRENT_COLOR = new Color(255, 255, 0); // Yellow
    private static final Color OPTIMAL_COLOR = new Color(255, 69, 0); // Red
    
    public DynamicProgrammingVisualization() {
        currentProblem = "None";
        isAnimating = false;
        initialize();
    }
    
    private void initialize() {
        setTitle("Dynamic Programming Visualization - Fibonacci, Knapsack, LCS");
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
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(230, 240, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        visualPanel = new JPanel(new BorderLayout());
        visualPanel.setBackground(new Color(248, 248, 255));
        visualPanel.setPreferredSize(new Dimension(getWidth(), 400));
        visualPanel.setBorder(BorderFactory.createTitledBorder("Memoization Table"));
        
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(getWidth(), 250));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Problem Analysis & Step Log"));
        
        add(topPanel, BorderLayout.NORTH);
        add(visualPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupControls() {
        // Problem selection buttons
        btnFibonacci = createStyledButton("Fibonacci", new Color(60, 179, 113), 80);
        btnKnapsack = createStyledButton("0/1 Knapsack", new Color(30, 144, 255), 90);
        btnLCS = createStyledButton("LCS", new Color(138, 43, 226), 60);
        
        btnFibonacci.addActionListener(this);
        btnKnapsack.addActionListener(this);
        btnLCS.addActionListener(this);
        
        topPanel.add(btnFibonacci);
        topPanel.add(btnKnapsack);
        topPanel.add(btnLCS);
        
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Input fields
        topPanel.add(new JLabel("n:"));
        tfN = new JTextField("10", 3);
        tfN.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfN);
        
        topPanel.add(new JLabel("Capacity:"));
        tfCapacity = new JTextField("10", 3);
        tfCapacity.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfCapacity);
        
        topPanel.add(new JLabel("String1:"));
        tfString1 = new JTextField("ABCDGH", 8);
        tfString1.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfString1);
        
        topPanel.add(new JLabel("String2:"));
        tfString2 = new JTextField("AEDFHR", 8);
        tfString2.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfString2);
        
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Control buttons
        btnStep = createStyledButton("Step", new Color(255, 140, 0), 60);
        btnSolve = createStyledButton("Solve", new Color(50, 205, 50), 70);
        btnClear = createStyledButton("Clear", new Color(220, 20, 60), 60);
        btnHelp = createStyledButton("Help", new Color(70, 130, 180), 60);
        btnBack = createStyledButton("← Back", new Color(105, 105, 105), 70);
        
        btnStep.addActionListener(this);
        btnSolve.addActionListener(this);
        btnClear.addActionListener(this);
        btnHelp.addActionListener(this);
        btnBack.addActionListener(this);
        
        topPanel.add(btnStep);
        topPanel.add(btnSolve);
        topPanel.add(btnClear);
        topPanel.add(btnHelp);
        topPanel.add(btnBack);
    }
    
    private void setupInfoPanel() {
        // Status and complexity panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(245, 245, 245));
        
        statusLabel = new JLabel("Select a DP problem to begin visualization");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0, 100, 0));
        
        complexityLabel = new JLabel("Time: O(?), Space: O(?)");
        complexityLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        complexityLabel.setForeground(new Color(0, 0, 139));
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(complexityLabel);
        
        // Log area
        logArea = new JTextArea(10, 80);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logArea.setEditable(false);
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        infoPanel.add(statusPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(scrollPane);
        
        // Setup empty table initially
        setupEmptyTable();
    }
    
    private void setupEmptyTable() {
        tableModel = new javax.swing.table.DefaultTableModel();
        memoTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                
                if (dp != null && row < dp.length && col < dp[0].length) {
                    if (computed != null && computed[row][col]) {
                        comp.setBackground(COMPUTED_COLOR);
                    } else {
                        comp.setBackground(Color.WHITE);
                    }
                    
                    // Highlight current step
                    if (row == currentStep / 100 && col == currentStep % 100) {
                        comp.setBackground(CURRENT_COLOR);
                    }
                } else {
                    comp.setBackground(Color.WHITE);
                }
                
                return comp;
            }
        };
        
        memoTable.setFont(new Font("Arial", Font.PLAIN, 11));
        memoTable.setRowHeight(25);
        memoTable.setGridColor(Color.GRAY);
        memoTable.setShowGrid(true);
        
        JScrollPane tableScrollPane = new JScrollPane(memoTable);
        visualPanel.removeAll();
        visualPanel.add(tableScrollPane, BorderLayout.CENTER);
        visualPanel.revalidate();
        visualPanel.repaint();
    }
    
    private JButton createStyledButton(String text, Color backgroundColor, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setPreferredSize(new Dimension(width, 30));
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
    
    // Fibonacci Problem
    private void setupFibonacci() {
        try {
            n = Integer.parseInt(tfN.getText());
            if (n < 0 || n > 50) {
                JOptionPane.showMessageDialog(this, "Please enter n between 0 and 50!");
                return;
            }
            
            currentProblem = "Fibonacci";
            dp = new int[n + 1][1];
            computed = new boolean[n + 1][1];
            currentStep = 0;
            
            // Initialize table
            tableModel.setRowCount(n + 1);
            tableModel.setColumnCount(2);
            tableModel.setColumnIdentifiers(new String[]{"F(i)", "Value"});
            
            for (int i = 0; i <= n; i++) {
                tableModel.setValueAt("F(" + i + ")", i, 0);
                tableModel.setValueAt("?", i, 1);
                dp[i][0] = -1;
                computed[i][0] = false;
            }
            
            logArea.append("=== Fibonacci Sequence F(" + n + ") ===\n");
            logArea.append("Recurrence: F(n) = F(n-1) + F(n-2)\n");
            logArea.append("Base cases: F(0) = 0, F(1) = 1\n");
            statusLabel.setText("Fibonacci problem setup complete. Use Step or Solve to compute.");
            complexityLabel.setText("Time: O(n), Space: O(n)");
            
            memoTable.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for n!");
        }
    }
    
    private int solveFibonacci(int num, boolean stepByStep) {
        if (num <= 1) {
            dp[num][0] = num;
            computed[num][0] = true;
            tableModel.setValueAt(num, num, 1);
            
            if (stepByStep) {
                logArea.append("Base case: F(" + num + ") = " + num + "\n");
                currentStep = num * 100;
                memoTable.repaint();
                return num;
            }
        }
        
        if (computed[num][0]) {
            if (stepByStep) {
                logArea.append("Found memoized: F(" + num + ") = " + dp[num][0] + "\n");
            }
            return dp[num][0];
        }
        
        if (stepByStep) {
            logArea.append("Computing F(" + num + ") = F(" + (num-1) + ") + F(" + (num-2) + ")\n");
            currentStep = num * 100;
            memoTable.repaint();
        }
        
        int result = solveFibonacci(num - 1, stepByStep) + solveFibonacci(num - 2, stepByStep);
        dp[num][0] = result;
        computed[num][0] = true;
        tableModel.setValueAt(result, num, 1);
        
        if (stepByStep) {
            logArea.append("Computed: F(" + num + ") = " + result + "\n");
            memoTable.repaint();
        }
        
        return result;
    }
    
    // 0/1 Knapsack Problem
    private void setupKnapsack() {
        try {
            capacity = Integer.parseInt(tfCapacity.getText());
            if (capacity < 1 || capacity > 20) {
                JOptionPane.showMessageDialog(this, "Please enter capacity between 1 and 20!");
                return;
            }
            
            // Sample items for demonstration
            weights = new int[]{2, 1, 3, 2, 4};
            values = new int[]{3, 2, 4, 2, 5};
            int itemCount = weights.length;
            
            currentProblem = "Knapsack";
            dp = new int[itemCount + 1][capacity + 1];
            computed = new boolean[itemCount + 1][capacity + 1];
            currentStep = 0;
            
            // Setup table
            tableModel.setRowCount(itemCount + 1);
            tableModel.setColumnCount(capacity + 1);
            
            String[] columnNames = new String[capacity + 1];
            columnNames[0] = "Weight\\Cap";
            for (int j = 1; j <= capacity; j++) {
                columnNames[j] = String.valueOf(j);
            }
            tableModel.setColumnIdentifiers(columnNames);
            
            for (int i = 0; i <= itemCount; i++) {
                for (int w = 0; w <= capacity; w++) {
                    if (i == 0 || w == 0) {
                        dp[i][w] = 0;
                        computed[i][w] = true;
                        tableModel.setValueAt("0", i, w);
                    } else {
                        dp[i][w] = -1;
                        computed[i][w] = false;
                        tableModel.setValueAt("?", i, w);
                    }
                }
                if (i == 0) {
                    tableModel.setValueAt("0", i, 0);
                } else {
                    tableModel.setValueAt("Item" + i + "(w:" + weights[i-1] + ",v:" + values[i-1] + ")", i, 0);
                }
            }
            
            logArea.append("=== 0/1 Knapsack Problem ===\n");
            logArea.append("Capacity: " + capacity + "\n");
            logArea.append("Items: ");
            for (int i = 0; i < itemCount; i++) {
                logArea.append("(w:" + weights[i] + ",v:" + values[i] + ") ");
            }
            logArea.append("\nRecurrence: dp[i][w] = max(dp[i-1][w], dp[i-1][w-weight[i]] + value[i])\n");
            statusLabel.setText("Knapsack problem setup complete. Use Step or Solve to compute.");
            complexityLabel.setText("Time: O(n*W), Space: O(n*W)");
            
            memoTable.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for capacity!");
        }
    }
    
    private void solveKnapsack(boolean stepByStep) {
        int itemCount = weights.length;
        
        for (int i = 1; i <= itemCount; i++) {
            for (int w = 1; w <= capacity; w++) {
                if (stepByStep) {
                    logArea.append("Computing dp[" + i + "][" + w + "] for item " + i + 
                                  " (weight:" + weights[i-1] + ", value:" + values[i-1] + ")\n");
                    currentStep = i * 100 + w;
                }
                
                if (weights[i-1] <= w) {
                    int include = dp[i-1][w-weights[i-1]] + values[i-1];
                    int exclude = dp[i-1][w];
                    dp[i][w] = Math.max(include, exclude);
                    
                    if (stepByStep) {
                        logArea.append("  Include: " + include + ", Exclude: " + exclude + 
                                      " -> Choose: " + dp[i][w] + "\n");
                    }
                } else {
                    dp[i][w] = dp[i-1][w];
                    if (stepByStep) {
                        logArea.append("  Item too heavy, take previous: " + dp[i][w] + "\n");
                    }
                }
                
                computed[i][w] = true;
                tableModel.setValueAt(dp[i][w], i, w);
                
                if (stepByStep) {
                    memoTable.repaint();
                    try { Thread.sleep(200); } catch (InterruptedException ex) {}
                }
            }
        }
        
        logArea.append("Maximum value: " + dp[itemCount][capacity] + "\n");
        statusLabel.setText("Knapsack solved! Maximum value: " + dp[itemCount][capacity]);
    }
    
    // Longest Common Subsequence Problem
    private void setupLCS() {
        str1 = tfString1.getText().trim();
        str2 = tfString2.getText().trim();
        
        if (str1.isEmpty() || str2.isEmpty() || str1.length() > 10 || str2.length() > 10) {
            JOptionPane.showMessageDialog(this, "Please enter valid strings (max length 10)!");
            return;
        }
        
        currentProblem = "LCS";
        int m = str1.length();
        int n = str2.length();
        dp = new int[m + 1][n + 1];
        computed = new boolean[m + 1][n + 1];
        currentStep = 0;
        
        // Setup table
        tableModel.setRowCount(m + 1);
        tableModel.setColumnCount(n + 1);
        
        String[] columnNames = new String[n + 1];
        columnNames[0] = " ";
        for (int j = 1; j <= n; j++) {
            columnNames[j] = String.valueOf(str2.charAt(j-1));
        }
        tableModel.setColumnIdentifiers(columnNames);
        
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                    computed[i][j] = true;
                    tableModel.setValueAt("0", i, j);
                } else {
                    dp[i][j] = -1;
                    computed[i][j] = false;
                    tableModel.setValueAt("?", i, j);
                }
            }
            if (i == 0) {
                tableModel.setValueAt(" ", i, 0);
            } else {
                tableModel.setValueAt(String.valueOf(str1.charAt(i-1)), i, 0);
            }
        }
        
        logArea.append("=== Longest Common Subsequence ===\n");
        logArea.append("String 1: " + str1 + "\n");
        logArea.append("String 2: " + str2 + "\n");
        logArea.append("Recurrence: If chars match: dp[i][j] = dp[i-1][j-1] + 1\n");
        logArea.append("            Else: dp[i][j] = max(dp[i-1][j], dp[i][j-1])\n");
        statusLabel.setText("LCS problem setup complete. Use Step or Solve to compute.");
        complexityLabel.setText("Time: O(m*n), Space: O(m*n)");
        
        memoTable.repaint();
    }
    
    private void solveLCS(boolean stepByStep) {
        int m = str1.length();
        int n = str2.length();
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (stepByStep) {
                    logArea.append("Computing dp[" + i + "][" + j + "]: '" + 
                                  str1.charAt(i-1) + "' vs '" + str2.charAt(j-1) + "'\n");
                    currentStep = i * 100 + j;
                }
                
                if (str1.charAt(i-1) == str2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1] + 1;
                    if (stepByStep) {
                        logArea.append("  Characters match! dp[" + i + "][" + j + "] = " + dp[i][j] + "\n");
                    }
                } else {
                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
                    if (stepByStep) {
                        logArea.append("  No match. Take max(" + dp[i-1][j] + ", " + dp[i][j-1] + 
                                      ") = " + dp[i][j] + "\n");
                    }
                }
                
                computed[i][j] = true;
                tableModel.setValueAt(dp[i][j], i, j);
                
                if (stepByStep) {
                    memoTable.repaint();
                    try { Thread.sleep(300); } catch (InterruptedException ex) {}
                }
            }
        }
        
        logArea.append("LCS length: " + dp[m][n] + "\n");
        statusLabel.setText("LCS solved! Length: " + dp[m][n]);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            dispose();
            SwingUtilities.invokeLater(DSAVisualizationMain::new);
        } else if (e.getSource() == btnFibonacci) {
            setupFibonacci();
        } else if (e.getSource() == btnKnapsack) {
            setupKnapsack();
        } else if (e.getSource() == btnLCS) {
            setupLCS();
        } else if (e.getSource() == btnSolve) {
            solveCurrentProblem(false);
        } else if (e.getSource() == btnStep) {
            solveCurrentProblem(true);
        } else if (e.getSource() == btnClear) {
            clearVisualization();
        } else if (e.getSource() == btnHelp) {
            showHelp();
        }
    }
    
    private void solveCurrentProblem(boolean stepByStep) {
        switch (currentProblem) {
            case "Fibonacci":
                solveFibonacci(n, stepByStep);
                break;
            case "Knapsack":
                solveKnapsack(stepByStep);
                break;
            case "LCS":
                solveLCS(stepByStep);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Please select a problem first!");
        }
    }
    
    private void clearVisualization() {
        currentProblem = "None";
        setupEmptyTable();
        logArea.setText("");
        statusLabel.setText("Visualization cleared. Select a DP problem to begin.");
        complexityLabel.setText("Time: O(?), Space: O(?)");
    }
    
    private void showHelp() {
        String helpText = "<html><body style='width: 650px;'>" +
            "<h2>Dynamic Programming Visualization</h2>" +
            "<h3>What is Dynamic Programming?</h3>" +
            "<p>DP is an optimization technique that solves complex problems by breaking them " +
            "into simpler subproblems and storing the results to avoid redundant calculations.</p>" +
            "<h3>Problems Demonstrated:</h3>" +
            "<ul>" +
            "<li><b>Fibonacci:</b> Classic recursive problem with overlapping subproblems</li>" +
            "<li><b>0/1 Knapsack:</b> Optimization problem with weight and value constraints</li>" +
            "<li><b>LCS:</b> Finding longest common subsequence between two strings</li>" +
            "</ul>" +
            "<h3>Key Concepts:</h3>" +
            "<ul>" +
            "<li><b>Overlapping Subproblems:</b> Same subproblems solved multiple times</li>" +
            "<li><b>Optimal Substructure:</b> Optimal solution contains optimal subsolutions</li>" +
            "<li><b>Memoization:</b> Store results to avoid recomputation</li>" +
            "</ul>" +
            "<h3>Visualization Features:</h3>" +
            "<ul>" +
            "<li><b>Memoization Table:</b> Shows computed vs uncomputed states</li>" +
            "<li><b>Step-by-step:</b> Watch how subproblems are solved</li>" +
            "<li><b>Time/Space Complexity:</b> Analysis for each problem</li>" +
            "</ul>" +
            "<h3>Color Coding:</h3>" +
            "<ul>" +
            "<li><b>White:</b> Uncomputed state</li>" +
            "<li><b>Light Green:</b> Computed/memoized state</li>" +
            "<li><b>Yellow:</b> Currently computing</li>" +
            "</ul>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Dynamic Programming Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DynamicProgrammingVisualization::new);
    }
}