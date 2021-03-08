package com.secres;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import com.secres.View.TablePanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * The <code>Model</code> class defines all I/O from the CSV files.
 * <P>
 * Each instance of <code>Model</code> spawns a new {@link SwingWorker} for adding each new row to the current <code>JTable</code>'s <code>TableModel</code>
 * <P>
 * 
 * The class also manages exporting table data to a CSV file.
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
	 * @param table  The table
	 */
	public Model(File path, JTable table, boolean refresh) {
		class Worker extends SwingWorker<Void, String> {
			@Override
			protected Void doInBackground() {
				try {
					reader = new CSVReader(new FileReader(path));
				} catch (FileNotFoundException e1) {
					showError("File Not Found :(", e1);
				}
				try {
					header = (String[]) reader.readNext();
				} catch (CsvValidationException e1) {
					showError("CSV Not Validated :(", e1);
				} catch (IOException e1) {
					showError("I/O Exception :(", e1);
				}
				//SwingUtilities.invokeAndWait(() -> model = new DefaultTableModel(header, 0)); // NOT invokeLater() because model HAS to be initialized immediately on EDT
				model = new DefaultTableModel(header, 0);
				table.setModel(model);
				try {
					while((line = reader.readNext()) != null) {
						model.addRow(line);
				    }
				} catch(Exception e) {
					showError("An Exception Occurred :(", e);
				}
				return null;
			}
			@Override
			protected void done() {
				try {
					if(refresh == true) {
						((TablePanel) View.getTabbedPane().getSelectedComponent()).getTable().setModel(model);
						JOptionPane.showMessageDialog(View.getFrame(), "Refreshed data.");
					}
					else {
						JOptionPane.showMessageDialog(View.getFrame(), "Finished loading data.");
					}
					reader.close();
				} catch (IOException e) {
					showError("I/O Exception :(", e);
				}
			}
		};
		Worker worker = new Worker();
		worker.execute();
	}
	
	public static void save(String path, JTable table) {
		new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() {
				exportToCSV(path, table);
				return null;
			}
			@Override
			protected void done() {
				JOptionPane.showMessageDialog(View.getFrame(), "Finished saving file.");
			}
		}.execute();
	}
	
	public static void exportToCSV(String pathToExportTo, JTable tableToExport) {
	    try {
	        TableModel model = tableToExport.getModel();
	        FileWriter csv = new FileWriter(new File(pathToExportTo));

	        for(int i = 0; i < model.getColumnCount(); i++) {
	        	if(i != model.getColumnCount() - 1) {
	        		csv.write(model.getColumnName(i) + ",");
	        	}
	        	else {
	        		csv.write(model.getColumnName(i));
	        	}
	        }

	        csv.write("\n");

	        for(int i = 0; i < model.getRowCount(); i++) {
	            for(int j = 0; j < model.getColumnCount(); j++) {
	            	if(j != model.getColumnCount() - 1) {
	            		csv.write(model.getValueAt(i, j).toString() + ",");
	            	}
	            	else {
	            		csv.write(model.getValueAt(i, j).toString());
	            	}
	            }
	            csv.write("\n");
	        }

	        csv.close();
	    } catch (IOException e) {
	    	showError("I/O Exception :(", e);
	    }
	}
	
	private static void showError(String title, Exception e) {
		JTextPane textPane = new JTextPane();
		textPane.setText(e.getMessage());
		JOptionPane.showMessageDialog(View.getFrame(), textPane, title, JOptionPane.ERROR_MESSAGE);
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
