package com.secres;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
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
		/*
		if(System.getProperty("os.name").toLowerCase().contains("win")) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		*/
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
	
	public void createView() {
		new View();
	}

}
