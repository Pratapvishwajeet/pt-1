# Enhanced Binary Search Tree Visualization

An interactive Java Swing application for visualizing Binary Search Tree operations with modern UI and comprehensive features.

## ✨ Features

### Core BST Operations
- **Add Node**: Insert new nodes into the BST with visual positioning
- **Delete Node**: Remove nodes with proper BST restructuring
- **Search**: Find nodes with path highlighting in different colors
- **Clear Tree**: Reset the entire tree structure

### Advanced Features
- **BST Validation**: Verify if the current structure maintains BST properties
- **Random Tree Generation**: Create random BSTs of different sizes for testing
- **Interactive Search**: Visual path highlighting with color coding
- **Statistical Information**: Display tree height and node count in real-time

### User Interface Enhancements
- **Modern Styling**: Professional color scheme with gradient effects
- **Hover Effects**: Interactive buttons with visual feedback  
- **Responsive Layout**: Organized panels with proper spacing
- **Color-Coded Nodes**: 
  - 🟢 Light Green: Normal nodes
  - 🟡 Yellow: Search path nodes  
  - 🔴 Red: Found/target nodes

### Keyboard Shortcuts
- **Enter/A**: Add number from text field
- **D**: Delete number from text field
- **S**: Search for number
- **C**: Clear entire tree
- **V**: Validate BST structure
- **R**: Generate random tree

### Help System
- **Comprehensive Help**: Built-in tutorial explaining all features
- **Keyboard Reference**: Complete list of shortcuts
- **Color Legend**: Visual guide for node colors
- **Operation Guide**: Step-by-step instructions

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- Java Swing (included in standard JDK)

### Running the Application
```bash
javac BSTVisualization.java
java BSTVisualization
```

## 📋 Usage

1. **Adding Nodes**: Enter a number in the text field and click "Add" or press Enter
2. **Searching**: Enter a number and click "Search" to highlight the path
3. **Random Trees**: Click "Random" to generate test trees of various sizes
4. **Validation**: Click "Validate" to check BST properties
5. **Help**: Click "Help" for detailed instructions

## 🎯 Educational Value

This tool is perfect for:
- Computer Science students learning BST concepts
- Visualizing tree traversal algorithms (Inorder, Preorder, Postorder)
- Understanding BST insertion and deletion algorithms
- Testing BST properties and validation
- Demonstrating search complexity in tree structures

## 🛠️ Technical Improvements

- Enhanced error handling with specific user messages
- Improved memory management with proper node cleanup
- Modern Swing components with custom styling
- Efficient tree traversal algorithms
- Robust input validation
- Professional UI design patterns

## 📊 Tree Information Display

The application shows:
- **Tree Height**: Maximum depth of the tree
- **Node Count**: Total number of nodes
- **Traversals**: Inorder, Preorder, and Postorder sequences
- **Real-time Updates**: Information updates with every operation

## 🎨 Visual Features

- Clean, modern interface design
- Smooth hover effects on buttons
- Professional color palette
- Clear node connections with visual lines
- Organized information panels
- Responsive layout design

## 🔧 Future Enhancements

Potential improvements could include:
- Animation effects for operations
- AVL tree balancing with rotations  
- Save/Load tree structures
- Step-by-step operation mode
- Tree comparison features
- Export functionality

---

*Enhanced version with modern UI, comprehensive features, and educational focus.*

<p align="center">
	<a href="https://github.com/urvesh254" title="profile">
	<img src="https://img.shields.io/badge/maintainer-urvesh254-blue" alt="maintainer">
	</a>
	<a href="https://www.oracle.com/in/java/technologies/javase-downloads.html" title="JDK Download">
		<img src="https://img.shields.io/badge/JDK-%3E%3D%20v8-blue" alt="jdk version">
	</a>
	<a href="https://github.com/urvesh254/BST-Visualization/releases">
		<img src="https://img.shields.io/badge/release-1.0.0-blue" alt="release">
	</a>
	<img src="https://img.shields.io/badge/contributor-welcome-brightgreen" alt="contributor">
</p>

## Table of contents
* [Introduction](#introduction)
* [Prerequisite](#prerequisite)
* [Download](#download)
* [Execution](#execution)
* [Explanation](#explanation)
	* [Adding element in BST](#adding-element-in-binary-search-tree)
	* [Deleting Element from BST](#deleting-element-from-binary-search-tree)

## Introduction
- Hey there, welcome to **BST Visualization** repository. In this repository you see how operations in **Binary Search Tree** Data Structure like "Delete" and "Add" actually works and how BST is construct in visually.
- Currently this program accept only Integer inputs.
- **"Suggestions are welcome"**, put your suggestions in issue.

## Prerequisite 
-   For run **BST Vitalization** in you system you want to install some softwares.
	 - [Java JDK ](https://www.oracle.com/in/java/technologies/javase-downloads.html "Java JDK") 	
	 - IDE or Language editor (Any one)
		 - [Sublime Text](https://www.sublimetext.com/ "Sublime Text") 
		 - [NetBeans](https://netbeans.org/ "NetBeans IDE")
		 - [Atom](https://atom.io/ "Atom")
		 - [Notepad++](https://notepad-plus-plus.org/downloads/ "Notepad++")

## Download
- Download .jar file form [Latest Releases](https://github.com/urvesh254/BST-Visualization/releases "Download")
- **Note**: For run this .jar file you want to complete [Prerequisite](#prerequisite) firsts.

## Execution
- Open CMD or terminal where you put BSTVisualization.java file.
- First compile the java file using this command.
	```cmd
	> javac BSTVisualization.java
	```
- After compilation run the file using JVM using this command.
	```cmd
	> java BSTVisualization
	```

## Explanation
### Adding Element in Binary Search Tree
- We can add element in BST using two ways.
	1. With using **"Add"** button.
	2. With pressing **"A"** or **"a"** or **"Enter"** key in keyboard.
	
![Add in BST](https://user-images.githubusercontent.com/55116730/102015789-a6009c00-3d83-11eb-8ae9-bf47b3fd6c67.gif "Adding Element in BST")

### Deleting Element from Binary Search Tree
- We can also delete element in BST using two ways.
	1. With using **"Delete"** button.
	2. With pressing **"D"** or **"d"** key in keyboard.

![Delete in BST](https://user-images.githubusercontent.com/55116730/102015791-a9942300-3d83-11eb-9c0f-4befc0288583.gif)
- If entered element is no present or Binary Search Tree is empty then it throws an popup window.
	1. BST Empty Error
![BST Empty Error](https://user-images.githubusercontent.com/55116730/102014950-9b8fd380-3d7e-11eb-845b-9ff621e5c559.jpg "BST Empty Error")
	2. Element Not Available Error
![Element Not Available Error](https://user-images.githubusercontent.com/55116730/102014949-9a5ea680-3d7e-11eb-9288-d9d3bc018ba8.jpg "Element Not Available Error")
