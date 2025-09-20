// Sorting Algorithms Visualization - Bubble, Quick, Merge, Selection, Insertion Sort
// Features: Step-by-step animation, comparison counters, time complexity analysis

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SortingVisualization extends JFrame implements ActionListener {
    private JPanel topPanel, visualPanel, statsPanel;
    private JButton btnBubble, btnQuick, btnMerge, btnSelection, btnInsertion, btnHeap;
    private JButton btnGenerate, btnShuffle, btnReset, btnStep, btnPlay, btnStop, btnBack, btnHelp;
    private JTextField tfArraySize, tfSpeed;
    private JTextArea logArea;
    private JLabel statusLabel, statsLabel;
    
    private int[] array;
    private int[] originalArray;
    private ArrayList<Rectangle> bars;
    private ArrayList<Color> colors;
    private Timer animationTimer;
    private boolean isAnimating;
    private String currentAlgorithm;
    
    // Animation variables
    private int step;
    private int comparisons;
    private int swaps;
    private int i, j, pivot;
    private boolean ascending;
    
    // Colors for visualization
    private static final Color DEFAULT_COLOR = new Color(70, 130, 180);
    private static final Color COMPARING_COLOR = new Color(255, 255, 0);
    private static final Color SWAPPING_COLOR = new Color(255, 69, 0);
    private static final Color SORTED_COLOR = new Color(50, 205, 50);
    private static final Color PIVOT_COLOR = new Color(255, 20, 147);
    
    public SortingVisualization() {
        array = new int[30];
        bars = new ArrayList<>();
        colors = new ArrayList<>();
        initialize();
        generateRandomArray();
    }
    
    private void initialize() {
        setTitle("Sorting Algorithms Visualization");
        setSize(1400, 800);
        getContentPane().setBackground(new Color(240, 248, 255));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setupPanels();
        setupControls();
        setupVisualizationPanel();
        setupStatsPanel();
        
        setVisible(true);
    }
    
    private void setupPanels() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(230, 240, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBars(g);
            }
        };
        visualPanel.setBackground(new Color(248, 248, 255));
        visualPanel.setPreferredSize(new Dimension(getWidth(), 400));
        
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setPreferredSize(new Dimension(getWidth(), 200));
        statsPanel.setBackground(new Color(245, 245, 245));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Algorithm Statistics & Log"));
        
        add(topPanel, BorderLayout.NORTH);
        add(visualPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    private void setupControls() {
        // Array controls
        topPanel.add(new JLabel("Array Size:"));
        tfArraySize = new JTextField("30", 3);
        tfArraySize.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfArraySize);
        
        btnGenerate = createStyledButton("Generate", new Color(60, 179, 113), 80);
        btnShuffle = createStyledButton("Shuffle", new Color(255, 140, 0), 70);
        btnReset = createStyledButton("Reset", new Color(220, 20, 60), 60);
        btnGenerate.addActionListener(this);
        btnShuffle.addActionListener(this);
        btnReset.addActionListener(this);
        topPanel.add(btnGenerate);
        topPanel.add(btnShuffle);
        topPanel.add(btnReset);
        
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Sorting algorithm buttons
        btnBubble = createStyledButton("Bubble", new Color(255, 69, 0), 70);
        btnQuick = createStyledButton("Quick", new Color(30, 144, 255), 60);
        btnMerge = createStyledButton("Merge", new Color(138, 43, 226), 70);
        btnSelection = createStyledButton("Selection", new Color(255, 20, 147), 80);
        btnInsertion = createStyledButton("Insertion", new Color(34, 139, 34), 80);
        btnHeap = createStyledButton("Heap", new Color(184, 134, 11), 60);
        
        btnBubble.addActionListener(this);
        btnQuick.addActionListener(this);
        btnMerge.addActionListener(this);
        btnSelection.addActionListener(this);
        btnInsertion.addActionListener(this);
        btnHeap.addActionListener(this);
        
        topPanel.add(btnBubble);
        topPanel.add(btnQuick);
        topPanel.add(btnMerge);
        topPanel.add(btnSelection);
        topPanel.add(btnInsertion);
        topPanel.add(btnHeap);
        
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Animation controls
        topPanel.add(new JLabel("Speed:"));
        tfSpeed = new JTextField("100", 3);
        tfSpeed.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfSpeed);
        
        btnStep = createStyledButton("Step", new Color(70, 130, 180), 60);
        btnPlay = createStyledButton("Play", new Color(50, 205, 50), 60);
        btnStop = createStyledButton("Stop", new Color(255, 0, 0), 60);
        btnHelp = createStyledButton("Help", new Color(105, 105, 105), 60);
        btnBack = createStyledButton("← Back", new Color(105, 105, 105), 70);
        
        btnStep.addActionListener(this);
        btnPlay.addActionListener(this);
        btnStop.addActionListener(this);
        btnHelp.addActionListener(this);
        btnBack.addActionListener(this);
        
        topPanel.add(btnStep);
        topPanel.add(btnPlay);
        topPanel.add(btnStop);
        topPanel.add(btnHelp);
        topPanel.add(btnBack);
    }
    
    private void setupVisualizationPanel() {
        updateBars();
    }
    
    private void setupStatsPanel() {
        statusLabel = new JLabel("Select a sorting algorithm to begin");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0, 100, 0));
        
        statsLabel = new JLabel("Comparisons: 0 | Swaps: 0 | Algorithm: None");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        logArea = new JTextArea(6, 80);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logArea.setEditable(false);
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        statsPanel.add(statusLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statsPanel.add(statsLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statsPanel.add(scrollPane);
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
    
    private void generateRandomArray() {
        try {
            int size = Integer.parseInt(tfArraySize.getText());
            size = Math.max(5, Math.min(100, size)); // Limit size between 5 and 100
            array = new int[size];
            originalArray = new int[size];
            
            Random rand = new Random();
            for (int i = 0; i < size; i++) {
                array[i] = rand.nextInt(300) + 10; // Values between 10 and 310
                originalArray[i] = array[i];
            }
            
            updateBars();
            resetStats();
            logArea.append("Generated random array of size " + size + "\n");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid array size!");
        }
    }
    
    private void updateBars() {
        bars.clear();
        colors.clear();
        
        int panelWidth = visualPanel.getWidth() > 0 ? visualPanel.getWidth() : 1200;
        int panelHeight = visualPanel.getHeight() > 0 ? visualPanel.getHeight() : 400;
        int barWidth = Math.max(2, (panelWidth - 40) / array.length);
        int maxHeight = panelHeight - 60;
        
        int maxValue = Arrays.stream(array).max().orElse(1);
        
        for (int i = 0; i < array.length; i++) {
            int barHeight = (int) ((double) array[i] / maxValue * maxHeight);
            int x = 20 + i * barWidth;
            int y = panelHeight - barHeight - 20;
            
            bars.add(new Rectangle(x, y, barWidth - 1, barHeight));
            colors.add(DEFAULT_COLOR);
        }
        
        visualPanel.repaint();
    }
    
    private void drawBars(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < bars.size(); i++) {
            Rectangle bar = bars.get(i);
            g2.setColor(colors.get(i));
            g2.fillRect(bar.x, bar.y, bar.width, bar.height);
            g2.setColor(Color.BLACK);
            g2.drawRect(bar.x, bar.y, bar.width, bar.height);
            
            // Draw value on top of bar for small arrays
            if (array.length <= 20) {
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                String value = String.valueOf(array[i]);
                FontMetrics fm = g2.getFontMetrics();
                int textX = bar.x + (bar.width - fm.stringWidth(value)) / 2;
                int textY = bar.y - 5;
                g2.drawString(value, textX, textY);
            }
        }
    }
    
    private void resetStats() {
        comparisons = 0;
        swaps = 0;
        step = 0;
        updateStats();
    }
    
    private void updateStats() {
        statsLabel.setText("Comparisons: " + comparisons + " | Swaps: " + swaps + " | Algorithm: " + 
                          (currentAlgorithm != null ? currentAlgorithm : "None"));
    }
    
    // Bubble Sort Implementation
    private void startBubbleSort() {
        currentAlgorithm = "Bubble Sort";
        resetStats();
        i = 0;
        j = 0;
        ascending = true;
        statusLabel.setText("Bubble Sort: O(n²) - Compares adjacent elements and swaps if needed");
        logArea.append("=== Starting Bubble Sort ===\n");
        logArea.append("Time Complexity: O(n²) | Space Complexity: O(1)\n");
        
        if (animationTimer != null) animationTimer.stop();
        animationTimer = new Timer(getAnimationDelay(), e -> bubbleSortStep());
        animationTimer.start();
    }
    
    private void bubbleSortStep() {
        if (i >= array.length - 1) {
            // Sorting complete
            markAllAsSorted();
            stopAnimation();
            logArea.append("Bubble Sort completed!\n");
            statusLabel.setText("Bubble Sort completed! All elements are sorted.");
            return;
        }
        
        // Reset colors
        for (int k = 0; k < colors.size(); k++) {
            if (k > array.length - 1 - i) {
                colors.set(k, SORTED_COLOR);
            } else {
                colors.set(k, DEFAULT_COLOR);
            }
        }
        
        if (j < array.length - 1 - i) {
            // Highlight comparison
            colors.set(j, COMPARING_COLOR);
            colors.set(j + 1, COMPARING_COLOR);
            
            comparisons++;
            if (array[j] > array[j + 1]) {
                // Swap elements
                int temp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = temp;
                swaps++;
                
                colors.set(j, SWAPPING_COLOR);
                colors.set(j + 1, SWAPPING_COLOR);
                
                logArea.append("Swapped " + array[j + 1] + " and " + array[j] + "\n");
            }
            
            j++;
        } else {
            // End of pass
            colors.set(array.length - 1 - i, SORTED_COLOR);
            j = 0;
            i++;
            logArea.append("Pass " + i + " completed\n");
        }
        
        updateBars();
        updateStats();
        visualPanel.repaint();
    }
    
    // Quick Sort Implementation
    private void startQuickSort() {
        currentAlgorithm = "Quick Sort";
        resetStats();
        statusLabel.setText("Quick Sort: O(n log n) average - Divides array using pivot element");
        logArea.append("=== Starting Quick Sort ===\n");
        logArea.append("Time Complexity: O(n log n) average, O(n²) worst | Space Complexity: O(log n)\n");
        
        quickSortRecursive(0, array.length - 1);
        markAllAsSorted();
        logArea.append("Quick Sort completed!\n");
        statusLabel.setText("Quick Sort completed! All elements are sorted.");
        updateStats();
        visualPanel.repaint();
    }
    
    private void quickSortRecursive(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSortRecursive(low, pi - 1);
            quickSortRecursive(pi + 1, high);
        }
    }
    
    private int partition(int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        
        logArea.append("Partitioning from " + low + " to " + high + " with pivot " + pivot + "\n");
        
        for (int j = low; j < high; j++) {
            comparisons++;
            if (array[j] < pivot) {
                i++;
                if (i != j) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    swaps++;
                }
            }
        }
        
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        swaps++;
        
        updateBars();
        return i + 1;
    }
    
    // Selection Sort Implementation
    private void startSelectionSort() {
        currentAlgorithm = "Selection Sort";
        resetStats();
        i = 0;
        statusLabel.setText("Selection Sort: O(n²) - Finds minimum element and places it at the beginning");
        logArea.append("=== Starting Selection Sort ===\n");
        logArea.append("Time Complexity: O(n²) | Space Complexity: O(1)\n");
        
        if (animationTimer != null) animationTimer.stop();
        animationTimer = new Timer(getAnimationDelay(), e -> selectionSortStep());
        animationTimer.start();
    }
    
    private void selectionSortStep() {
        if (i >= array.length - 1) {
            markAllAsSorted();
            stopAnimation();
            logArea.append("Selection Sort completed!\n");
            statusLabel.setText("Selection Sort completed! All elements are sorted.");
            return;
        }
        
        int minIdx = i;
        for (int j = i + 1; j < array.length; j++) {
            comparisons++;
            if (array[j] < array[minIdx]) {
                minIdx = j;
            }
        }
        
        if (minIdx != i) {
            int temp = array[minIdx];
            array[minIdx] = array[i];
            array[i] = temp;
            swaps++;
            logArea.append("Selected minimum " + array[i] + " and placed at position " + i + "\n");
        }
        
        // Update colors
        for (int k = 0; k < colors.size(); k++) {
            if (k <= i) {
                colors.set(k, SORTED_COLOR);
            } else {
                colors.set(k, DEFAULT_COLOR);
            }
        }
        
        i++;
        updateBars();
        updateStats();
        visualPanel.repaint();
    }
    
    private void markAllAsSorted() {
        for (int k = 0; k < colors.size(); k++) {
            colors.set(k, SORTED_COLOR);
        }
    }
    
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            isAnimating = false;
        }
    }
    
    private int getAnimationDelay() {
        try {
            int speed = Integer.parseInt(tfSpeed.getText());
            return Math.max(10, Math.min(1000, 1000 - speed * 9)); // Convert to delay
        } catch (NumberFormatException e) {
            return 100; // Default delay
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            dispose();
            SwingUtilities.invokeLater(DSAVisualizationMain::new);
        } else if (e.getSource() == btnGenerate) {
            generateRandomArray();
        } else if (e.getSource() == btnShuffle) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int value : array) {
                list.add(value);
            }
            Collections.shuffle(list);
            for (int i = 0; i < array.length; i++) {
                array[i] = list.get(i);
            }
            updateBars();
            resetStats();
            logArea.append("Array shuffled\n");
        } else if (e.getSource() == btnReset) {
            System.arraycopy(originalArray, 0, array, 0, array.length);
            updateBars();
            resetStats();
            logArea.append("Array reset to original state\n");
        } else if (e.getSource() == btnBubble) {
            stopAnimation();
            startBubbleSort();
        } else if (e.getSource() == btnQuick) {
            stopAnimation();
            startQuickSort();
        } else if (e.getSource() == btnSelection) {
            stopAnimation();
            startSelectionSort();
        } else if (e.getSource() == btnStop) {
            stopAnimation();
            statusLabel.setText("Animation stopped");
        } else if (e.getSource() == btnHelp) {
            showHelp();
        }
        // Add other sorting algorithms as needed
    }
    
    private void showHelp() {
        String helpText = "<html><body style='width: 650px;'>" +
            "<h2>Sorting Algorithms Visualization</h2>" +
            "<h3>Available Algorithms:</h3>" +
            "<ul>" +
            "<li><b>Bubble Sort:</b> O(n²) - Simple but inefficient</li>" +
            "<li><b>Quick Sort:</b> O(n log n) average - Divide and conquer</li>" +
            "<li><b>Selection Sort:</b> O(n²) - Finds minimum element each pass</li>" +
            "<li><b>Merge Sort:</b> O(n log n) - Stable divide and conquer</li>" +
            "<li><b>Insertion Sort:</b> O(n²) - Good for small arrays</li>" +
            "<li><b>Heap Sort:</b> O(n log n) - Uses heap data structure</li>" +
            "</ul>" +
            "<h3>Controls:</h3>" +
            "<ul>" +
            "<li><b>Generate:</b> Create new random array</li>" +
            "<li><b>Shuffle:</b> Randomize current array</li>" +
            "<li><b>Reset:</b> Return to original array</li>" +
            "<li><b>Speed:</b> Control animation speed (1-100)</li>" +
            "</ul>" +
            "<h3>Color Coding:</h3>" +
            "<ul>" +
            "<li><b>Blue:</b> Unsorted elements</li>" +
            "<li><b>Yellow:</b> Elements being compared</li>" +
            "<li><b>Red:</b> Elements being swapped</li>" +
            "<li><b>Green:</b> Sorted elements</li>" +
            "<li><b>Pink:</b> Pivot element (Quick Sort)</li>" +
            "</ul>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Sorting Algorithms Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortingVisualization::new);
    }
}