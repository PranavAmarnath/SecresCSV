package com.secres;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.BiConsumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

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
		JPanel panel = new JPanel();
		JLabel chooseFile = new JLabel("Choose a File", JLabel.CENTER);
		chooseFile.setFont(new Font("SansSerif", Font.BOLD, 15));
		chooseFile.setForeground(new Color(150, 150, 150));
		panel.setBorder(new EmptyBorder(200, 200, 200, 200));
		panel.add(chooseFile);
		
		openButton = new JButton(new ImageIcon(getClass().getResource("/folder_open.png")));
		openButton.setFocusable(false);
		
		tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
		openButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			if(path != null) {
				fileChooser.setCurrentDirectory(path);
			}
			int returnVal = fileChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fileChooser.getSelectedFile();
        		TablePanel newPanel = new TablePanel();
        		tabbedPane.addTab(path.getName(), newPanel);
        		Main.createModel(path, newPanel.getTable());
        		if(tabbedPane.getTitleAt(0).contains("New")) {
        			tabbedPane.removeTabAt(0); // remove the initial tab
        		}
        		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
		});
		
		tabNewIndex = 1;
		tabbedPane.addTab("New" + tabNewIndex, panel);
		tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
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
			table.getTableHeader().setReorderingAllowed(false);
			table.setCellSelectionEnabled(true);
			
			JScrollPane scrollPane = new JScrollPane(table);
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
