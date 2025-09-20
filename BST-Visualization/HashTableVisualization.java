// Hash Table Visualization - Hash functions, collision handling, rehashing
// Features: Multiple hash functions, chaining, open addressing, load factor monitoring

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class HashTableVisualization extends JFrame implements ActionListener {
    private JPanel topPanel, visualPanel, infoPanel;
    private JButton btnInsert, btnSearch, btnDelete, btnRehash, btnClear, btnRandom, btnBack, btnHelp;
    private JTextField tfKey, tfValue, tfSize;
    private JTextArea logArea;
    private JLabel statusLabel, loadFactorLabel, collisionLabel;
    private JComboBox<String> hashFunctionCombo, collisionHandlingCombo;
    
    private HashEntry[] table;
    private int tableSize;
    private int elementCount;
    private String hashFunction;
    private String collisionHandling;
    private java.util.Map<Integer, Color> slotColors;
    private int lastAccessedSlot;
    
    private static class HashEntry {
        String key;
        String value;
        boolean deleted; // For open addressing
        java.util.List<KeyValuePair> chain; // For chaining
        
        HashEntry() {
            chain = new ArrayList<>();
            deleted = false;
        }
        
        static class KeyValuePair {
            String key;
            String value;
            
            KeyValuePair(String key, String value) {
                this.key = key;
                this.value = value;
            }
            
            @Override
            public String toString() {
                return key + ":" + value;
            }
        }
    }
    
    public HashTableVisualization() {
        tableSize = 11; // Start with prime number
        table = new HashEntry[tableSize];
        slotColors = new HashMap<>();
        hashFunction = "Division";
        collisionHandling = "Chaining";
        lastAccessedSlot = -1;
        initializeTable();
        initialize();
    }
    
    private void initialize() {
        setTitle("Hash Table Visualization - Hash Functions & Collision Handling");
        setSize(1400, 800);
        getContentPane().setBackground(new Color(240, 248, 255));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setupPanels();
        setupControls();
        setupInfoPanel();
        updateVisualization();
        
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
                drawHashTable(g);
            }
        };
        visualPanel.setBackground(new Color(248, 248, 255));
        visualPanel.setPreferredSize(new Dimension(getWidth(), 450));
        
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(getWidth(), 200));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Hash Table Statistics & Operations Log"));
        
        add(topPanel, BorderLayout.NORTH);
        add(visualPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupControls() {
        // Hash function selection
        topPanel.add(new JLabel("Hash Function:"));
        hashFunctionCombo = new JComboBox<>(new String[]{"Division", "Multiplication", "Universal"});
        hashFunctionCombo.addActionListener(e -> {
            hashFunction = (String) hashFunctionCombo.getSelectedItem();
            logArea.append("Changed hash function to: " + hashFunction + "\n");
            updateVisualization();
        });
        topPanel.add(hashFunctionCombo);
        
        // Collision handling selection
        topPanel.add(new JLabel("Collision:"));
        collisionHandlingCombo = new JComboBox<>(new String[]{"Chaining", "Linear Probing", "Quadratic Probing", "Double Hashing"});
        collisionHandlingCombo.addActionListener(e -> {
            collisionHandling = (String) collisionHandlingCombo.getSelectedItem();
            logArea.append("Changed collision handling to: " + collisionHandling + "\n");
            clearTable();
            updateVisualization();
        });
        topPanel.add(collisionHandlingCombo);
        
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Data entry
        topPanel.add(new JLabel("Key:"));
        tfKey = new JTextField(8);
        tfKey.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfKey);
        
        topPanel.add(new JLabel("Value:"));
        tfValue = new JTextField(8);
        tfValue.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfValue);
        
        btnInsert = createStyledButton("Insert", new Color(60, 179, 113), 70);
        btnSearch = createStyledButton("Search", new Color(30, 144, 255), 70);
        btnDelete = createStyledButton("Delete", new Color(220, 20, 60), 70);
        btnInsert.addActionListener(this);
        btnSearch.addActionListener(this);
        btnDelete.addActionListener(this);
        topPanel.add(btnInsert);
        topPanel.add(btnSearch);
        topPanel.add(btnDelete);
        
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Table management
        topPanel.add(new JLabel("Size:"));
        tfSize = new JTextField(String.valueOf(tableSize), 3);
        tfSize.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(tfSize);
        
        btnRehash = createStyledButton("Rehash", new Color(138, 43, 226), 70);
        btnRandom = createStyledButton("Random", new Color(184, 134, 11), 70);
        btnClear = createStyledButton("Clear", new Color(255, 69, 0), 60);
        btnHelp = createStyledButton("Help", new Color(70, 130, 180), 60);
        btnBack = createStyledButton("← Back", new Color(105, 105, 105), 70);
        
        btnRehash.addActionListener(this);
        btnRandom.addActionListener(this);
        btnClear.addActionListener(this);
        btnHelp.addActionListener(this);
        btnBack.addActionListener(this);
        
        topPanel.add(btnRehash);
        topPanel.add(btnRandom);
        topPanel.add(btnClear);
        topPanel.add(btnHelp);
        topPanel.add(btnBack);
    }
    
    private void setupInfoPanel() {
        // Statistics panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(new Color(245, 245, 245));
        
        statusLabel = new JLabel("Hash table initialized. Ready for operations.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0, 100, 0));
        
        loadFactorLabel = new JLabel("Load Factor: 0.00");
        loadFactorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        loadFactorLabel.setForeground(new Color(0, 0, 139));
        
        collisionLabel = new JLabel("Collisions: 0");
        collisionLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        collisionLabel.setForeground(new Color(139, 0, 0));
        
        statsPanel.add(statusLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(loadFactorLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(collisionLabel);
        
        // Log area
        logArea = new JTextArea(8, 80);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logArea.setEditable(false);
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        infoPanel.add(statsPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(scrollPane);
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
    
    private void initializeTable() {
        elementCount = 0;
        for (int i = 0; i < tableSize; i++) {
            table[i] = new HashEntry();
        }
        slotColors.clear();
    }
    
    // Hash Functions
    private int hashFunction(String key) {
        switch (hashFunction) {
            case "Division":
                return Math.abs(key.hashCode()) % tableSize;
            case "Multiplication":
                double A = 0.6180339887; // Golden ratio - 1
                double temp = Math.abs(key.hashCode()) * A;
                return (int) (tableSize * (temp - Math.floor(temp)));
            case "Universal":
                int a = 31; // Prime number
                int b = 17; // Another prime
                return Math.abs((a * key.hashCode() + b) % 1009) % tableSize; // 1009 is large prime
            default:
                return Math.abs(key.hashCode()) % tableSize;
        }
    }
    
    private int secondHashFunction(String key) {
        // For double hashing
        int prime = 7; // Should be smaller than table size
        return prime - (Math.abs(key.hashCode()) % prime);
    }
    
    // Hash Table Operations
    private void insertEntry(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid key!");
            return;
        }
        
        key = key.trim();
        value = value.trim();
        
        logArea.append("=== Inserting key: '" + key + "', value: '" + value + "' ===\n");
        
        int index = hashFunction(key);
        logArea.append("Hash function result: " + index + "\n");
        
        if (collisionHandling.equals("Chaining")) {
            insertWithChaining(index, key, value);
        } else {
            insertWithOpenAddressing(index, key, value);
        }
        
        updateStats();
        updateVisualization();
        
        // Check if rehashing is needed
        double loadFactor = (double) elementCount / tableSize;
        if (loadFactor > 0.75) {
            logArea.append("Load factor exceeded 0.75. Consider rehashing.\n");
            statusLabel.setText("High load factor! Consider rehashing for better performance.");
        }
    }
    
    private void insertWithChaining(int index, String key, String value) {
        HashEntry entry = table[index];
        
        // Check if key already exists in chain
        for (HashEntry.KeyValuePair pair : entry.chain) {
            if (pair.key.equals(key)) {
                pair.value = value;
                logArea.append("Updated existing key in chain at index " + index + "\n");
                slotColors.put(index, new Color(255, 255, 0)); // Yellow for update
                lastAccessedSlot = index;
                return;
            }
        }
        
        // Add new key-value pair to chain
        entry.chain.add(new HashEntry.KeyValuePair(key, value));
        elementCount++;
        
        if (entry.chain.size() > 1) {
            logArea.append("Collision handled by chaining at index " + index + 
                          " (chain length: " + entry.chain.size() + ")\n");
            slotColors.put(index, new Color(255, 69, 0)); // Red for collision
        } else {
            logArea.append("Inserted successfully at index " + index + "\n");
            slotColors.put(index, new Color(50, 205, 50)); // Green for success
        }
        
        lastAccessedSlot = index;
    }
    
    private void insertWithOpenAddressing(int index, String key, String value) {
        int originalIndex = index;
        int attempt = 0;
        int step = 1;
        
        if (collisionHandling.equals("Double Hashing")) {
            step = secondHashFunction(key);
        }
        
        while (table[index].key != null && !table[index].deleted) {
            if (table[index].key.equals(key)) {
                table[index].value = value;
                logArea.append("Updated existing key at index " + index + "\n");
                slotColors.put(index, new Color(255, 255, 0)); // Yellow for update
                lastAccessedSlot = index;
                return;
            }
            
            attempt++;
            logArea.append("Collision at index " + index + ", attempt " + attempt + "\n");
            
            // Calculate next index based on probing method
            switch (collisionHandling) {
                case "Linear Probing":
                    index = (originalIndex + attempt) % tableSize;
                    break;
                case "Quadratic Probing":
                    index = (originalIndex + attempt * attempt) % tableSize;
                    break;
                case "Double Hashing":
                    index = (originalIndex + attempt * step) % tableSize;
                    break;
            }
            
            if (attempt >= tableSize) {
                logArea.append("Table is full! Cannot insert.\n");
                statusLabel.setText("Table is full! Rehashing required.");
                return;
            }
        }
        
        table[index].key = key;
        table[index].value = value;
        table[index].deleted = false;
        elementCount++;
        
        if (attempt > 0) {
            logArea.append("Inserted after " + attempt + " collision(s) at index " + index + "\n");
            slotColors.put(index, new Color(255, 165, 0)); // Orange for collision resolution
        } else {
            logArea.append("Inserted successfully at index " + index + "\n");
            slotColors.put(index, new Color(50, 205, 50)); // Green for success
        }
        
        lastAccessedSlot = index;
    }
    
    private void searchEntry(String key) {
        if (key == null || key.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid key!");
            return;
        }
        
        key = key.trim();
        slotColors.clear();
        
        logArea.append("=== Searching for key: '" + key + "' ===\n");
        
        int index = hashFunction(key);
        logArea.append("Hash function result: " + index + "\n");
        
        if (collisionHandling.equals("Chaining")) {
            searchWithChaining(index, key);
        } else {
            searchWithOpenAddressing(index, key);
        }
        
        updateVisualization();
    }
    
    private void searchWithChaining(int index, String key) {
        HashEntry entry = table[index];
        slotColors.put(index, new Color(255, 255, 0)); // Yellow for search
        
        for (HashEntry.KeyValuePair pair : entry.chain) {
            if (pair.key.equals(key)) {
                logArea.append("Key found in chain at index " + index + 
                              " with value: '" + pair.value + "'\n");
                slotColors.put(index, new Color(50, 205, 50)); // Green for found
                statusLabel.setText("Key '" + key + "' found with value: '" + pair.value + "'");
                lastAccessedSlot = index;
                return;
            }
        }
        
        logArea.append("Key not found\n");
        slotColors.put(index, new Color(255, 69, 0)); // Red for not found
        statusLabel.setText("Key '" + key + "' not found in hash table");
        lastAccessedSlot = index;
    }
    
    private void searchWithOpenAddressing(int index, String key) {
        int originalIndex = index;
        int attempt = 0;
        int step = 1;
        
        if (collisionHandling.equals("Double Hashing")) {
            step = secondHashFunction(key);
        }
        
        while (table[index].key != null) {
            slotColors.put(index, new Color(255, 255, 0)); // Yellow for search path
            
            if (!table[index].deleted && table[index].key.equals(key)) {
                logArea.append("Key found at index " + index + " with value: '" + 
                              table[index].value + "'\n");
                slotColors.put(index, new Color(50, 205, 50)); // Green for found
                statusLabel.setText("Key '" + key + "' found with value: '" + table[index].value + "'");
                lastAccessedSlot = index;
                return;
            }
            
            attempt++;
            if (attempt >= tableSize) break;
            
            // Calculate next index
            switch (collisionHandling) {
                case "Linear Probing":
                    index = (originalIndex + attempt) % tableSize;
                    break;
                case "Quadratic Probing":
                    index = (originalIndex + attempt * attempt) % tableSize;
                    break;
                case "Double Hashing":
                    index = (originalIndex + attempt * step) % tableSize;
                    break;
            }
        }
        
        logArea.append("Key not found after " + attempt + " probe(s)\n");
        statusLabel.setText("Key '" + key + "' not found in hash table");
        lastAccessedSlot = -1;
    }
    
    private void rehashTable() {
        try {
            int newSize = Integer.parseInt(tfSize.getText());
            if (newSize < 5 || newSize > 100) {
                JOptionPane.showMessageDialog(this, "Table size must be between 5 and 100!");
                return;
            }
            
            logArea.append("=== Rehashing table from size " + tableSize + " to " + newSize + " ===\n");
            
            // Store old entries
            HashEntry[] oldTable = table;
            int oldSize = tableSize;
            
            // Create new table
            tableSize = newSize;
            table = new HashEntry[tableSize];
            initializeTable();
            
            // Reinsert all elements
            for (int i = 0; i < oldSize; i++) {
                if (collisionHandling.equals("Chaining")) {
                    for (HashEntry.KeyValuePair pair : oldTable[i].chain) {
                        insertEntry(pair.key, pair.value);
                    }
                } else {
                    if (oldTable[i].key != null && !oldTable[i].deleted) {
                        insertEntry(oldTable[i].key, oldTable[i].value);
                    }
                }
            }
            
            logArea.append("Rehashing completed. New table size: " + tableSize + "\n");
            statusLabel.setText("Rehashing completed successfully");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid table size!");
        }
    }
    
    private void generateRandomEntries() {
        String[] keys = {"apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honey"};
        String[] values = {"red", "yellow", "red", "brown", "purple", "green", "purple", "golden"};
        
        clearTable();
        
        for (int i = 0; i < Math.min(keys.length, tableSize - 1); i++) {
            insertEntry(keys[i], values[i]);
        }
        
        logArea.append("Generated random entries\n");
        statusLabel.setText("Random entries generated");
    }
    
    private void clearTable() {
        initializeTable();
        updateStats();
        updateVisualization();
        logArea.append("Hash table cleared\n");
        statusLabel.setText("Hash table cleared. Ready for operations.");
    }
    
    private void updateStats() {
        double loadFactor = (double) elementCount / tableSize;
        loadFactorLabel.setText("Load Factor: " + String.format("%.2f", loadFactor));
        
        int collisions = 0;
        if (collisionHandling.equals("Chaining")) {
            for (HashEntry entry : table) {
                if (entry.chain.size() > 1) {
                    collisions += entry.chain.size() - 1;
                }
            }
        }
        collisionLabel.setText("Collisions: " + collisions);
    }
    
    private void updateVisualization() {
        visualPanel.repaint();
    }
    
    private void drawHashTable(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int panelWidth = visualPanel.getWidth();
        int panelHeight = visualPanel.getHeight();
        
        if (panelWidth <= 0 || panelHeight <= 0) return;
        
        int cols = Math.min(10, tableSize);
        int rows = (tableSize + cols - 1) / cols;
        
        int cellWidth = Math.max(80, (panelWidth - 40) / cols);
        int cellHeight = Math.max(60, (panelHeight - 40) / rows);
        
        int startX = (panelWidth - cols * cellWidth) / 2;
        int startY = 20;
        
        for (int i = 0; i < tableSize; i++) {
            int row = i / cols;
            int col = i % cols;
            int x = startX + col * cellWidth;
            int y = startY + row * cellHeight;
            
            drawHashSlot(g2, x, y, cellWidth - 2, cellHeight - 2, i);
        }
    }
    
    private void drawHashSlot(Graphics2D g2, int x, int y, int width, int height, int index) {
        // Determine slot color
        Color slotColor = slotColors.getOrDefault(index, new Color(230, 230, 250));
        
        // Draw slot background
        g2.setColor(slotColor);
        g2.fillRect(x, y, width, height);
        
        // Draw slot border
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect(x, y, width, height);
        
        // Draw index
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.BLACK);
        g2.drawString("[" + index + "]", x + 5, y + 15);
        
        // Draw content
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        
        if (collisionHandling.equals("Chaining")) {
            drawChainContent(g2, x, y, width, height, index);
        } else {
            drawOpenAddressingContent(g2, x, y, width, height, index);
        }
    }
    
    private void drawChainContent(Graphics2D g2, int x, int y, int width, int height, int index) {
        HashEntry entry = table[index];
        int textY = y + 30;
        
        if (entry.chain.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("empty", x + 5, textY);
        } else {
            for (int i = 0; i < entry.chain.size(); i++) {
                HashEntry.KeyValuePair pair = entry.chain.get(i);
                String text = pair.key + ":" + pair.value;
                if (text.length() > 8) {
                    text = text.substring(0, 8) + "...";
                }
                
                g2.setColor(Color.BLUE);
                g2.drawString(text, x + 5, textY + i * 12);
                
                if (textY + i * 12 > y + height - 5) break; // Prevent overflow
            }
            
            if (entry.chain.size() > 1) {
                g2.setColor(Color.RED);
                g2.drawString("(" + entry.chain.size() + ")", x + width - 25, y + 15);
            }
        }
    }
    
    private void drawOpenAddressingContent(Graphics2D g2, int x, int y, int width, int height, int index) {
        HashEntry entry = table[index];
        int textY = y + 30;
        
        if (entry.key == null) {
            g2.setColor(Color.GRAY);
            g2.drawString("empty", x + 5, textY);
        } else if (entry.deleted) {
            g2.setColor(Color.RED);
            g2.drawString("deleted", x + 5, textY);
        } else {
            String text = entry.key + ":" + entry.value;
            if (text.length() > 8) {
                text = text.substring(0, 8) + "...";
            }
            
            g2.setColor(Color.BLUE);
            g2.drawString(text, x + 5, textY);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            dispose();
            SwingUtilities.invokeLater(DSAVisualizationMain::new);
        } else if (e.getSource() == btnInsert) {
            insertEntry(tfKey.getText(), tfValue.getText());
            tfKey.setText("");
            tfValue.setText("");
        } else if (e.getSource() == btnSearch) {
            searchEntry(tfKey.getText());
        } else if (e.getSource() == btnRehash) {
            rehashTable();
        } else if (e.getSource() == btnRandom) {
            generateRandomEntries();
        } else if (e.getSource() == btnClear) {
            clearTable();
        } else if (e.getSource() == btnHelp) {
            showHelp();
        }
    }
    
    private void showHelp() {
        String helpText = "<html><body style='width: 650px;'>" +
            "<h2>Hash Table Visualization</h2>" +
            "<h3>Hash Functions:</h3>" +
            "<ul>" +
            "<li><b>Division:</b> h(k) = k mod m (simple modulo)</li>" +
            "<li><b>Multiplication:</b> h(k) = floor(m * (kA mod 1))</li>" +
            "<li><b>Universal:</b> h(k) = ((ak + b) mod p) mod m</li>" +
            "</ul>" +
            "<h3>Collision Handling:</h3>" +
            "<ul>" +
            "<li><b>Chaining:</b> Multiple values in same slot using linked list</li>" +
            "<li><b>Linear Probing:</b> Check next slot sequentially</li>" +
            "<li><b>Quadratic Probing:</b> Check slots at quadratic intervals</li>" +
            "<li><b>Double Hashing:</b> Use second hash function for step size</li>" +
            "</ul>" +
            "<h3>Color Coding:</h3>" +
            "<ul>" +
            "<li><b>Light Blue:</b> Empty slots</li>" +
            "<li><b>Green:</b> Successful operation</li>" +
            "<li><b>Yellow:</b> Search path or update</li>" +
            "<li><b>Red:</b> Collision or not found</li>" +
            "<li><b>Orange:</b> Collision resolved</li>" +
            "</ul>" +
            "<h3>Performance:</h3>" +
            "<ul>" +
            "<li><b>Average Case:</b> O(1) for all operations</li>" +
            "<li><b>Worst Case:</b> O(n) when many collisions occur</li>" +
            "<li><b>Load Factor:</b> α = n/m (keep < 0.75 for good performance)</li>" +
            "</ul>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Hash Table Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HashTableVisualization::new);
    }
}