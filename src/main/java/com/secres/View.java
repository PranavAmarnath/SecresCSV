package com.secres;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class View {

	private static JFrame frame;
	//private static File path;
	private JButton openButton, saveButton, newButton;
	private JToolBar toolBar;
	private JTabbedPane tabbedPane;
	private int tabNewIndex;
	private LinkedHashMap<TablePanel, File> newPanels = new LinkedHashMap<>();
	private final int EMPTY_ROW_COUNT = 20;
	
	public View() {
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		frame = new JFrame("Secres GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		TablePanel tempPanel = new TablePanel();
		DefaultTableModel tempModel = new DefaultTableModel(20, 20);
		for(int i = 0; i < EMPTY_ROW_COUNT; i++) {
			tempModel.addRow(new Object[] { });
		}
		tempPanel.getTable().setModel(tempModel);
		
		openButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));
		openButton.setFocusable(false);
		openButton.setToolTipText("Open");
		
		tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		openButton.addActionListener(e -> {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Separated Value Files", "csv");
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			Action details = fileChooser.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);

			if(newPanels.get(tabbedPane.getSelectedComponent()) != null) {
				fileChooser.setCurrentDirectory(newPanels.get(tabbedPane.getComponentAt(tabbedPane.getSelectedIndex())));
			}

			int returnVal = fileChooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
                File path = fileChooser.getSelectedFile();
        		TablePanel newPanel = new TablePanel();
        		newPanels.put(newPanel, path);
        		for(int i = 0; i < tabbedPane.getTabCount(); i++) {
        			if(tabbedPane.getTitleAt(i).equals(path.getName())) {
        				tabbedPane.setSelectedIndex(i);
        				break;
        			}
        			else {
        				tabbedPane.addTab(path.getName(), (Component) newPanels.keySet().toArray()[newPanels.size()-1]);
                		Main.createModel(path, ((TablePanel) newPanels.keySet().toArray()[newPanels.size()-1]).getTable());
                		if(tabbedPane.getTitleAt(0).contains("New")) {
                			tabbedPane.removeTabAt(0); // remove the initial tab
                		}
                		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                		break;
        			}
        		}
            }
		});
		
		saveButton = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
		saveButton.setFocusable(false);
		saveButton.setToolTipText("Save");
		
		saveButton.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to overwrite changes?", "Save", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Separated Value Files", "csv");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(filter);
				fileChooser.setAcceptAllFileFilterUsed(false);
				Action details = fileChooser.getActionMap().get("viewTypeDetails");
				details.actionPerformed(null);
				
				if(newPanels.get(tabbedPane.getSelectedComponent()) != null) {
					fileChooser.setCurrentDirectory(newPanels.get(tabbedPane.getComponentAt(tabbedPane.getSelectedIndex())));
				}
				
				if(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).contains("New")) {
					int returnVal = fileChooser.showSaveDialog(frame);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						// **This saves the 'new file' but not the contents** -> Needs fix
						File path = fileChooser.getSelectedFile();
						Main.saveModel(path.getAbsolutePath(), ((TablePanel) tabbedPane.getSelectedComponent()).getTable());
					}
				}
				else {
					// This saves an opened file -> This works
					Main.saveModel(newPanels.get(newPanels.keySet().toArray()[newPanels.size()-1]).getAbsolutePath(), ((TablePanel) tabbedPane.getSelectedComponent()).getTable());
				}
			}
		});
		
		newButton = new JButton(UIManager.getIcon("FileView.fileIcon"));
		newButton.setFocusable(false);
		newButton.setToolTipText("New");
		
		newButton.addActionListener(e -> {
			tabNewIndex++;
			TablePanel temp = new TablePanel();
			DefaultTableModel model = new DefaultTableModel(20, 20);
			for(int i = 0; i < EMPTY_ROW_COUNT; i++) {
				model.addRow(new Object[] { });
			}
			temp.getTable().setModel(model);
			tabbedPane.addTab("New" + tabNewIndex, temp);
			tabbedPane.setSelectedComponent(temp);
		});
		
		tabNewIndex = 1;
		tabbedPane.addTab("New" + tabNewIndex, tempPanel);
		tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
		tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
		tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback", (BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
	        // close tab here
	        tabbedPane.removeTabAt(tabIndex);
	        if(tabbedPane.getTabCount() == 0) {
	        	tabNewIndex++;
	        	TablePanel temp = new TablePanel();
				DefaultTableModel model = new DefaultTableModel(20, 20);
				for(int i = 0; i < EMPTY_ROW_COUNT; i++) {
					model.addRow(new Object[] { });
				}
				temp.getTable().setModel(model);
	        	tabbedPane.addTab("New" + tabNewIndex, temp);
	        }
	    });
		tabbedPane.putClientProperty("JTabbedPane.tabCloseToolTipText", "Close");
		
		toolBar = new JToolBar();
		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		frame.add(toolBar, BorderLayout.NORTH);
		
		mainPanel.add(tabbedPane);
		frame.add(mainPanel);
		
		frame.pack();
		frame.setVisible(true);
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
	
	public static JFrame getFrame() {
		return frame;
	}
	
}
