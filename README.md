# SecresCSV

A Java Swing application for opening/viewing, saving, printing, editing and/or refreshing multiple CSV files at a time in tabular format.
The application can handle longer lines than Microsoft Excel<sup>1</sup>.

## Download
<a href="https://github.com/PranavAmarnath/SecresCSV/releases/download/v3.0/secrescsv-3.0-SNAPSHOT.jar">
    <img src="https://img.shields.io/badge/SecresCSV-3.0-blue" alt="Download SecresCSV 3.0" />
</a>
<p>
Run with java -jar secrescsv-3.0-SNAPSHOT.jar (or double-click it). Requires Java 17 or newer.

## Insight
The purpose of this application is to view large CSV files to understand the data prior to data analysis.<p>
  
The application utilizes worker threads to add CSV data to a table. Each instance of the `Model` class adds data to a `DefaultTableModel` which can then be set to a `JXTable`.

[SecresCSV](https://github.com/PranavAmarnath/SecresCSV) supports opening, saving, printing, editing, and refreshing data from CSV files. It also comes with both light and dark themes.

Libraries:
* OpenCSV - reading CSV data
* FlatLaf - modern look and feel for Swing components
* SwingX - `JXBusyLabel`, `JXTable`, and `JXHyperlink`
* FlatLaf-SwingX - modern LAF for SwingX components
* FlatLaf-Extras - reading SVG content

How the application looks:
<p align="left">
      <img src="https://user-images.githubusercontent.com/64337291/173295138-797786be-f705-4637-9a46-861c2faf74d7.png" width="370" />
      <img src="https://user-images.githubusercontent.com/64337291/173429621-5fc88ee7-9408-4b09-9adf-d7ec3fbaa14c.png" width="370" /> 
</p>

###### <sup>1</sup>  Tested with a 382MB file
