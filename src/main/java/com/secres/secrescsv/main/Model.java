package com.secres.secrescsv.main;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;

import java.awt.Taskbar;
import java.awt.Taskbar.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
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
	private Object[] line;
	
	/**
	 * Model constructor to load CSV data
	 * @param path  Path to file
	 * @param table  The table
	 * @param refresh  If the user is refreshing (true) or if it's the first load (false)
	 */
	public Model(File path, JTable table, boolean refresh, boolean isLastFile) {
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
                    header = reader.readNext();
				    Object[] headerWithRowNum = new Object[header.length + 1];
				    headerWithRowNum[0] = "#";
                    for(int i = 1; i < headerWithRowNum.length; i++) {
                        headerWithRowNum[i] = header[i-1];
                    }
                    header = headerWithRowNum;
				} catch (CsvValidationException e1) {
					showError("CSV Not Validated :(", e1);
				} catch (IOException e1) {
					showError("I/O Exception :(", e1);
				}
				//SwingUtilities.invokeAndWait(() -> model = new DefaultTableModel(header, 0)); // NOT invokeLater() because model HAS to be initialized immediately on EDT
				model = new DefaultTableModel(header, 0) {
                    private static final long serialVersionUID = -4147142493942463884L;

                    @Override
		            public boolean isCellEditable(int row, int column) {
		                return column != 0;
		            }
				};
				table.setModel(model);
				try {
				    int i = 1;
					while((line = reader.readNext()) != null) {
					    Object[] lineWithRowNum = new Object[line.length + 1];
					    lineWithRowNum[0] = i;
					    for(int j = 1; j < lineWithRowNum.length; j++) {
					        lineWithRowNum[j] = line[j-1];
					    }
					    line = lineWithRowNum;
						model.addRow(line);
						i++;
				    }
				} catch(Exception e) {
					showError("An Exception Occurred :(", e);
				}
				return null;
			}
			@Override
			protected void done() {
				try {
				    table.requestFocusInWindow();
				    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
				    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
				    table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					if(isLastFile) {
						removeBusyLabel(); // remove the busy label only after the last file (largest file) is over
					}
					if(refresh == true) {
						table.setModel(model);
						JOptionPane.showMessageDialog(View.getFrame(), "Refreshed data.");
					}
					else {
						JOptionPane.showMessageDialog(View.getFrame(), "Finished loading " + path.getName());
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
	 * Executes {@link #exportToCSV(String, JTable)} on a SwingWorker after creating the busy label
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
				int index = path.lastIndexOf('\\');
				String fileName = path.substring(index + 1, path.length());
				JOptionPane.showMessageDialog(View.getFrame(), "Finished saving " + fileName);
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

	        for(int i = 1; i < model.getColumnCount(); i++) {
	        	if(i != model.getColumnCount() - 1) {
	        		csv.write(model.getColumnName(i) + ",");
	        	}
	        	else {
	        		csv.write(model.getColumnName(i));
	        	}
	        }

	        csv.write("\n");

	        for(int i = 0; i < model.getRowCount(); i++) {
	            for(int j = 1; j < model.getColumnCount(); j++) {
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
	    	showError("IOException :(", e);
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
	 * Displays the busy label.
	 */
	private static void createBusyLabel() {
		if(Taskbar.getTaskbar().isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW)) {
			Taskbar.getTaskbar().setWindowProgressState(View.getFrame(), State.INDETERMINATE);
		}
		View.getBusyLabel().setBusy(true);
		View.getBusyLabel().setVisible(true);
	}
	
	/**
	 * Removes the busy label.
	 */
	private static void removeBusyLabel() {
		if(Taskbar.getTaskbar().isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW)) {
			Taskbar.getTaskbar().setWindowProgressState(View.getFrame(), State.OFF);
		}
		View.getBusyLabel().setBusy(false);
		View.getBusyLabel().setVisible(false);
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
