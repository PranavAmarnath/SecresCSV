# SecresCSV

### A GUI for opening/viewing, editing, printing, and/or saving (beta) multiple CSV files at a time in tabular format.
#### The application can handle longer lines than Microsoft Excel<sup>1</sup>.

The purpose of this application is to view large CSV files to understand the data prior to data analysis.<p>
  
The application utilizes worker threads to add CSV data to a table. Each instance of the `Model` class adds data to a `DefaultTableModel` which can then be set to a `JTable`.

SecresCSV supports opening, saving, printing, selecting-all, and refreshing data from CSV files. It also comes with both light and dark themes.

Libraries:
* OpenCSV - reading CSV data
* FlatLaf - modern look and feel
* FlatLaf-Extras - reading SVG content

How the application looks:
![image](https://user-images.githubusercontent.com/64337291/110224833-3b4b9200-7e94-11eb-9cf3-2c408dc81e73.png)


###### <sup>1</sup>  Tested with a 382MB file
