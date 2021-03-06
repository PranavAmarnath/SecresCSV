package com.secres;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JTable.PrintMode;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class View {

	private static JFrame frame;
	private JButton openButton, saveButton, printButton, selectAllButton, refreshButton;
	private JToolBar toolBar;
	private static JTabbedPane tabbedPane;
	private static LinkedHashMap<TablePanel, File> newPanels = new LinkedHashMap<>();
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, viewMenu;
	private JMenuItem openMenuItem, saveMenuItem, printMenuItem;
	private JMenuItem selectAllMenuItem, refreshMenuItem;
	private JRadioButtonMenuItem lightMenuItem, darkMenuItem;
	
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
		
		tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		
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
		
		menuBar = new JMenuBar();
		toolBar = new JToolBar();
		
		createMenuBar();
		createToolBar();
		
		frame.add(toolBar, BorderLayout.NORTH);
		frame.setJMenuBar(menuBar);
		
		tabsPanel.add(tabbedPane);
		frame.add(tabsPanel);
		
		frame.pack();
		frame.setVisible(true);
		openDialog(); // Greet user with JFileChooser to open file
	}
	
	private void openDialog() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files (*.csv), Text files (*.txt)", "csv", "txt");
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
			tabbedPane.addTab(path.getName(), fileChooser.getIcon(path), (Component) newPanels.keySet().toArray()[newPanels.size()-1]);
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
	
	private void save() {
		Main.saveModel(newPanels.get(tabbedPane.getSelectedComponent()).getAbsolutePath(), ((TablePanel) tabbedPane.getSelectedComponent()).getTable());
	}
	
	private void selectAll() {
		((TablePanel) tabbedPane.getSelectedComponent()).getTable().requestFocus();
		((TablePanel) tabbedPane.getSelectedComponent()).getTable().selectAll();
	}
	
	private void print() {
		try {
			MessageFormat header = new MessageFormat("Page {0,number,integer}");
			((TablePanel) tabbedPane.getSelectedComponent()).getTable().print(PrintMode.FIT_WIDTH, header, null, true, null, true);
		} catch (PrinterException e) {
			e.printStackTrace();
		}
	}
	
	private void refresh() {
		Main.createModelRefresh(newPanels.get((TablePanel) tabbedPane.getSelectedComponent()), ((TablePanel) tabbedPane.getSelectedComponent()).getTable());
	}
	
	private void enableItems() {
		saveButton.setEnabled(true);
		selectAllButton.setEnabled(true);
		refreshButton.setEnabled(true);
		editMenu.setEnabled(true);
		saveMenuItem.setEnabled(true);
	}
	
	private void disableItems() {
		saveButton.setEnabled(false);
		selectAllButton.setEnabled(false);
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
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(e -> {
			save();
		});
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		printMenuItem = new JMenuItem("Print...");
		printMenuItem.addActionListener(e -> {
			print();
		});
		printMenuItem.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(printMenuItem);
		
		editMenu = new JMenu("Edit");
		selectAllMenuItem = new JMenuItem("Select All");
		selectAllMenuItem.addActionListener(e -> {
			selectAll();
		});
		selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		refreshMenuItem = new JMenuItem("Refresh");
		refreshMenuItem.addActionListener(e -> {
			refresh();
		});
		refreshMenuItem.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		editMenu.add(selectAllMenuItem);
		editMenu.add(refreshMenuItem);
		
		viewMenu = new JMenu("View");
		ButtonGroup themes = new ButtonGroup();
		lightMenuItem = new JRadioButtonMenuItem("Light");
		lightMenuItem.setSelected(true);
		themes.add(lightMenuItem);
		lightMenuItem.addActionListener(e -> {
			FlatLightLaf.install();
			SwingUtilities.updateComponentTreeUI(frame);
		});
		darkMenuItem = new JRadioButtonMenuItem("Dark");
		themes.add(darkMenuItem);
		darkMenuItem.addActionListener(e -> {
			FlatDarkLaf.install();
			SwingUtilities.updateComponentTreeUI(frame);
		});
		viewMenu.add(lightMenuItem);
		viewMenu.add(darkMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
	}
	
	private void createToolBar() {
		openButton = new JButton(new FlatSVGIcon("open.svg"));
		openButton.setFocusable(false);
		openButton.setToolTipText("Open");
		openButton.addActionListener(e -> {
			openDialog();
		});
		
		saveButton = new JButton(new FlatSVGIcon("save.svg"));
		saveButton.setFocusable(false);
		saveButton.setToolTipText("Save");
		saveButton.addActionListener(e -> {
			save();
		});
		
		printButton = new JButton(new FlatSVGIcon("print.svg"));
		printButton.setFocusable(false);
		printButton.setToolTipText("Print");
		printButton.addActionListener(e -> {
			print();
		});
		
		selectAllButton = new JButton();
		selectAllButton.setIcon(new FlatSVGIcon("select_all.svg"));
		selectAllButton.setFocusable(false);
		selectAllButton.setToolTipText("Select All");
		selectAllButton.addActionListener(e -> {
			selectAll();
		});
		
		refreshButton = new JButton();
		refreshButton.setIcon(new FlatSVGIcon("refresh.svg"));
		refreshButton.setFocusable(false);
		refreshButton.setToolTipText("Refresh");
		refreshButton.addActionListener(e -> {
			refresh();
		});

		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(printButton);
		toolBar.add(selectAllButton);
		toolBar.add(refreshButton);
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
