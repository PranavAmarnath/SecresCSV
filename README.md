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
<p align="left">
      <img src="https://user-images.githubusercontent.com/64337291/111371972-5027e280-8657-11eb-85cb-e561cabe7796.png" width="370" />
      <img src="https://user-images.githubusercontent.com/64337291/111372286-aac13e80-8657-11eb-9984-0af0e9bae470.png" width="370" /> 
</p>


###### <sup>1</sup>  Tested with a 382MB file
