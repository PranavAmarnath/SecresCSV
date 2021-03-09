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
		// For picky mac users
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		// Mac header on mac menubar
		System.setProperty("apple.awt.application.name", "Secres");
		System.setProperty("apple.awt.application.appearance", "system");
		// Acceleration of graphics, should ONLY be used by developers
		//System.setProperty("apple.awt.graphics.EnableQ2DX","true");
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

	static void createModelLoad(File PATH, JTable table) {
		new Model(PATH, table, false);
	}

	static void createModelRefresh(File PATH, JTable table) {
		new Model(PATH, table, true);
	}

	static void saveModel(String PATH, JTable table) {
		Model.save(PATH, table);
	}

	private void createView() {
		new View();
	}

}
