// Trie (Prefix Tree) Visualization - String operations and autocomplete
// Features: Insert, search, prefix matching, autocomplete suggestions

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class TrieVisualization extends JFrame implements ActionListener {
    private JPanel topPanel, visualPanel, infoPanel;
    private JButton btnInsert, btnSearch, btnDelete, btnPrefix, btnClear, btnRandom, btnBack, btnHelp;
    private JTextField tfWord, tfPrefix;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JList<String> suggestionList;
    private DefaultListModel<String> suggestionModel;
    
    private TrieNode root;
    private java.util.Map<TrieNode, NodeDisplay> nodeDisplays;
    private String lastSearchedWord;
    private String lastSearchedPrefix;
    
    private static class TrieNode {
        java.util.Map<Character, TrieNode> children;
        boolean isEndOfWord;
        Color color;
        char character;
        int level;
        
        TrieNode(char c, int level) {
            children = new HashMap<>();
            isEndOfWord = false;
            color = new Color(173, 216, 230); // Light blue
            character = c;
            this.level = level;
        }
        
        void setColor(Color color) {
            this.color = color;
        }
    }
    
    private static class NodeDisplay {
        int x, y;
        int width, height;
        TrieNode node;
        
        NodeDisplay(TrieNode node, int x, int y) {
            this.node = node;
            this.x = x;
            this.y = y;
            this.width = 30;
            this.height = 30;
        }
    }
    
    public TrieVisualization() {
        root = new TrieNode(' ', 0);
        nodeDisplays = new HashMap<>();
        initialize();
        insertSampleWords();
    }
    
    private void initialize() {
        setTitle("Trie (Prefix Tree) Visualization - String Operations & Autocomplete");
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
        
        visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTrie(g);
            }
        };
        visualPanel.setBackground(new Color(248, 248, 255));
        visualPanel.setPreferredSize(new Dimension(getWidth(), 450));
        
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(getWidth(), 200));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Operations Log & Autocomplete"));
        
        add(topPanel, BorderLayout.NORTH);
        add(visualPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupControls() {
        // Word operations
        topPanel.add(new JLabel("Word:"));
        tfWord = new JTextField(15);
        tfWord.setFont(new Font("Arial", Font.PLAIN, 12));
        tfWord.addActionListener(this); // Allow Enter key
        topPanel.add(tfWord);
        
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
        
        // Prefix operations
        topPanel.add(new JLabel("Prefix:"));
        tfPrefix = new JTextField(10);
        tfPrefix.setFont(new Font("Arial", Font.PLAIN, 12));
        tfPrefix.addActionListener(e -> findPrefix()); // Allow Enter key
        topPanel.add(tfPrefix);
        
        btnPrefix = createStyledButton("Find Prefix", new Color(138, 43, 226), 90);
        btnPrefix.addActionListener(this);
        topPanel.add(btnPrefix);
        
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Utility buttons
        btnRandom = createStyledButton("Sample Words", new Color(184, 134, 11), 100);
        btnClear = createStyledButton("Clear", new Color(255, 69, 0), 60);
        btnHelp = createStyledButton("Help", new Color(70, 130, 180), 60);
        btnBack = createStyledButton("← Back", new Color(105, 105, 105), 70);
        
        btnRandom.addActionListener(this);
        btnClear.addActionListener(this);
        btnHelp.addActionListener(this);
        btnBack.addActionListener(this);
        
        topPanel.add(btnRandom);
        topPanel.add(btnClear);
        topPanel.add(btnHelp);
        topPanel.add(btnBack);
    }
    
    private void setupInfoPanel() {
        // Left panel for log
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Operations Log"));
        
        statusLabel = new JLabel("Trie initialized. Insert words to build the prefix tree.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0, 100, 0));
        
        logArea = new JTextArea(8, 40);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logArea.setEditable(false);
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        
        leftPanel.add(statusLabel, BorderLayout.NORTH);
        leftPanel.add(logScrollPane, BorderLayout.CENTER);
        
        // Right panel for autocomplete suggestions
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Autocomplete Suggestions"));
        rightPanel.setPreferredSize(new Dimension(300, 200));
        
        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        suggestionList.setFont(new Font("Arial", Font.PLAIN, 12));
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = suggestionList.getSelectedValue();
                if (selected != null) {
                    tfWord.setText(selected);
                }
            }
        });
        JScrollPane suggestionScrollPane = new JScrollPane(suggestionList);
        
        rightPanel.add(new JLabel("Click to select:"), BorderLayout.NORTH);
        rightPanel.add(suggestionScrollPane, BorderLayout.CENTER);
        
        infoPanel.add(leftPanel, BorderLayout.CENTER);
        infoPanel.add(rightPanel, BorderLayout.EAST);
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
    
    // Trie operations
    private void insertWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid word!");
            return;
        }
        
        word = word.toLowerCase().trim();
        TrieNode current = root;
        
        logArea.append("Inserting word: '" + word + "'\n");
        
        for (char c : word.toCharArray()) {
            if (!current.children.containsKey(c)) {
                current.children.put(c, new TrieNode(c, current.level + 1));
                logArea.append("  Created new node for character: " + c + "\n");
            }
            current = current.children.get(c);
        }
        
        if (current.isEndOfWord) {
            logArea.append("  Word '" + word + "' already exists in trie\n");
            statusLabel.setText("Word '" + word + "' already exists in the trie");
        } else {
            current.isEndOfWord = true;
            logArea.append("  Marked end of word for: " + word + "\n");
            statusLabel.setText("Successfully inserted '" + word + "' into the trie");
        }
        
        updateVisualization();
        tfWord.setText("");
    }
    
    private boolean searchWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid word!");
            return false;
        }
        
        word = word.toLowerCase().trim();
        lastSearchedWord = word;
        resetNodeColors();
        
        TrieNode current = root;
        java.util.List<TrieNode> path = new ArrayList<>();
        
        logArea.append("Searching for word: '" + word + "'\n");
        
        for (char c : word.toCharArray()) {
            if (!current.children.containsKey(c)) {
                logArea.append("  Character '" + c + "' not found. Word does not exist.\n");
                statusLabel.setText("Word '" + word + "' not found in the trie");
                updateVisualization();
                return false;
            }
            current = current.children.get(c);
            path.add(current);
            current.setColor(new Color(255, 255, 0)); // Yellow for search path
        }
        
        boolean found = current.isEndOfWord;
        if (found) {
            current.setColor(new Color(50, 205, 50)); // Green for found word
            logArea.append("  Word '" + word + "' found in trie!\n");
            statusLabel.setText("Word '" + word + "' found in the trie");
        } else {
            current.setColor(new Color(255, 69, 0)); // Red for prefix only
            logArea.append("  Prefix '" + word + "' exists but not as complete word\n");
            statusLabel.setText("'" + word + "' exists as prefix but not as complete word");
        }
        
        updateVisualization();
        return found;
    }
    
    private void findPrefix() {
        String prefix = tfPrefix.getText();
        if (prefix == null || prefix.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid prefix!");
            return;
        }
        
        prefix = prefix.toLowerCase().trim();
        lastSearchedPrefix = prefix;
        resetNodeColors();
        
        TrieNode current = root;
        
        logArea.append("Finding words with prefix: '" + prefix + "'\n");
        
        // Navigate to prefix node
        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                logArea.append("  Prefix '" + prefix + "' not found in trie\n");
                statusLabel.setText("No words found with prefix '" + prefix + "'");
                suggestionModel.clear();
                updateVisualization();
                return;
            }
            current = current.children.get(c);
            current.setColor(new Color(255, 255, 0)); // Yellow for prefix path
        }
        
        // Find all words with this prefix
        java.util.List<String> suggestions = new ArrayList<>();
        findAllWordsWithPrefix(current, prefix, suggestions);
        
        // Update suggestions list
        suggestionModel.clear();
        for (String suggestion : suggestions) {
            suggestionModel.addElement(suggestion);
        }
        
        // Highlight prefix path and suggestions
        highlightPrefixSuggestions(current, new Color(144, 238, 144)); // Light green
        
        logArea.append("  Found " + suggestions.size() + " words with prefix '" + prefix + "'\n");
        for (String suggestion : suggestions) {
            logArea.append("    - " + suggestion + "\n");
        }
        
        statusLabel.setText("Found " + suggestions.size() + " words with prefix '" + prefix + "'");
        updateVisualization();
    }
    
    private void findAllWordsWithPrefix(TrieNode node, String currentWord, java.util.List<String> results) {
        if (node.isEndOfWord) {
            results.add(currentWord);
        }
        
        for (java.util.Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            findAllWordsWithPrefix(entry.getValue(), currentWord + entry.getKey(), results);
        }
    }
    
    private void highlightPrefixSuggestions(TrieNode prefixNode, Color color) {
        highlightSubtree(prefixNode, color);
    }
    
    private void highlightSubtree(TrieNode node, Color color) {
        node.setColor(color);
        for (TrieNode child : node.children.values()) {
            highlightSubtree(child, color);
        }
    }
    
    private void resetNodeColors() {
        resetNodeColorsRecursive(root);
    }
    
    private void resetNodeColorsRecursive(TrieNode node) {
        node.setColor(new Color(173, 216, 230)); // Light blue
        for (TrieNode child : node.children.values()) {
            resetNodeColorsRecursive(child);
        }
    }
    
    private void clearTrie() {
        root = new TrieNode(' ', 0);
        nodeDisplays.clear();
        suggestionModel.clear();
        logArea.append("Trie cleared\n");
        statusLabel.setText("Trie cleared. Ready for new words.");
        updateVisualization();
    }
    
    private void insertSampleWords() {
        String[] sampleWords = {"cat", "car", "card", "care", "careful", "cars", 
                               "dog", "dodge", "door", "doors", "tree", "try", "trying"};
        
        for (String word : sampleWords) {
            insertWordSilent(word);
        }
        
        logArea.append("Inserted sample words: " + String.join(", ", sampleWords) + "\n");
        statusLabel.setText("Sample words inserted. Try searching or finding prefixes!");
        updateVisualization();
    }
    
    private void insertWordSilent(String word) {
        if (word == null || word.trim().isEmpty()) return;
        
        word = word.toLowerCase().trim();
        TrieNode current = root;
        
        for (char c : word.toCharArray()) {
            if (!current.children.containsKey(c)) {
                current.children.put(c, new TrieNode(c, current.level + 1));
            }
            current = current.children.get(c);
        }
        
        current.isEndOfWord = true;
    }
    
    private void updateVisualization() {
        nodeDisplays.clear();
        calculateNodePositions();
        visualPanel.repaint();
    }
    
    private void calculateNodePositions() {
        int panelWidth = visualPanel.getWidth() > 0 ? visualPanel.getWidth() : 1200;
        int startX = panelWidth / 2;
        int startY = 50;
        
        if (root.children.isEmpty()) return;
        
        // Calculate positions using level-order traversal
        java.util.Queue<TrieNode> queue = new LinkedList<>();
        java.util.Queue<Integer> xPositions = new LinkedList<>();
        java.util.Queue<Integer> yPositions = new LinkedList<>();
        
        // Add root children to start
        int childCount = root.children.size();
        int spacing = Math.max(50, panelWidth / (childCount + 1));
        int currentX = startX - (childCount - 1) * spacing / 2;
        
        for (TrieNode child : root.children.values()) {
            queue.offer(child);
            xPositions.offer(currentX);
            yPositions.offer(startY);
            currentX += spacing;
        }
        
        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            int x = xPositions.poll();
            int y = yPositions.poll();
            
            nodeDisplays.put(current, new NodeDisplay(current, x, y));
            
            // Add children
            if (!current.children.isEmpty()) {
                int numChildren = current.children.size();
                int childSpacing = Math.max(40, spacing / 2);
                int childStartX = x - (numChildren - 1) * childSpacing / 2;
                
                for (TrieNode child : current.children.values()) {
                    queue.offer(child);
                    xPositions.offer(childStartX);
                    yPositions.offer(y + 80);
                    childStartX += childSpacing;
                }
            }
        }
    }
    
    private void drawTrie(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw connections first
        drawConnections(g2);
        
        // Draw nodes
        for (NodeDisplay display : nodeDisplays.values()) {
            drawNode(g2, display);
        }
    }
    
    private void drawConnections(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(Color.GRAY);
        
        for (NodeDisplay parentDisplay : nodeDisplays.values()) {
            TrieNode parent = parentDisplay.node;
            for (TrieNode child : parent.children.values()) {
                NodeDisplay childDisplay = nodeDisplays.get(child);
                if (childDisplay != null) {
                    g2.drawLine(
                        parentDisplay.x + parentDisplay.width / 2,
                        parentDisplay.y + parentDisplay.height,
                        childDisplay.x + childDisplay.width / 2,
                        childDisplay.y
                    );
                }
            }
        }
    }
    
    private void drawNode(Graphics2D g2, NodeDisplay display) {
        TrieNode node = display.node;
        
        // Draw node circle
        g2.setColor(node.color);
        g2.fillOval(display.x, display.y, display.width, display.height);
        
        // Draw border (thicker for end-of-word nodes)
        if (node.isEndOfWord) {
            g2.setStroke(new BasicStroke(3.0f));
            g2.setColor(Color.BLACK);
        } else {
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(Color.DARK_GRAY);
        }
        g2.drawOval(display.x, display.y, display.width, display.height);
        
        // Draw character
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        String text = String.valueOf(node.character);
        int textX = display.x + (display.width - fm.stringWidth(text)) / 2;
        int textY = display.y + (display.height + fm.getAscent()) / 2;
        g2.drawString(text, textX, textY);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            dispose();
            SwingUtilities.invokeLater(DSAVisualizationMain::new);
        } else if (e.getSource() == btnInsert || e.getSource() == tfWord) {
            insertWord(tfWord.getText());
        } else if (e.getSource() == btnSearch) {
            searchWord(tfWord.getText());
        } else if (e.getSource() == btnPrefix) {
            findPrefix();
        } else if (e.getSource() == btnClear) {
            clearTrie();
        } else if (e.getSource() == btnRandom) {
            clearTrie();
            insertSampleWords();
        } else if (e.getSource() == btnHelp) {
            showHelp();
        }
    }
    
    private void showHelp() {
        String helpText = "<html><body style='width: 650px;'>" +
            "<h2>Trie (Prefix Tree) Visualization</h2>" +
            "<h3>What is a Trie?</h3>" +
            "<p>A Trie is a tree-like data structure used for storing strings efficiently. " +
            "It's particularly useful for autocomplete, spell checkers, and prefix matching.</p>" +
            "<h3>Operations:</h3>" +
            "<ul>" +
            "<li><b>Insert:</b> Add a word to the trie</li>" +
            "<li><b>Search:</b> Check if a word exists in the trie</li>" +
            "<li><b>Delete:</b> Remove a word from the trie</li>" +
            "<li><b>Find Prefix:</b> Find all words starting with given prefix</li>" +
            "</ul>" +
            "<h3>Visual Elements:</h3>" +
            "<ul>" +
            "<li><b>Circle Nodes:</b> Each character in the trie</li>" +
            "<li><b>Thick Border:</b> End of word marker</li>" +
            "<li><b>Yellow Path:</b> Search/prefix path</li>" +
            "<li><b>Green Highlight:</b> Found words/suggestions</li>" +
            "<li><b>Red Highlight:</b> Prefix exists but not complete word</li>" +
            "</ul>" +
            "<h3>Time Complexity:</h3>" +
            "<ul>" +
            "<li><b>Insert/Search/Delete:</b> O(m) where m = word length</li>" +
            "<li><b>Prefix Search:</b> O(p + n) where p = prefix length, n = results</li>" +
            "</ul>" +
            "<h3>Space Complexity:</h3>" +
            "<ul>" +
            "<li><b>Storage:</b> O(ALPHABET_SIZE * N * M) worst case</li>" +
            "<li><b>Typical:</b> Much better due to shared prefixes</li>" +
            "</ul>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Trie Visualization Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrieVisualization::new);
    }
}