package com.secres;

import java.awt.Desktop;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

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
			FlatLightLaf.install();
			new Main();
		});
	}

	public static void createModelLoad(File PATH, JTable table) {
		new Model(PATH, table, false);
	}

	public static void createModelRefresh(File PATH, JTable table) {
		new Model(PATH, table, true);
	}

	public static void saveModel(String PATH, JTable table) {
		Model.save(PATH, table);
	}

	private void createView() {
		new View();
	}

}
