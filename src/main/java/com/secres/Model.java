package com.secres;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import com.secres.View.TablePanel;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXBusyLabel;

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
	/** Busy label */
	private static JXBusyLabel busyLabel;
	
	/**
	 * Model constructor to load CSV data
	 * @param path  Path to file
	 * @param table  The table
	 * @param refresh  If the user is refreshing (true) or if it's the first load (false)
	 */
	public Model(File path, JTable table, boolean refresh) {
		createBusyLabel();
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
					removeBusyLabel();
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
	
	/**
	 * Executes {@link #save(String, JTable)} on a SwingWorker after creating the busy label
	 * @param path  The path to export to
	 * @param table  The table to export
	 * @see #save(String, JTable)
	 */
	static void save(String path, JTable table) {
		createBusyLabel();
		new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() {
				exportToCSV(path, table);
				return null;
			}
			@Override
			protected void done() {
				removeBusyLabel();
				JOptionPane.showMessageDialog(View.getFrame(), "Finished saving file.");
			}
		}.execute();
	}
	
	/**
	 * Export table data to same path of CSV file.
	 * @param pathToExportTo  The path to export to
	 * @param tableToExport  The table to export
	 */
	private static void exportToCSV(String pathToExportTo, JTable tableToExport) {
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
	
	/**
	 * A method to show an error in a <code>JOptionPane</code>.
	 * @param title  Title of the dialog
	 * @param e  The Exception
	 */
	private static void showError(String title, Exception e) {
		JTextPane textPane = new JTextPane();
		textPane.setText(e.getMessage());
		JOptionPane.showMessageDialog(View.getFrame(), textPane, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Creates the busy label.
	 */
	private static void createBusyLabel() {
		busyLabel = new JXBusyLabel(new Dimension(18, 18)); // dimensions of icons to keep scaled
		busyLabel.setBusy(true);
		View.getToolBar().add(busyLabel);
	}
	
	/**
	 * Removes the busy label.
	 */
	private static void removeBusyLabel() {
		View.getToolBar().remove(busyLabel);
		View.getToolBar().revalidate();
		View.getToolBar().repaint();
	}
	
	/**
	 * Returns table model
	 * @return <code>DefaultTableModel</code> - table model
	 */
	DefaultTableModel getModel() {
		return model;
	}
	
	/**
	 * Returns table header
	 * @return <code>Object[]</code> - header
	 */
	Object[] getHeaders() {
		return header;
	}
	
}
