package com.secres;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * The <code>Model</code> class defines all I/O from the CSV files.
 * <P>
 * Each instance of <code>Model</code> spawns a new {@link SwingWorker} for adding each new row to the current <code>JTable</code>'s <code>TableModel</code>
 * <P>
 * After starting and finishing ALL reads, the <code>Model</code> notifies {@link Main}.<br>
 * 1. After <i>starting</i> the last read, {@link Main} creates a new instance of {@link View}.<br>
 * 2. After <i>finishing</i> the last read, {@link Main} starts updating each <code>JFreeChart</code> for each <code>ChartPanel</code> that {@link View} created.
 * 
 * @author Pranav Amarnath
 *
 */
public class Model {

	/** Table model */
	private DefaultTableModel model;
	/** Table header */
	private Object[] header;
	//private List<String[]> myEntries = new ArrayList<>();
	/** OpenCSV parser */
	private CSVReader reader;
	/** Current line */
	private String[] line;
	
	/**
	 * Model constructor
	 * @param path  Path to file
	 */
	public Model(File path, JTable table) {
		new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() {
				try {
					reader = new CSVReader(new FileReader(path));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					header = (String[]) reader.readNext();
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//SwingUtilities.invokeAndWait(() -> model = new DefaultTableModel(header, 0)); // NOT invokeLater() because model HAS to be initialized immediately on EDT
				model = new DefaultTableModel(header, 0);
				table.setModel(model);
				try {
					while((line = reader.readNext()) != null) {
						model.addRow(line);
				    }
				} catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void done() {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	/**
	 * Returns table model
	 * @return <code>DefaultTableModel</code> - table model
	 */
	public DefaultTableModel getModel() {
		return model;
	}
	
	/**
	 * Returns table header
	 * @return <code>Object[]</code> - header
	 */
	public Object[] getHeaders() {
		return header;
	}
	
}
