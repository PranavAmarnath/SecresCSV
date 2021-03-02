package com.secres;

import java.awt.BorderLayout;
import java.io.File;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
	private static File path;
	private JButton openButton;
	private JToolBar toolBar;
	private JTabbedPane tabbedPane;
	private int tabNewIndex;
	
	public View() {
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		frame = new JFrame("Secres GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		
		JTable tempTable = new JTable();
		DefaultTableModel tempModel = new DefaultTableModel(20, 20);
		for(int i = 0; i < 200; i++) {
			tempModel.addRow(new Object[] {});
		}
		tempTable.setModel(tempModel);
		
		tempTable.setShowGrid(true);
		tempTable.setAutoResizeMode(0);
		tempTable.setCellSelectionEnabled(true);
		JScrollPane tempScrollPane = new JScrollPane(tempTable);
		
		JTable rowTable = new RowNumberTable(tempTable);
		rowTable.setShowGrid(true);
		tempScrollPane.setRowHeaderView(rowTable);
		tempScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
		
		panel.add(tempScrollPane);
		
		openButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));
		openButton.setFocusable(false);
		
		tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		openButton.addActionListener(e -> {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Separated Value Files", "csv");
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(filter);
			Action details = fileChooser.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);
			if(path != null) {
				fileChooser.setCurrentDirectory(path);
			}
			int returnVal = fileChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fileChooser.getSelectedFile();
        		TablePanel newPanel = new TablePanel();
        		for(int i = 0; i < tabbedPane.getTabCount(); i++) {
        			if(tabbedPane.getTitleAt(i).equals(path.getName())) {
        				tabbedPane.setSelectedIndex(i);
        				break;
        			}
        			else {
        				tabbedPane.addTab(path.getName(), newPanel);
                		Main.createModel(path, newPanel.getTable());
                		if(tabbedPane.getTitleAt(0).contains("New")) {
                			tabbedPane.removeTabAt(0); // remove the initial tab
                		}
                		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                		break;
        			}
        		}
            }
		});
		
		tabNewIndex = 1;
		tabbedPane.addTab("New" + tabNewIndex, panel);
		tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
		tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
		tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback", (BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
	        // close tab here
	        tabbedPane.removeTabAt(tabIndex);
	        if(tabbedPane.getTabCount() == 0) {
	        	tabbedPane.addTab("New" + tabNewIndex, panel);
	        }
	        tabNewIndex++;
	    });
		tabbedPane.putClientProperty("JTabbedPane.tabCloseToolTipText", "Close");
		
		toolBar = new JToolBar();
		toolBar.add(openButton);
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
	
	public static File getFile() {
		return path;
	}
	
}
