package com.secres;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class View {

	private static JFrame frame;
	private JButton openButton, saveButton, refreshButton;
	private JToolBar toolBar;
	private static JTabbedPane tabbedPane;
	private static LinkedHashMap<TablePanel, File> newPanels = new LinkedHashMap<>();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu, editMenu;
	private JMenuItem openMenuItem, saveMenuItem, refreshMenuItem;
	
	public View() {
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		frame = new JFrame("Secres GUI") {
			public Dimension getPreferredSize() {
				return new Dimension(600, 500);
			}
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel tabsPanel = new JPanel(new BorderLayout());
		
		createMenuBar();
		createToolBar();
		
		tabbedPane.setTabLayoutPolicy(1); // scrolling tabs
		tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
		tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
		tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback", (BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
	        // close tab here
        	openSaveDialog();
			tabbedPane.removeTabAt(tabIndex);
			if(tabbedPane.getTabCount() == 0) {
				disableItems();
			}
	    });
		tabbedPane.putClientProperty("JTabbedPane.tabCloseToolTipText", "Close");
		
		toolBar = new JToolBar();
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(refreshButton);
		frame.add(toolBar, BorderLayout.NORTH);
		
		tabsPanel.add(tabbedPane);
		frame.add(tabsPanel);
		
		frame.setJMenuBar(menuBar);
		
		frame.pack();
		frame.setVisible(true);
		openDialog(); // Greet user with JFileChooser to open file
	}
	
	private void openDialog() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Separated Value Files", "csv");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		Action details = fileChooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);

		if(newPanels.get(tabbedPane.getSelectedComponent()) != null) {
			fileChooser.setCurrentDirectory(newPanels.get(tabbedPane.getSelectedComponent()));
		}

		int returnVal = fileChooser.showOpenDialog(frame);
		if(returnVal == 0) {
            File path = fileChooser.getSelectedFile();
    		TablePanel newPanel = new TablePanel();
    		newPanels.put(newPanel, path);
    		if(tabbedPane.getTabCount() > 0) {
    			enableItems();
        		for(int i = 0; i < tabbedPane.getTabCount(); i++) {
        			if(tabbedPane.getTitleAt(i).equals(path.getName())) {
        				tabbedPane.setSelectedIndex(i);
        				return;
        			}
        		}
    		}
    		else {
    			disableItems();
    		}
			tabbedPane.addTab(path.getName(), (Component) newPanels.keySet().toArray()[newPanels.size()-1]);
			enableItems();
    		Main.createModelLoad(path, ((TablePanel) newPanels.keySet().toArray()[newPanels.size()-1]).getTable());
    		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
		else if(returnVal != 0 && tabbedPane.getTabCount() == 0) {
			disableItems();
		}
	}
	
	private void openSaveDialog() {
		int result = JOptionPane.showConfirmDialog(frame, "Do you want to save any changes?", "Save", JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.YES_OPTION) {
			save();
		}
	}
	
	private void enableItems() {
		saveButton.setEnabled(true);
		refreshButton.setEnabled(true);
		editMenu.setEnabled(true);
		saveMenuItem.setEnabled(true);
	}
	
	private void disableItems() {
		saveButton.setEnabled(false);
		refreshButton.setEnabled(false);
		editMenu.setEnabled(false);
		saveMenuItem.setEnabled(false);
	}
	
	private void createMenuBar() {
		fileMenu = new JMenu("File");
		openMenuItem = new JMenuItem("Open...");
		openMenuItem.addActionListener(e -> {
			openDialog();
		});
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(e -> {
			save();
		});
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		
		editMenu = new JMenu("Edit");
		refreshMenuItem = new JMenuItem("Refresh");
		refreshMenuItem.addActionListener(e -> {
			refresh();
		});
		refreshMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
		editMenu.add(refreshMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
	}
	

	private void save() {
		Main.saveModel(newPanels.get(tabbedPane.getSelectedComponent()).getAbsolutePath(), ((TablePanel) tabbedPane.getSelectedComponent()).getTable());
	}
	
	private void refresh() {
		Main.createModelRefresh(newPanels.get((TablePanel) tabbedPane.getSelectedComponent()), ((TablePanel) tabbedPane.getSelectedComponent()).getTable());
	}
	
	private void createToolBar() {
		openButton = new JButton(new FlatSVGIcon("open.svg"));
		openButton.setFocusable(false);
		openButton.setToolTipText("Open");
		
		tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		openButton.addActionListener(e -> {
			openDialog();
		});
		
		saveButton = new JButton(new FlatSVGIcon("save.svg"));
		saveButton.setFocusable(false);
		saveButton.setToolTipText("Save");
		
		saveButton.addActionListener(e -> {
			save();
		});
		
		refreshButton = new JButton();
		refreshButton.setIcon(new FlatSVGIcon("refresh.svg"));
		refreshButton.setFocusable(false);
		refreshButton.setToolTipText("Refresh");
		
		refreshButton.addActionListener(e -> {
			refresh();
		});
	}
	
	class TablePanel extends JPanel {
		private JTable table = new JTable();
		
		public TablePanel() {
			setLayout(new BorderLayout());
			
			table.setShowGrid(true);
			table.setAutoResizeMode(0);
			table.setCellSelectionEnabled(true);
			
			JScrollPane scrollPane = new JScrollPane(table);
			JTable rowTable = new RowNumberTable(table);
			rowTable.setShowGrid(true);
			scrollPane.setRowHeaderView(rowTable);
			scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
			
			add(scrollPane);
		}
		
		public JTable getTable() {
			return table;
		}
	}
	
	public static JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
	public static LinkedHashMap<TablePanel, File> getPanels() {
		return newPanels;
	}
	
}
