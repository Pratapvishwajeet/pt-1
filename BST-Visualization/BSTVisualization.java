// Enhanced Binary Search Tree Visualization Tool
// Features: Search with path highlighting, BST validation, random tree generation,
// improved UI with modern styling, statistical information, keyboard shortcuts,
// comprehensive help system, and enhanced error handling

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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
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

public class BSTVisualization extends JFrame implements ActionListener, KeyListener {
	// Tree Root Node.
	private Node root;

	// private Color color;
	private JPanel topPanel, treePanel, infoPanel;
	private JPanel topLeftPanel, topRightPanel;
	private JButton btnAdd, btnDelete, btnSearch, btnClear, btnValidate, btnRandom, btnHelp, btnBack;
	private JTextField tf;
	private int X = 300, Y = 75;
	private Graphics2D g2;
	private Rectangle size;
	private JLabel labelInorder, labelPreorder, labelPostorder, labelHeight;
	private JLabel ansInorder, ansPreorder, ansPostorder, ansHeight;
	private JLabel labelStats, ansStats;
	private FontMetrics fontMatrix;
	private java.util.List<Node> searchPath;
	private Node foundNode;

	//Node Structure
	private static class Node {
		static int TEXT_WIDTH = 40;
		static int TEXT_HEIGHT = 40;

		JLabel data;
		Node left;
		Node right;
		Points p;

		Node(int info) {
			data = new JLabel(info + "", SwingConstants.CENTER);
			data.setFont(new Font("Arial", Font.BOLD, 16));
			data.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 2),
				BorderFactory.createEmptyBorder(3, 3, 3, 3)
			));
			data.setOpaque(true);
			data.setBackground(new Color(144, 238, 144)); // Light green
			data.setForeground(Color.BLACK);
			p = null;
		}
		
		// Reset node color to default
		void resetColor() {
			data.setBackground(new Color(144, 238, 144)); // Light green
		}
		
		// Highlight node as part of search path
		void highlightAsPath() {
			data.setBackground(new Color(255, 255, 140)); // Light yellow
		}
		
		// Highlight node as found
		void highlightAsFound() {
			data.setBackground(new Color(255, 99, 99)); // Light red
		}
	}

	//Points structure
	private static class Points {
		int x1 = 0, x2 = 0, y2 = 0, y1 = 0;

		Points(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.x2 = x2;
			this.y2 = y2;
			this.y1 = y1;
		}

		public String toString() {
			return "x1 = " + x1 + ", y1 = " + y1 + ", x2 = " + x2 + ", y2 = " + y2;
		}
	}

	// For storing the Height of the root,left and right child height.
	private static class Height {
		int root, left, right;

		Height() {
			this.root = 0;
			this.left = 0;
			this.right = 0;
		}

		Height(int left, int right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return Integer.toString(this.root);
		}
	}

	public void paint(Graphics g) {
		super.paintComponents(g);

		g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3.0f));

		Stack<Node> s = new Stack<>();
		Node curr = root;
		Points pts;
		int offset = topPanel.getBounds().height;
		while (!s.isEmpty() || curr != null) {
			while (curr != null) {
				s.push(curr);
				curr = curr.left;
			}
			if (!s.isEmpty())
				curr = s.pop();
			pts = curr.p;
			g2.drawLine(pts.x1 + 7, pts.y1 + 30 + offset, pts.x2 + 3, pts.y2 + 10 + offset);
			curr = curr.right;
		}

		// x1 = label.getX()+7
		// y1 = label.getY()+30
	}

	public BSTVisualization() {
		// Initialize the frame.
		searchPath = new ArrayList<>();
		foundNode = null;
		initialize();
	}

	// Create a styled button with modern appearance
	private JButton createStyledButton(String text, Color backgroundColor) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setPreferredSize(new Dimension(80, 35));
		button.setBackground(backgroundColor);
		button.setForeground(Color.WHITE);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		// Add hover effect
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

	private void initialize() {

		// setLayout(null); // layout
		setSize(1400, 800); //frame size
		getContentPane().setBackground(new Color(240, 248, 255)); // Alice blue

		size = getBounds();
		X = size.width / 2;

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
		treePanel.setPreferredSize(new Dimension(size.width, size.height - 300));
		treePanel.setBackground(new Color(248, 248, 255)); // Ghost white

		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setPreferredSize(new Dimension(size.width, 200));
		infoPanel.setBackground(new Color(245, 245, 245));
		infoPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(Color.GRAY), 
			"Tree Traversals", 
			0, 0, 
			new Font("Arial", Font.BOLD, 14)
		));

		// Height of BST label
		labelHeight = new JLabel("Height: ");
		labelHeight.setFont(new Font("Calibri", Font.BOLD, 16));
		topLeftPanel.add(labelHeight);

		// Height of BST answer
		ansHeight = new JLabel("0");
		ansHeight.setFont(new Font("Calibri", Font.BOLD, 16));
		ansHeight.setPreferredSize(new Dimension(30, 30));
		topLeftPanel.add(ansHeight);

		// Statistics label
		labelStats = new JLabel("  |  Nodes: ");
		labelStats.setFont(new Font("Calibri", Font.BOLD, 16));
		topLeftPanel.add(labelStats);

		// Statistics answer
		ansStats = new JLabel("0");
		ansStats.setFont(new Font("Calibri", Font.BOLD, 16));
		ansStats.setPreferredSize(new Dimension(30, 30));
		topLeftPanel.add(ansStats);

		//For geting data.
		tf = new JTextField("");
		tf.setFont(new Font("Arial", Font.BOLD, 16));
		tf.setPreferredSize(new Dimension(120, 35));
		tf.addKeyListener(this);
		tf.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.GRAY, 1),
			BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		topRightPanel.add(tf);

		//Add Button
		btnAdd = createStyledButton("Add", new Color(60, 179, 113));
		btnAdd.addActionListener(this);
		topRightPanel.add(btnAdd);

		//Delete Button
		btnDelete = createStyledButton("Delete", new Color(220, 20, 60));
		btnDelete.addActionListener(this);
		topRightPanel.add(btnDelete);

		//Search Button
		btnSearch = createStyledButton("Search", new Color(30, 144, 255));
		btnSearch.addActionListener(this);
		topRightPanel.add(btnSearch);

		//Clear Button
		btnClear = createStyledButton("Clear", new Color(255, 140, 0));
		btnClear.addActionListener(this);
		topRightPanel.add(btnClear);

		//Validate Button
		btnValidate = createStyledButton("Validate", new Color(138, 43, 226));
		btnValidate.addActionListener(this);
		topRightPanel.add(btnValidate);

		//Random Tree Button
		btnRandom = createStyledButton("Random", new Color(255, 69, 0));
		btnRandom.addActionListener(this);
		topRightPanel.add(btnRandom);

		//Help Button
		btnHelp = createStyledButton("Help", new Color(70, 130, 180));
		btnHelp.addActionListener(this);
		topRightPanel.add(btnHelp);

		//Back Button
		btnBack = createStyledButton("← Back", new Color(105, 105, 105));
		btnBack.addActionListener(this);
		topRightPanel.add(btnBack);

		// Inorder label
		labelInorder = new JLabel("Inorder :");
		labelInorder.setFont(new Font("Times New Roman", Font.BOLD, 20));
		infoPanel.add(labelInorder);

		infoPanel.add(Box.createRigidArea(new Dimension(7, 5)));

		// Inorder traversal answer
		ansInorder = new JLabel("BST is empty.");
		ansInorder.setFont(new Font("Arial", Font.PLAIN, 18));
		infoPanel.add(ansInorder);

		infoPanel.add(Box.createRigidArea(new Dimension(7, 15)));

		// Preorder label
		labelPreorder = new JLabel("Preorder :");
		labelPreorder.setFont(new Font("Times New Roman", Font.BOLD, 20));
		infoPanel.add(labelPreorder);

		infoPanel.add(Box.createRigidArea(new Dimension(7, 5)));

		// Preorder traversal answer
		ansPreorder = new JLabel("BST is empty.");
		ansPreorder.setFont(new Font("Arial", Font.PLAIN, 18));
		infoPanel.add(ansPreorder);

		infoPanel.add(Box.createRigidArea(new Dimension(7, 15)));

		// Postorder label
		labelPostorder = new JLabel("Postorder :");
		labelPostorder.setFont(new Font("Times New Roman", Font.BOLD, 20));
		infoPanel.add(labelPostorder);

		// Postorder traversal answer
		ansPostorder = new JLabel("BST is empty.");
		ansPostorder.setFont(new Font("Arial", Font.PLAIN, 18));
		infoPanel.add(ansPostorder);

		tf.requestFocusInWindow();

		add(topPanel, BorderLayout.NORTH);
		add(treePanel, BorderLayout.CENTER);
		add(infoPanel, BorderLayout.SOUTH);

		setTitle("Enhanced BST Visualization - Interactive Learning Tool"); //Title Frame
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btnClear) {
			clearTree();
			return;
		} else if (evt.getSource() == btnValidate) {
			validateBST();
			return;
		} else if (evt.getSource() == btnRandom) {
			generateRandomTree();
			return;
		} else if (evt.getSource() == btnHelp) {
			showHelp();
			return;
		} else if (evt.getSource() == btnBack) {
			dispose();
			SwingUtilities.invokeLater(() -> new DSAVisualizationMain());
			return;
		}
		
		if (tf.isEnabled()) {
			try {
				String inputText = tf.getText().trim();
				if (inputText.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter a number before performing the operation.", "Input Required", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int data = Integer.parseInt(inputText);
				
				if (data > 9999 || data < -9999) {
					JOptionPane.showMessageDialog(null, "Please enter a number between -9999 and 9999.", "Number Too Large", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if (evt.getSource() == btnAdd) {
					add(data);
				} else if (evt.getSource() == btnDelete) {
					delete(data);
				} else if (evt.getSource() == btnSearch) {
					search(data);
				}
				tf.setText("");
				tf.requestFocusInWindow();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid integer.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
				tf.selectAll(); // Select the invalid input for easy correction
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent evt) {
		char c = evt.getKeyChar();
		if (!tf.isEnabled()) {
			return;
		} else if (c == 'a' || c == 'A' || c == '\n') {
			try {
				String data = tf.getText();
				evt.consume(); // Not type 'a' or 'A' character in textfield
				if (!data.isEmpty()) {
					add(Integer.parseInt(data));
				} else {
					throw new Exception();
				}
				tf.requestFocusInWindow();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Please Enter Integer.");
			}
			tf.setText("");
		} else if (c == 'd' || c == 'D') {
			try {
				String data = tf.getText();
				evt.consume(); // Not type 'd' or 'D' character in textfield
				if (!data.isEmpty()) {
					delete(Integer.parseInt(data));
				}
				tf.requestFocusInWindow();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Please Enter Integer.");
			}
			tf.setText("");
		} else if (c == 's' || c == 'S') {
			try {
				String data = tf.getText();
				evt.consume();
				if (!data.isEmpty()) {
					search(Integer.parseInt(data));
				}
				tf.requestFocusInWindow();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Please Enter Integer.");
			}
			tf.setText("");
		} else if (c == 'c' || c == 'C') {
			evt.consume();
			clearTree();
			tf.setText("");
			tf.requestFocusInWindow();
		} else if (c == 'v' || c == 'V') {
			evt.consume();
			validateBST();
			tf.setText("");
			tf.requestFocusInWindow();
		} else if (c == 'r' || c == 'R') {
			evt.consume();
			generateRandomTree();
			tf.setText("");
			tf.requestFocusInWindow();
		} else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
			evt.consume();
	}

	@Override
	public void keyPressed(KeyEvent evt) {
	}

	@Override
	public void keyReleased(KeyEvent evt) {
	}

	//Add element in BST.
	public void add(int info) {
		Node newNode = new Node(info);
		int width = getWidth(newNode);

		if (root == null) {
			root = newNode;
			newNode.data.setBounds(treePanel.getBounds().width / 2, 10, width, 40);
			newNode.p = new Points(0, 0, 0, 0);
		} else {
			Node curr = root, pre = root;
			int currData;
			X = treePanel.getBounds().width / 2;
			while (curr != null) {
				pre = curr;
				currData = Integer.parseInt(curr.data.getText());
				if (info == currData) {
					JOptionPane.showMessageDialog(null, info + " is already exist.");
					return;
				} else if (currData > info) {
					curr = curr.left;
				} else {
					curr = curr.right;
				}
				X /= 2;
			}

			currData = Integer.parseInt(pre.data.getText());
			int x = pre.data.getX();
			int y = pre.data.getY();
			Dimension preDimension = pre.data.getSize();
			Dimension currDimension = new Dimension(width, Node.TEXT_HEIGHT);

			if (currData > info) {
				pre.left = newNode;
				newNode.data.setBounds(x - X, y + Y, width, 40);
				// x1=x;y1=y+20;x2=x-X+20;y2=y+Y+20;
				newNode.p = new Points(x, y + preDimension.height / 2, x - X + currDimension.width / 2, y + Y + currDimension.height / 2);
			} else {
				pre.right = newNode;
				newNode.data.setBounds(x + X, y + Y, width, 40);
				// x1=x+40;y1=y+20;x2=x+X+20;y2=y+Y+20;
				newNode.p = new Points(x + preDimension.width, y + preDimension.height / 2, x + X + currDimension.width / 2, y + Y + currDimension.height / 2);
			}
		}

		// Set all traversal and height of BST
		setInfo();

		// paint(treePanel.getGraphics());
		treePanel.add(newNode.data);
		treePanel.validate();
		treePanel.repaint();

		validate();
		repaint();
	}

	// Delete Node from BST
	public void delete(int data) {
		if (root == null) {
			JOptionPane.showMessageDialog(null, "BST is empty.");
		} else {
			Node curr = root, pre = root;

			while (curr != null) {
				int info = Integer.parseInt(curr.data.getText());
				if (info == data) {
					break;
				} else if (info > data) {
					pre = curr;
					curr = curr.left;
				} else {
					pre = curr;
					curr = curr.right;
				}
			}

			if (curr == null) { // data is not find.
				JOptionPane.showMessageDialog(null, data + " is not available.");
				return;
			} else if (curr.left == null || curr.right == null) { // data has 0 or 1 child

				treePanel.remove(curr.data);
				treePanel.validate();
				treePanel.repaint();

				validate();
				repaint();

				if (curr != root) {
					Node address = curr.left != null ? curr.left : curr.right;
					// curr.data>pre.data
					int preData = Integer.parseInt(pre.data.getText());
					int currData = Integer.parseInt(curr.data.getText());
					if (currData > preData) {
						pre.right = address;
					} else {
						pre.left = address;
					}
				} else {
					if (curr.left != null) {
						root = curr.left;
					} else {
						root = curr.right;
					}
				}

			} else { // data has 2 child.

				treePanel.remove(curr.data);
				treePanel.validate();
				treePanel.repaint();

				validate();
				repaint();

				/*
				 It set another node depending upon the height of left and right sub tree.
				 */
				Node nextRoot = null, preRoot = curr;
				Height height = calculateHeight(curr);

				/* For taking maximum element from the left Side. */
				if (height.left > height.right) {
					nextRoot = curr.left;
					while (nextRoot.right != null) {
						preRoot = nextRoot;
						nextRoot = nextRoot.right;
					}

					if (preRoot != curr) {
						preRoot.right = nextRoot.left;
					} else {
						preRoot.left = nextRoot.left;
					}
				} else { /* For taking minimum element from the right Side.*/
					nextRoot = curr.right;
					while (nextRoot.left != null) {
						preRoot = nextRoot;
						nextRoot = nextRoot.left;
					}

					if (preRoot != curr) {
						preRoot.left = nextRoot.right;
					} else {
						preRoot.right = nextRoot.right;
					}
				}

				curr.data = nextRoot.data;
			}
			reArrangeNode(root, root, treePanel.getBounds().width / 2);
		}

		// Set all traversal and height of BST
		setInfo();
	}

	// Set all traversal and height of BST
	private void setInfo() {
		Height height = calculateHeight(root);
		int nodeCount = countNodes(root);

		if (height.root == 0) {
			ansInorder.setText("BST is empty.");
			ansPostorder.setText("BST is empty.");
			ansPreorder.setText("BST is empty.");
		} else {
			ansInorder.setText(inorder(root));
			ansPostorder.setText(postorder(root));
			ansPreorder.setText(preorder(root));
		}

		ansHeight.setText(String.valueOf(height.root));
		ansStats.setText(String.valueOf(nodeCount));
	}

	private int getWidth(Node node) {
		fontMatrix = getFontMetrics(node.data.getFont());
		int width = fontMatrix.stringWidth(node.data.getText());
		return width < Node.TEXT_WIDTH ? Node.TEXT_WIDTH : (width + 5);
	}

	//Inorder logic
	private String inorder(Node root) {
		if (root == null)
			return "";

		return inorder(root.left) + root.data.getText() + " " + inorder(root.right);
	}

	//Preorder logic
	public String preorder(Node root) {
		if (root == null)
			return "";

		return root.data.getText() + " " + preorder(root.left) + preorder(root.right);
	}

	//Postorder logic
	public String postorder(Node root) {
		if (root == null)
			return "";

		return postorder(root.left) + postorder(root.right) + root.data.getText() + " ";
	}

	// Calculate Height of BST using recursive method.
	private Height calculateHeight(Node root) {
		if (root == null) {
			return new Height();
		}
		Height leftChild = calculateHeight(root.left);
		Height rightChild = calculateHeight(root.right);
		Height current = new Height(leftChild.root, rightChild.root);
		current.root = 1 + Math.max(leftChild.root, rightChild.root);
		return current;
	}

	// Rearrange nodes
	private void reArrangeNode(Node node, Node pre, int X) {
		if (node == null)
			return;

		int width = getWidth(node);

		if (root == node) {
			node.data.setBounds(X, 10, width, Node.TEXT_HEIGHT);
		} else {
			int x = pre.data.getX();
			int y = pre.data.getY();
			Dimension preDimension = pre.data.getSize();
			Dimension currDimension = new Dimension(width, Node.TEXT_HEIGHT);

			int preData = Integer.parseInt(pre.data.getText());
			int nodeData = Integer.parseInt(node.data.getText());
			if (nodeData < preData) {
				node.data.setBounds(x - X, y + Y, width, Node.TEXT_HEIGHT);
				node.p = new Points(x, y + preDimension.height / 2, x - X + currDimension.width / 2, y + Y + currDimension.height / 2);
			} else {
				node.data.setBounds(x + X, y + Y, width, Node.TEXT_HEIGHT);
				// node.p = new Points(x + 40, y + 20, x + X + 20, y + Y + 20);
				node.p = new Points(x + preDimension.width, y + preDimension.height / 2, x + X + currDimension.width / 2, y + Y + currDimension.height / 2);
			}
		}

		reArrangeNode(node.left, node, X / 2);
		reArrangeNode(node.right, node, X / 2);
	}

	// Search for a value in BST and highlight the search path
	public void search(int data) {
		if (root == null) {
			JOptionPane.showMessageDialog(null, "BST is empty.");
			return;
		}
		
		// Reset all node colors first
		resetAllColors(root);
		
		searchPath = new ArrayList<>();
		foundNode = null;
		
		Node curr = root;
		boolean found = false;
		
		while (curr != null) {
			searchPath.add(curr);
			curr.highlightAsPath();
			
			int currData = Integer.parseInt(curr.data.getText());
			if (currData == data) {
				curr.highlightAsFound();
				foundNode = curr;
				found = true;
				break;
			} else if (currData > data) {
				curr = curr.left;
			} else {
				curr = curr.right;
			}
		}
		
		treePanel.repaint();
		
		if (found) {
			JOptionPane.showMessageDialog(null, 
				"Found " + data + " in the BST!\nSearch path length: " + searchPath.size() + " nodes");
		} else {
			JOptionPane.showMessageDialog(null, data + " not found in the BST.");
		}
	}
	
	// Reset colors of all nodes
	private void resetAllColors(Node node) {
		if (node == null) return;
		node.resetColor();
		resetAllColors(node.left);
		resetAllColors(node.right);
	}
	
	// Clear the entire tree
	public void clearTree() {
		if (root == null) {
			JOptionPane.showMessageDialog(null, "BST is already empty.");
			return;
		}
		
		// Remove all components from tree panel
		clearTreeRecursive(root);
		root = null;
		
		treePanel.removeAll();
		treePanel.validate();
		treePanel.repaint();
		
		// Reset info
		setInfo();
		
		JOptionPane.showMessageDialog(null, "Tree cleared successfully!");
	}
	
	private void clearTreeRecursive(Node node) {
		if (node == null) return;
		clearTreeRecursive(node.left);
		clearTreeRecursive(node.right);
		treePanel.remove(node.data);
	}
	
	// Validate if current structure is a valid BST
	public void validateBST() {
		if (root == null) {
			JOptionPane.showMessageDialog(null, "BST is empty - technically valid!");
			return;
		}
		
		boolean isValid = isBSTValid(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		if (isValid) {
			JOptionPane.showMessageDialog(null, 
				"✓ Valid BST!\nThe tree satisfies BST properties.");
		} else {
			JOptionPane.showMessageDialog(null, 
				"✗ Invalid BST!\nThe tree violates BST properties.");
		}
	}
	
	private boolean isBSTValid(Node node, int minVal, int maxVal) {
		if (node == null) return true;
		
		int nodeData = Integer.parseInt(node.data.getText());
		
		if (nodeData <= minVal || nodeData >= maxVal) {
			return false;
		}
		
		return isBSTValid(node.left, minVal, nodeData) && 
			   isBSTValid(node.right, nodeData, maxVal);
	}

	// Count total number of nodes
	private int countNodes(Node node) {
		if (node == null) return 0;
		return 1 + countNodes(node.left) + countNodes(node.right);
	}

	// Generate a random BST for demonstration
	public void generateRandomTree() {
		// Clear existing tree first
		if (root != null) {
			clearTreeRecursive(root);
			root = null;
			treePanel.removeAll();
		}
		
		String[] options = {"Small (5-8 nodes)", "Medium (10-15 nodes)", "Large (20-25 nodes)"};
		String choice = (String) JOptionPane.showInputDialog(
			this,
			"Choose tree size:",
			"Generate Random BST",
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		if (choice == null) return; // User cancelled
		
		int nodeCount;
		int maxValue;
		
		if (choice.equals(options[0])) { // Small
			nodeCount = 5 + (int)(Math.random() * 4); // 5-8 nodes
			maxValue = 100;
		} else if (choice.equals(options[1])) { // Medium
			nodeCount = 10 + (int)(Math.random() * 6); // 10-15 nodes
			maxValue = 200;
		} else { // Large
			nodeCount = 20 + (int)(Math.random() * 6); // 20-25 nodes
			maxValue = 500;
		}
		
		java.util.Set<Integer> usedNumbers = new HashSet<>();
		
		for (int i = 0; i < nodeCount; i++) {
			int randomValue;
			do {
				randomValue = 1 + (int)(Math.random() * maxValue);
			} while (usedNumbers.contains(randomValue));
			
			usedNumbers.add(randomValue);
			add(randomValue);
		}
		
		JOptionPane.showMessageDialog(this, 
			"Generated random BST with " + nodeCount + " nodes!");
	}

	// Show help dialog with instructions
	public void showHelp() {
		String helpText = "<html><body style='font-family: Arial; font-size: 12px; width: 500px;'>" +
			"<h2 style='color: #2E4057;'>Binary Search Tree Visualization - Help</h2>" +
			"<h3 style='color: #048A81;'>Operations:</h3>" +
			"<ul>" +
			"<li><b>Add:</b> Insert a new node into the BST</li>" +
			"<li><b>Delete:</b> Remove a node from the BST</li>" +
			"<li><b>Search:</b> Find a node and highlight the search path</li>" +
			"<li><b>Clear:</b> Remove all nodes from the tree</li>" +
			"<li><b>Validate:</b> Check if the structure is a valid BST</li>" +
			"<li><b>Random:</b> Generate a random BST for demonstration</li>" +
			"</ul>" +
			"<h3 style='color: #048A81;'>Keyboard Shortcuts:</h3>" +
			"<ul>" +
			"<li><b>Enter or A:</b> Add the number in the text field</li>" +
			"<li><b>D:</b> Delete the number in the text field</li>" +
			"<li><b>S:</b> Search for the number in the text field</li>" +
			"<li><b>C:</b> Clear the entire tree</li>" +
			"<li><b>V:</b> Validate the BST structure</li>" +
			"<li><b>R:</b> Generate a random tree</li>" +
			"</ul>" +
			"<h3 style='color: #048A81;'>Color Legend:</h3>" +
			"<ul>" +
			"<li><span style='background-color: #90EE90; padding: 2px 6px;'>Light Green</span> - Normal nodes</li>" +
			"<li><span style='background-color: #FFFF8C; padding: 2px 6px;'>Yellow</span> - Search path nodes</li>" +
			"<li><span style='background-color: #FF6363; padding: 2px 6px;'>Red</span> - Found/Target node</li>" +
			"</ul>" +
			"<h3 style='color: #048A81;'>Information Panel:</h3>" +
			"<p>The bottom panel shows tree traversals (Inorder, Preorder, Postorder) and " +
			"the top panel displays tree height and node count.</p>" +
			"</body></html>";
		
		JOptionPane.showMessageDialog(
			this,
			helpText,
			"BST Visualization Help",
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	public static void main(String arg[]) {
		BSTVisualization bst = new BSTVisualization();

		bst.add(500);
		bst.add(250);
		bst.add(350);
		bst.add(200);
		bst.add(750);
		bst.add(1000);
		bst.add(700);
		bst.add(740);
	}
}