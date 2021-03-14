# SecresCSV

### A GUI for opening/viewing, editing, printing, and/or saving (beta) multiple CSV files at a time in tabular format.
#### The application can handle longer lines than Microsoft Excel<sup>1</sup>.

The purpose of this application is to view large CSV files to understand the data prior to data analysis.<p>
  
The application utilizes worker threads to add CSV data to a table. Each instance of the `Model` class adds data to a `DefaultTableModel` which can then be set to a `JTable`.

[SecresCSV](https://github.com/PranavAmarnath/SecresCSV) supports opening, saving, printing, editing, and refreshing data from CSV files. It also comes with both light and dark themes.

Libraries:
* OpenCSV - reading CSV data
* FlatLaf - modern look and feel for Swing components
* SwingX - `JXBusyLabel` and `JXHyperlink`
* FlatLaf-SwingX - modern LAF for SwingX components
* FlatLaf-Extras - reading SVG content

How the application looks:
<p align="middle">
      <img src="https://user-images.githubusercontent.com/64337291/111054236-f5587600-841f-11eb-8f78-f2c684b36824.png" width="350" />
      <img src="https://user-images.githubusercontent.com/64337291/111054228-dd80f200-841f-11eb-9ef7-7a2fbf8e1976.png" width="350" /> 
</p>


###### <sup>1</sup>  Tested with a 382MB file
