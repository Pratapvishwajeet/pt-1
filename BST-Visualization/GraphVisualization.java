// Graph Algorithms Visualization - BFS, DFS, Dijkstra's Algorithm
// Features: Interactive graph creation, path finding, traversal animations

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GraphVisualization extends JFrame implements ActionListener, MouseListener {
    private java.util.Map<String, GraphNode> nodes;
    private java.util.List<GraphEdge> edges;
    private JPanel topPanel, graphPanel, infoPanel;
    private JPanel topLeftPanel, topRightPanel;
    private JButton btnAddNode, btnAddEdge, btnBFS, btnDFS, btnDijkstra, btnClear, btnRandom, btnBack, btnHelp;
    private JTextField tfNode, tfFrom, tfTo, tfWeight;
    private JTextArea logArea;
    private JLabel statusLabel;
    private GraphNode selectedNode;
    private GraphNode startNode, endNode;
    private String currentAlgorithm;
    private boolean isDirected;
    private JCheckBox directedCheckBox;

    private static class GraphNode {
        String name;
        int x, y;
        Color color;
        boolean visited;
        int distance;
        GraphNode parent;
        JLabel label;

        GraphNode(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.color = new Color(173, 216, 230); // Light blue
            this.visited = false;
            this.distance = Integer.MAX_VALUE;
            this.parent = null;
            
            label = new JLabel(name, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            label.setOpaque(true);
            label.setBackground(color);
            label.setBounds(x - 20, y - 20, 40, 40);
        }
        
        void setColor(Color color) {
            this.color = color;
            label.setBackground(color);
        }
        
        void reset() {
            visited = false;
            distance = Integer.MAX_VALUE;
            parent = null;
            setColor(new Color(173, 216, 230));
        }
    }

    private static class GraphEdge {
        GraphNode from, to;
        int weight;
        Color color;
        boolean isDirected;

        GraphEdge(GraphNode from, GraphNode to, int weight, boolean isDirected) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.isDirected = isDirected;
            this.color = Color.BLACK;
        }
    }

    public GraphVisualization() {
        nodes = new HashMap<>();
        edges = new ArrayList<>();
        isDirected = false;
        initialize();
    }

    private void initialize() {
        setTitle("Graph Algorithms Visualization - BFS, DFS, Dijkstra");
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

        topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topLeftPanel.setBackground(new Color(230, 240, 250));
        topPanel.add(topLeftPanel, BorderLayout.WEST);

        topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        topRightPanel.setBackground(new Color(230, 240, 250));
        topPanel.add(topRightPanel, BorderLayout.EAST);

        graphPanel = new JPanel(null);
        graphPanel.setBackground(new Color(248, 248, 255));
        graphPanel.addMouseListener(this);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(getWidth(), 150));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Algorithm Execution Log"));

        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void setupControls() {
        // Graph type
        directedCheckBox = new JCheckBox("Directed Graph");
        directedCheckBox.setBackground(new Color(230, 240, 250));
        directedCheckBox.addActionListener(e -> isDirected = directedCheckBox.isSelected());
        topLeftPanel.add(directedCheckBox);

        // Node operations
        topLeftPanel.add(new JLabel("Node:"));
        tfNode = new JTextField(3);
        tfNode.setFont(new Font("Arial", Font.PLAIN, 12));
        topLeftPanel.add(tfNode);

        btnAddNode = createStyledButton("Add Node", new Color(60, 179, 113), 80);
        btnAddNode.addActionListener(this);
        topLeftPanel.add(btnAddNode);

        // Edge operations
        topLeftPanel.add(new JLabel(" | From:"));
        tfFrom = new JTextField(2);
        tfFrom.setFont(new Font("Arial", Font.PLAIN, 12));
        topLeftPanel.add(tfFrom);

        topLeftPanel.add(new JLabel("To:"));
        tfTo = new JTextField(2);
        tfTo.setFont(new Font("Arial", Font.PLAIN, 12));
        topLeftPanel.add(tfTo);

        topLeftPanel.add(new JLabel("Weight:"));
        tfWeight = new JTextField(3);
        tfWeight.setFont(new Font("Arial", Font.PLAIN, 12));
        tfWeight.setText("1");
        topLeftPanel.add(tfWeight);

        btnAddEdge = createStyledButton("Add Edge", new Color(30, 144, 255), 80);
        btnAddEdge.addActionListener(this);
        topLeftPanel.add(btnAddEdge);

        // Algorithm buttons
        btnBFS = createStyledButton("BFS", new Color(255, 140, 0), 60);
        btnDFS = createStyledButton("DFS", new Color(220, 20, 60), 60);
        btnDijkstra = createStyledButton("Dijkstra", new Color(138, 43, 226), 70);
        btnRandom = createStyledButton("Random", new Color(184, 134, 11), 70);
        btnClear = createStyledButton("Clear", new Color(255, 69, 0), 60);
        btnHelp = createStyledButton("Help", new Color(70, 130, 180), 60);
        btnBack = createStyledButton("← Back", new Color(105, 105, 105), 70);

        btnBFS.addActionListener(this);
        btnDFS.addActionListener(this);
        btnDijkstra.addActionListener(this);
        btnRandom.addActionListener(this);
        btnClear.addActionListener(this);
        btnHelp.addActionListener(this);
        btnBack.addActionListener(this);

        topRightPanel.add(btnBFS);
        topRightPanel.add(btnDFS);
        topRightPanel.add(btnDijkstra);
        topRightPanel.add(btnRandom);
        topRightPanel.add(btnClear);
        topRightPanel.add(btnHelp);
        topRightPanel.add(btnBack);
    }

    private void setupInfoPanel() {
        statusLabel = new JLabel("Click on the graph area to add nodes, or use the controls above.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0, 100, 0));

        logArea = new JTextArea(4, 80);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);

        infoPanel.add(statusLabel);
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

    // Graph algorithms
    private void performBFS(GraphNode start) {
        resetGraph();
        currentAlgorithm = "BFS";
        logArea.append("=== Breadth-First Search from " + start.name + " ===\n");
        
        Queue<GraphNode> queue = new LinkedList<>();
        queue.offer(start);
        start.visited = true;
        start.setColor(new Color(255, 255, 0)); // Yellow for start
        
        logArea.append("Starting BFS from node " + start.name + "\n");
        
        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            logArea.append("Visiting node: " + current.name + "\n");
            
            if (current != start) {
                current.setColor(new Color(144, 238, 144)); // Light green for visited
            }
            
            // Find neighbors
            for (GraphEdge edge : edges) {
                GraphNode neighbor = null;
                if (edge.from == current && !edge.to.visited) {
                    neighbor = edge.to;
                } else if (!isDirected && edge.to == current && !edge.from.visited) {
                    neighbor = edge.from;
                }
                
                if (neighbor != null) {
                    neighbor.visited = true;
                    neighbor.parent = current;
                    neighbor.setColor(new Color(255, 192, 203)); // Light pink for discovered
                    queue.offer(neighbor);
                    logArea.append("  Discovered neighbor: " + neighbor.name + "\n");
                }
            }
        }
        
        logArea.append("BFS completed!\n\n");
        graphPanel.repaint();
    }

    private void performDFS(GraphNode start) {
        resetGraph();
        currentAlgorithm = "DFS";
        logArea.append("=== Depth-First Search from " + start.name + " ===\n");
        
        start.setColor(new Color(255, 255, 0)); // Yellow for start
        dfsRecursive(start);
        
        logArea.append("DFS completed!\n\n");
        graphPanel.repaint();
    }

    private void dfsRecursive(GraphNode current) {
        current.visited = true;
        logArea.append("Visiting node: " + current.name + "\n");
        
        if (!current.color.equals(new Color(255, 255, 0))) { // Not start node
            current.setColor(new Color(144, 238, 144)); // Light green for visited
        }
        
        // Find neighbors
        for (GraphEdge edge : edges) {
            GraphNode neighbor = null;
            if (edge.from == current && !edge.to.visited) {
                neighbor = edge.to;
            } else if (!isDirected && edge.to == current && !edge.from.visited) {
                neighbor = edge.from;
            }
            
            if (neighbor != null) {
                neighbor.parent = current;
                logArea.append("  Exploring neighbor: " + neighbor.name + "\n");
                dfsRecursive(neighbor);
            }
        }
    }

    private void performDijkstra(GraphNode start) {
        resetGraph();
        currentAlgorithm = "Dijkstra";
        logArea.append("=== Dijkstra's Shortest Path from " + start.name + " ===\n");
        
        PriorityQueue<GraphNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        start.distance = 0;
        start.setColor(new Color(255, 255, 0)); // Yellow for start
        pq.offer(start);
        
        logArea.append("Starting Dijkstra from node " + start.name + " (distance: 0)\n");
        
        while (!pq.isEmpty()) {
            GraphNode current = pq.poll();
            
            if (current.visited) continue;
            current.visited = true;
            
            logArea.append("Processing node: " + current.name + " (distance: " + current.distance + ")\n");
            
            if (current != start) {
                current.setColor(new Color(144, 238, 144)); // Light green for processed
            }
            
            // Update distances to neighbors
            for (GraphEdge edge : edges) {
                GraphNode neighbor = null;
                int edgeWeight = edge.weight;
                
                if (edge.from == current) {
                    neighbor = edge.to;
                } else if (!isDirected && edge.to == current) {
                    neighbor = edge.from;
                }
                
                if (neighbor != null && !neighbor.visited) {
                    int newDistance = current.distance + edgeWeight;
                    if (newDistance < neighbor.distance) {
                        neighbor.distance = newDistance;
                        neighbor.parent = current;
                        neighbor.setColor(new Color(255, 192, 203)); // Light pink for updated
                        pq.offer(neighbor);
                        logArea.append("  Updated distance to " + neighbor.name + ": " + newDistance + "\n");
                    }
                }
            }
        }
        
        logArea.append("Dijkstra completed! Final distances:\n");
        for (GraphNode node : nodes.values()) {
            String dist = node.distance == Integer.MAX_VALUE ? "∞" : String.valueOf(node.distance);
            logArea.append("  " + node.name + ": " + dist + "\n");
        }
        logArea.append("\n");
        
        graphPanel.repaint();
    }

    private void resetGraph() {
        for (GraphNode node : nodes.values()) {
            node.reset();
        }
        for (GraphEdge edge : edges) {
            edge.color = Color.BLACK;
        }
    }

    private void addNode(String name, int x, int y) {
        if (nodes.containsKey(name)) {
            logArea.append("Node " + name + " already exists!\n");
            return;
        }
        
        GraphNode node = new GraphNode(name, x, y);
        nodes.put(name, node);
        graphPanel.add(node.label);
        graphPanel.repaint();
        logArea.append("Added node: " + name + " at (" + x + ", " + y + ")\n");
    }

    private void addEdge(String fromName, String toName, int weight) {
        GraphNode from = nodes.get(fromName);
        GraphNode to = nodes.get(toName);
        
        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this, "Both nodes must exist!");
            return;
        }
        
        GraphEdge edge = new GraphEdge(from, to, weight, isDirected);
        edges.add(edge);
        graphPanel.repaint();
        
        String edgeType = isDirected ? "directed" : "undirected";
        logArea.append("Added " + edgeType + " edge: " + fromName + " -> " + toName + " (weight: " + weight + ")\n");
    }

    private void generateRandomGraph() {
        nodes.clear();
        edges.clear();
        graphPanel.removeAll();
        
        // Add random nodes
        String[] nodeNames = {"A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < 6; i++) {
            int x = 100 + (int)(Math.random() * (graphPanel.getWidth() - 200));
            int y = 100 + (int)(Math.random() * (graphPanel.getHeight() - 200));
            addNode(nodeNames[i], x, y);
        }
        
        // Add random edges
        for (int i = 0; i < 8; i++) {
            String from = nodeNames[(int)(Math.random() * nodeNames.length)];
            String to = nodeNames[(int)(Math.random() * nodeNames.length)];
            if (!from.equals(to)) {
                int weight = 1 + (int)(Math.random() * 10);
                addEdge(from, to, weight);
            }
        }
        
        logArea.append("Generated random graph with 6 nodes and random edges\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            dispose();
            SwingUtilities.invokeLater(DSAVisualizationMain::new);
        } else if (e.getSource() == btnClear) {
            nodes.clear();
            edges.clear();
            graphPanel.removeAll();
            graphPanel.repaint();
            logArea.setText("");
            statusLabel.setText("Graph cleared. Click to add nodes.");
        } else if (e.getSource() == btnAddNode) {
            String name = tfNode.getText().trim();
            if (!name.isEmpty()) {
                addNode(name, 200, 200);
                tfNode.setText("");
            }
        } else if (e.getSource() == btnAddEdge) {
            try {
                String from = tfFrom.getText().trim();
                String to = tfTo.getText().trim();
                int weight = Integer.parseInt(tfWeight.getText().trim());
                if (!from.isEmpty() && !to.isEmpty()) {
                    addEdge(from, to, weight);
                    tfFrom.setText("");
                    tfTo.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid weight value!");
            }
        } else if (e.getSource() == btnBFS || e.getSource() == btnDFS || e.getSource() == btnDijkstra) {
            if (nodes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add nodes first!");
                return;
            }
            
            String[] nodeNames = nodes.keySet().toArray(new String[0]);
            String startNodeName = (String) JOptionPane.showInputDialog(
                this, "Select starting node:", "Algorithm Execution",
                JOptionPane.QUESTION_MESSAGE, null, nodeNames, nodeNames[0]
            );
            
            if (startNodeName != null) {
                GraphNode start = nodes.get(startNodeName);
                if (e.getSource() == btnBFS) {
                    performBFS(start);
                } else if (e.getSource() == btnDFS) {
                    performDFS(start);
                } else if (e.getSource() == btnDijkstra) {
                    performDijkstra(start);
                }
            }
        } else if (e.getSource() == btnRandom) {
            generateRandomGraph();
        } else if (e.getSource() == btnHelp) {
            showHelp();
        }
    }

    private void showHelp() {
        String helpText = "<html><body style='width: 650px;'>" +
            "<h2>Graph Algorithms Visualization</h2>" +
            "<h3>Graph Creation:</h3>" +
            "<ul>" +
            "<li><b>Add Node:</b> Enter name and click 'Add Node' or click on graph area</li>" +
            "<li><b>Add Edge:</b> Specify from/to nodes and weight</li>" +
            "<li><b>Directed Graph:</b> Check box for directed edges</li>" +
            "<li><b>Random:</b> Generate random graph for testing</li>" +
            "</ul>" +
            "<h3>Algorithms:</h3>" +
            "<ul>" +
            "<li><b>BFS (Breadth-First Search):</b> Explores level by level</li>" +
            "<li><b>DFS (Depth-First Search):</b> Explores as far as possible</li>" +
            "<li><b>Dijkstra:</b> Finds shortest paths from start node</li>" +
            "</ul>" +
            "<h3>Color Coding:</h3>" +
            "<ul>" +
            "<li><b>Light Blue:</b> Unvisited nodes</li>" +
            "<li><b>Yellow:</b> Starting node</li>" +
            "<li><b>Light Pink:</b> Discovered/updated nodes</li>" +
            "<li><b>Light Green:</b> Visited/processed nodes</li>" +
            "</ul>" +
            "<h3>Time Complexity:</h3>" +
            "<ul>" +
            "<li><b>BFS/DFS:</b> O(V + E) where V=vertices, E=edges</li>" +
            "<li><b>Dijkstra:</b> O((V + E) log V) with priority queue</li>" +
            "</ul>" +
            "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Graph Algorithms Help", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == graphPanel) {
            String name = JOptionPane.showInputDialog(this, "Enter node name:");
            if (name != null && !name.trim().isEmpty()) {
                addNode(name.trim(), e.getX(), e.getY());
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void paint(Graphics g) {
        super.paintComponents(g);
        drawEdges(g);
    }

    private void drawEdges(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2.0f));
        
        int offset = topPanel.getHeight();
        
        for (GraphEdge edge : edges) {
            g2.setColor(edge.color);
            
            int x1 = edge.from.x;
            int y1 = edge.from.y + offset;
            int x2 = edge.to.x;
            int y2 = edge.to.y + offset;
            
            g2.drawLine(x1, y1, x2, y2);
            
            // Draw weight
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
            g2.setColor(Color.RED);
            g2.drawString(String.valueOf(edge.weight), midX, midY);
            
            // Draw arrow for directed graph
            if (edge.isDirected) {
                drawArrow(g2, x1, y1, x2, y2);
            }
        }
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(Color.BLACK);
        
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        
        if (length > 0) {
            dx /= length;
            dy /= length;
            
            // Arrow position (closer to target node)
            int arrowX = (int)(x2 - 20 * dx);
            int arrowY = (int)(y2 - 20 * dy);
            
            // Arrow head
            int[] xPoints = {arrowX, (int)(arrowX - 10*dx + 5*dy), (int)(arrowX - 10*dx - 5*dy)};
            int[] yPoints = {arrowY, (int)(arrowY - 10*dy - 5*dx), (int)(arrowY - 10*dy + 5*dx)};
            
            g2.fillPolygon(xPoints, yPoints, 3);
        }
    }
}