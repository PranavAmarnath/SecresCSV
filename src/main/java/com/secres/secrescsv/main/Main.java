package com.secres.secrescsv.main;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXTable;

import com.formdev.flatlaf.FlatLightLaf;

public class Main {

	public Main() { 
		createView();
	}

	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.application.name", "SecresCSV");
		System.setProperty("apple.awt.application.appearance", "system");
		System.setProperty("apple.awt.antialiasing", "true");
		System.setProperty("apple.awt.textantialiasing", "true");
		System.setProperty("flatlaf.useWindowDecorations", "false");
		UIManager.put("Table.showHorizontalLines", true);
		UIManager.put("Table.showVerticalLines", true);
		UIManager.put("Table.intercellSpacing", new Dimension(1, 1));
		/*
		if(System.getProperty("os.name").toLowerCase().contains("win")) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		*/
		if(System.getProperty("os.name").toString().contains("Mac")) {
			try {				
				SwingUtilities.invokeLater(() -> {
					Desktop desktop = Desktop.getDesktop();

					JPanel aboutPanel = View.createAboutPanel();
					desktop.setAboutHandler(e -> {
						JOptionPane.showMessageDialog(View.getFrame(), aboutPanel, "About SecresCSV", JOptionPane.PLAIN_MESSAGE);
					});
					desktop.setPreferencesHandler(e -> {
						JOptionPane.showMessageDialog(View.getFrame(), "Preferences", "Preferences", JOptionPane.INFORMATION_MESSAGE);
					});
					desktop.setQuitHandler((e,r) -> {
						System.exit(0);
					});
				});
			} catch (Exception e) { e.printStackTrace(); }
		}
		SwingUtilities.invokeLater(() -> {
			FlatLightLaf.setup();
			new Main();
		});
	}

	public static void createModelLoad(File PATH, JXTable table, boolean refresh, boolean isLastFile) {
		new Model(PATH, table, refresh, isLastFile);
	}

	public static void saveModel(String PATH, JXTable table) {
		Model.save(PATH, table);
	}

	private void createView() {
		new View();
	}

}
