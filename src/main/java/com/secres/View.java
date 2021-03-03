package com.secres;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class View {

	private static JFrame frame;
	private JButton openButton, saveButton;
	private JToolBar toolBar;
	private static JTabbedPane tabbedPane;
	private LinkedHashMap<TablePanel, File> newPanels = new LinkedHashMap<>();
	
	public View() {
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		frame = new JFrame("Secres GUI") {
			public Dimension getPreferredSize() {
				return new Dimension(500, 400);
			}
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel tabsPanel = new JPanel(new BorderLayout());
		
		openButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));
		openButton.setFocusable(false);
		openButton.setToolTipText("Open");
		
		tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		openButton.addActionListener(e -> {
			openOpenDialog();
		});
		
		saveButton = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
		saveButton.setFocusable(false);
		saveButton.setToolTipText("Save");
		
		saveButton.addActionListener(e -> {
			openSaveDialog();
		});
		
		tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
		tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
		tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback", (BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
	        // close tab here
			int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this file?", "Close", JOptionPane.YES_NO_OPTION);
		    if(result == JOptionPane.YES_OPTION) {
	        	openSaveDialog();
				tabbedPane.removeTabAt(tabIndex);
		    }
	    });
		tabbedPane.putClientProperty("JTabbedPane.tabCloseToolTipText", "Close");
		
		toolBar = new JToolBar();
		toolBar.add(openButton);
		toolBar.add(saveButton);
		frame.add(toolBar, BorderLayout.NORTH);
		
		tabsPanel.add(tabbedPane);
		frame.add(tabsPanel);
		
		frame.pack();
		frame.setVisible(true);
		openOpenDialog();
	}
	
	private void openOpenDialog() {
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
        		for(int i = 0; i < tabbedPane.getTabCount(); i++) {
        			if(tabbedPane.getTitleAt(i).equals(path.getName())) {
        				tabbedPane.setSelectedIndex(i);
        				return;
        			}
        		}
    		}
			tabbedPane.addTab(path.getName(), (Component) newPanels.keySet().toArray()[newPanels.size()-1]);
    		Main.createModel(path, ((TablePanel) newPanels.keySet().toArray()[newPanels.size()-1]).getTable());
    		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
	}
	
	private void openSaveDialog() {
		// Check here if the tab's first index contains '*' signifying it's been changed.
		// The '*' should be added if the table has been changed.
		int result = JOptionPane.showConfirmDialog(frame, "Do you want to overwrite changes?", "Save", JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.YES_OPTION) {
			Main.saveModel(newPanels.get(tabbedPane.getSelectedComponent()).getAbsolutePath(), ((TablePanel) tabbedPane.getSelectedComponent()).getTable());
		}
	}
	
	class TablePanel extends JPanel {
		private JTable table = new JTable();
		
		public TablePanel() {
			setLayout(new BorderLayout());
			
			table.setShowGrid(true);
			table.setAutoResizeMode(0);
			table.setCellSelectionEnabled(true);
			// Add TableModelListener
			
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
	
}
