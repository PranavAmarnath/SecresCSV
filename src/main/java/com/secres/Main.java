package com.secres;

import java.io.File;

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
		SwingUtilities.invokeLater(() -> {
			FlatLightLaf.install();
			new Main();
		});
	}
	
	public static void createModel(File PATH, JTable table) {
		new Model(PATH, table);
	}
	
	public static void createView() {
		new View();
	}

}
