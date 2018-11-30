package com.davisbase.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.davisbase.pojo.DataRecord;
import com.davisbase.util.DavisBaseConstants;
import com.davisbase.util.RowIdComparator;

public class BuildTree {

	
	public static void initialzeCatalog() {
		
		try {
			File data = new File(DavisBaseConstants.DATA_DIR_NAME);
			
			if(!data.exists()) {
				data.mkdir();
			}
			File catalog = new File(DavisBaseConstants.DATA_DIR_NAME + "\\" + DavisBaseConstants.CATALOG);
			
			if(!catalog.exists()) {
				System.out.println("Catalog Directory does not exit... Creating directory...");
				catalog.mkdir();
				createCatalogDirectories();
			}
			else {
				String[] tableList = catalog.list();		
				if(!Arrays.asList(tableList).contains(DavisBaseConstants.DAVISBASE_TABLES + DavisBaseConstants.TABLE_EXTENSION) ||
						!Arrays.asList(tableList).contains(DavisBaseConstants.DAVISBASE_COLUMNS + DavisBaseConstants.TABLE_EXTENSION)) {
					System.out.println("Catalog tables does not exist... Creating new....");
					createCatalogDirectories();
				}
			}
		}
		catch (SecurityException se) 
		{
			System.out.println("Catalog files not created ");
			se.printStackTrace();
		}
	}
	
	public static void createUserData() {

		try {
			File userData = new File(DavisBaseConstants.DATA_DIR_NAME + "\\" + DavisBaseConstants.USERDATA);
			
			if(!userData.exists()) {
				System.out.println("User_Data Directory does not exit... Creating directory...");
				userData.mkdir();
			}
		} catch (SecurityException se) {
			// TODO Auto-generated catch block
			System.out.println("User_data cannot be created ");
			se.printStackTrace();
		}
	}
	
	private static void createCatalogDirectories() {
		
			try {
				File catalog = new File(DavisBaseConstants.DATA_DIR_NAME + "\\" + DavisBaseConstants.CATALOG);
				
				/*Deleting existing files in catalog*/
				for(File content : catalog.listFiles()) {
					content.delete();
				}
			} catch (SecurityException se) {
				// TODO Auto-generated catch block
				System.out.println("Unable to delete the catalog files... ");
				se.printStackTrace();
			}
			createDavisBaseTable();
			createDavisBaseColumn();
	}
	
	private static void createDavisBaseTable() {
		
		try {
			/*Create new table page*/
			RandomAccessFile davisbaseTable = new RandomAccessFile(DavisBaseConstants.DATA_DIR_NAME + "\\"
																	+ DavisBaseConstants.CATALOG + "\\"
																	+ DavisBaseConstants.DAVISBASE_TABLES
																	+ DavisBaseConstants.TABLE_EXTENSION, "rw");
			davisbaseTable.setLength(DavisBaseConstants.PAGE_SIZE); // set page size
			davisbaseTable.seek(0);
			davisbaseTable.writeByte(0x0D); // Set page type to leaf table
			davisbaseTable.writeByte(DavisBaseConstants.NUMBER_OF_CATALOG_DATA); // By default number of catalog data is 2
			
			int[] addressOffset = new int[2];
			
			/*Size of record is payload + cell header*/
			int[] recordSize = {18, 19}; // 18 + 6, 19 + 6 
			
			for (int i = 0; i < addressOffset.length; i++) {
				if(i == 0) { // First record is stored at the end of the page
					addressOffset[i] =  (int) (DavisBaseConstants.PAGE_SIZE - (recordSize[0] + DavisBaseConstants.CELL_HEADER_SIZE));
				}			
				else {
					addressOffset[i] = addressOffset[i - 1] - (recordSize[i] + DavisBaseConstants.CELL_HEADER_SIZE); // totalsize of each record is payload + 6
				}			
			}
			
			/* Update Page Header */
			davisbaseTable.writeShort(addressOffset[addressOffset.length-1]); // store the address where the content begins
			davisbaseTable.writeInt(-1); // store the right sibling page number, as of now it is null
			davisbaseTable.writeShort(addressOffset[0]); // Store the address of first record sorted by row_id
			davisbaseTable.writeShort(addressOffset[1]); // Store the address of second record sorted by row_id
			
			/* Writing first record, davisbase_tables*/
			davisbaseTable.seek(addressOffset[0]);
			davisbaseTable.writeShort(recordSize[0]); // Length of payload of davisbase_tables
			davisbaseTable.writeInt(1); // row_id
			davisbaseTable.writeByte(1); // one column
			davisbaseTable.writeByte(28); // Datatype of column, payload_length + cell_header length + 0x0C
			davisbaseTable.writeBytes("davisbase_tables");
			
			/* Writing first record, davisbase_columns */
			davisbaseTable.seek(addressOffset[1]);
			davisbaseTable.writeShort(recordSize[1]); // Length of payload of davisbase_columns
			davisbaseTable.writeInt(2); // row_id
			davisbaseTable.writeByte(1); // one column
			davisbaseTable.writeByte(29); // Datatype of column, payload_length + cell_header length + 0x0C
			davisbaseTable.writeBytes("davisbase_columns");
			davisbaseTable.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to create davisBase_tables in catalog");
			e.printStackTrace();
		}
	}
	
	private static void createDavisBaseColumn() {
		
		/*Create new table page*/
		try {
			RandomAccessFile davisbaseColumn = new RandomAccessFile(DavisBaseConstants.DATA_DIR_NAME + "\\"
																	+ DavisBaseConstants.CATALOG + "\\"
																	+ DavisBaseConstants.DAVISBASE_COLUMNS
																	+ DavisBaseConstants.TABLE_EXTENSION, "rw");
			davisbaseColumn.setLength(DavisBaseConstants.PAGE_SIZE); // set page size
			davisbaseColumn.seek(0);
			davisbaseColumn.writeByte(0x0D); // Set page type to leaf table
			davisbaseColumn.writeByte(0x08); // Number of records in davisbase_columns initially
			
			/*payload size 27, 33, 28, 34, 35, 43, 35*/
			int[] recordSize = {33, 39, 34, 40, 41, 39, 49, 41};
			int[] addressOffset = new int[8];
			
			for (int i = 0; i < addressOffset.length; i++) {
				if(i == 0) {
					addressOffset[i] =  (int) (DavisBaseConstants.PAGE_SIZE - (recordSize[0] + DavisBaseConstants.CELL_HEADER_SIZE));
				}
				else {
					addressOffset[i] = addressOffset[i - 1] - (recordSize[i] + DavisBaseConstants.CELL_HEADER_SIZE); // totalsize of each record is payload + 6
				}			
			}
			
			davisbaseColumn.writeShort(addressOffset[addressOffset.length-1]); // store the address where the content begins
			davisbaseColumn.writeInt(-1);// store the right sibling page number, as of now it is null
			
			/*Store the adderss of record sorted by row_id*/
			for (int i = 0; i < addressOffset.length; i++) {
				davisbaseColumn.writeShort(addressOffset[i]);
			}
			
			/* Writing first record */
			davisbaseColumn.seek(addressOffset[0]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[0]); // Length of payload
			davisbaseColumn.writeInt(1); // row_id	
			/*Payload details*/
			davisbaseColumn.writeByte(5); // Number of columns
			/*Datatype of columns*/
			davisbaseColumn.writeByte(28); // table_name
			davisbaseColumn.writeByte(17); // column_name
			davisbaseColumn.writeByte(15); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_tables");
			davisbaseColumn.writeBytes("rowid");
			davisbaseColumn.writeBytes("INT");
			davisbaseColumn.writeByte(1);
			davisbaseColumn.writeBytes("NO");

			/* Writing 2nd record */
			davisbaseColumn.seek(addressOffset[1]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[1]);
			davisbaseColumn.writeInt(2); // row_id
			/* Payload details*/
			davisbaseColumn.writeByte(5); // Number of columns
			/* DataType of columns */
			davisbaseColumn.writeByte(28); // table_name
			davisbaseColumn.writeByte(22); // column_name
			davisbaseColumn.writeByte(16); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_tables");
			davisbaseColumn.writeBytes("table_name");
			davisbaseColumn.writeBytes("TEXT");
			davisbaseColumn.writeByte(2);
			davisbaseColumn.writeBytes("NO");

			/* Writing 3rd record */
			davisbaseColumn.seek(addressOffset[2]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[2]);
			davisbaseColumn.writeInt(3); // row_id
			/* Payload details*/
			davisbaseColumn.writeByte(5); // Number of columns
			/* DataType of columns */
			davisbaseColumn.writeByte(29); // table_name
			davisbaseColumn.writeByte(17); // column_name
			davisbaseColumn.writeByte(15); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_columns");
			davisbaseColumn.writeBytes("rowid");
			davisbaseColumn.writeBytes("INT");
			davisbaseColumn.writeByte(1);
			davisbaseColumn.writeBytes("NO");

			/* Writing 4th record */
			davisbaseColumn.seek(addressOffset[3]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[3]);
			davisbaseColumn.writeInt(4); // row_id
			/* Payload details*/
			davisbaseColumn.writeByte(5); // Number of columns
			/* DataType of columns */
			davisbaseColumn.writeByte(29); // table_name
			davisbaseColumn.writeByte(22); // column_name
			davisbaseColumn.writeByte(16); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_columns");
			davisbaseColumn.writeBytes("table_name");
			davisbaseColumn.writeBytes("TEXT");
			davisbaseColumn.writeByte(2);
			davisbaseColumn.writeBytes("NO");

			/* Writing 5th record */
			davisbaseColumn.seek(addressOffset[4]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[4]);
			davisbaseColumn.writeInt(5); // row_id
			/* Payload details*/
			davisbaseColumn.writeByte(5); // Number of columns
			/* DataType of columns */
			davisbaseColumn.writeByte(29); // table_name
			davisbaseColumn.writeByte(23); // column_name
			davisbaseColumn.writeByte(16); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_columns");
			davisbaseColumn.writeBytes("column_name");
			davisbaseColumn.writeBytes("TEXT");
			davisbaseColumn.writeByte(3);
			davisbaseColumn.writeBytes("NO");

			/* Writing 6th record */
			davisbaseColumn.seek(addressOffset[5]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[5]);
			davisbaseColumn.writeInt(6); // row_id
			/* Payload details*/
			davisbaseColumn.writeByte(5); // Number of columns
			/* DataType of columns */
			davisbaseColumn.writeByte(29); // table_name
			davisbaseColumn.writeByte(21); // column_name
			davisbaseColumn.writeByte(16); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_columns");
			davisbaseColumn.writeBytes("data_type");
			davisbaseColumn.writeBytes("TEXT");
			davisbaseColumn.writeByte(4);
			davisbaseColumn.writeBytes("NO");

			/* Writing 7th record */
			davisbaseColumn.seek(addressOffset[6]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[6]);
			davisbaseColumn.writeInt(7); // row_id
			/* Payload details */
			davisbaseColumn.writeByte(5); // Number of columns
			/* DataType of columns */
			davisbaseColumn.writeByte(29); // table_name
			davisbaseColumn.writeByte(28); // column_name
			davisbaseColumn.writeByte(19); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_columns");
			davisbaseColumn.writeBytes("ordinal_position");
			davisbaseColumn.writeBytes("TINYINT");
			davisbaseColumn.writeByte(5);
			davisbaseColumn.writeBytes("NO");

			/* Writing 8th record */
			davisbaseColumn.seek(addressOffset[7]);
			/* Record Header */
			davisbaseColumn.writeShort(recordSize[7]);
			davisbaseColumn.writeInt(8); // row_id
			/* Payload details */
			davisbaseColumn.writeByte(5); // Number of columns
			/* DataType of columns */
			davisbaseColumn.writeByte(29); // table_name
			davisbaseColumn.writeByte(23); // column_name
			davisbaseColumn.writeByte(16); // data_type
			davisbaseColumn.writeByte(4); // ordinal_position
			davisbaseColumn.writeByte(14); // is_nullable
			/* Content in the cells, payload*/
			davisbaseColumn.writeBytes("davisbase_columns");
			davisbaseColumn.writeBytes("is_nullable");
			davisbaseColumn.writeBytes("TEXT");
			davisbaseColumn.writeByte(6);
			davisbaseColumn.writeBytes("NO");

			davisbaseColumn.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to create davisBase_columns in catalog");
			e.printStackTrace();
		}
	}
	
	public static void DropTable(String tableName) {
		
	}
	
	public static void SelectQuery(String tableName, String[] columnNames, String[] condition) {
		
	}
	
	public static void UpdateQuery(String tableName, String[] updateValues, String[] condition) {
		
	}
	
	public static void createTable(String tableName, String[] columnNames) {
		
		RandomAccessFile tableFile;
		try {
			tableFile = new RandomAccessFile(DavisBaseConstants.DATA_DIR_NAME + "\\"
												+ DavisBaseConstants.USERDATA + "\\"
												+ tableName + DavisBaseConstants.TABLE_EXTENSION, "rw");
			tableFile.setLength(DavisBaseConstants.PAGE_SIZE);
			/*Creating page header*/
			tableFile.seek(0);
			tableFile.writeByte(0x0D); // Leaf page type
			tableFile.seek(2);
			tableFile.writeShort((int)DavisBaseConstants.PAGE_SIZE); // cell content area starts at the end by default
			tableFile.writeInt(-1); // store the right sibling page number, as of now it is null
			tableFile.close();
			
			/*Update davisbase_table and davis_base columns*/
			updateDavisBaseTable(tableName); // insert to davisbase_table should be added when the insert method is implemented
			updateDavisBaseColumn(tableName, columnNames); // insert to davisbase_column should be added when the insert method is implemented
			
		} catch(Exception e) {
			System.out.println("Not able to create the table... Error occured...");
			e.printStackTrace();
		}
	}

	private static void updateDavisBaseTable(String tableName) throws FileNotFoundException, IOException {
		RandomAccessFile davisbaseTable = new RandomAccessFile(DavisBaseConstants.DATA_DIR_NAME + "\\"
																+ DavisBaseConstants.CATALOG + "\\"
																+ DavisBaseConstants.DAVISBASE_TABLES
																+ DavisBaseConstants.TABLE_EXTENSION, "rw");
		int numberOfPages = (int) (davisbaseTable.length() / DavisBaseConstants.PAGE_SIZE);
		
		List<DataRecord> dataList = getCatalogTableData(davisbaseTable, numberOfPages); // Obtain the davisbase_table records
		
		/*Getting the key to enter the next record
		 * It will be the row_id + 1 of the last record*/
		int key = dataList.get(dataList.size() - 1).getRowId() + 1;		
		String[] values = { Integer.toString(key), tableName };
		
		
		/*Add insert into table method here to insert values to davisbase_table
		 * insertToTable(davisbase_table, values)*/
		
		
	}
	
	private static void updateDavisBaseColumn(String tableName, String[] columnNames) throws FileNotFoundException, IOException {
		RandomAccessFile davisbaseColumn = new RandomAccessFile(DavisBaseConstants.DATA_DIR_NAME + "\\"
																+ DavisBaseConstants.CATALOG + "\\"
																+ DavisBaseConstants.DAVISBASE_COLUMNS
																+ DavisBaseConstants.TABLE_EXTENSION, "rw");
		int numberOfPages = (int) (davisbaseColumn.length() / DavisBaseConstants.PAGE_SIZE);
		
		List<DataRecord> dataList = getCatalogTableData(davisbaseColumn, numberOfPages); // Obtain the davisbase_table records
		
		/*Getting the key to enter the next record
		 * Last key will be the row_id of the last record inserted*/
		int key = dataList.get(dataList.size() - 1).getRowId();
		
		for (int i = 0; i < columnNames.length; i++) {
				
			key += 1;
			String[] columnDetails = columnNames[i].split(" "); // contains column_name and data_type		
			String isNullable = "YES";

			/*if the columnDetails length is greater than 2, it means there is a constraint
			 * which will be either primary key or not null, in both cases the attribute isNullbale will be false*/
			if (columnDetails.length > 2) {
				isNullable = "NO";
			}
			String columnName = columnDetails[0].trim();
			String dataType = columnDetails[1].toUpperCase();
			String ordinalPosition = Integer.toString(i + 1);
			String[] values = { Integer.toString(key), tableName, columnName, dataType, ordinalPosition, isNullable };
			
			/*Add insert into table method here to insert values to davisbase_column
			 * insertToTable(davisbase_column, values)*/
			
		}
	}

	private static List<DataRecord> getCatalogTableData(RandomAccessFile tableFile, int numberOfPages)
			throws IOException {
		Set<DataRecord> dataSet = new LinkedHashSet<DataRecord>(); // Used to store the data in the page in the order of insertion
		
		int numberOfRecords;
		short[] dataOffset;
		for (int pageNumber = 0; pageNumber < numberOfPages; pageNumber++) {
			tableFile.seek((pageNumber * DavisBaseConstants.PAGE_SIZE) + 4);
			if (tableFile.readInt() == -1) { // Checking whether the page is the rightmost leaf page
				tableFile.seek((pageNumber * DavisBaseConstants.PAGE_SIZE) + 1); // Number of cells in the page
				numberOfRecords = tableFile.readByte();
				dataOffset = new short[numberOfRecords];
				tableFile.seek((pageNumber * DavisBaseConstants.PAGE_SIZE) + 8); // The starting address of each record
				for (int offset = 0; offset < numberOfRecords; offset++) {
					dataOffset[offset] = tableFile.readShort();
				}
				dataSet = getData(tableFile, dataOffset, pageNumber);
			}
		}
		
		tableFile.close();
		
		List<DataRecord> dataList = new ArrayList<DataRecord>(dataSet);
		Collections.sort(dataList, new RowIdComparator()); // Sorting all the records in the datalist according to row_id
		return dataList;
	}
	
	private static Set<DataRecord> getData(RandomAccessFile tableFile, short[] dataOffset, int pageNumber){
		
		Set<DataRecord> dataSet = new LinkedHashSet<DataRecord>();
		
		try {
			for(int k = 0; k < dataOffset.length; k++) {
				DataRecord dataRecord = new DataRecord();
				dataRecord.setPageNumber(pageNumber);
				dataRecord.setRecordLocation(dataOffset[k]);
				tableFile.seek(dataOffset[k]);
				dataRecord.setPayLoadLength(tableFile.readShort()); // cell header contains the payload length
				dataRecord.setRowId(tableFile.readInt()); // rowid of the content
				
				dataRecord.setNumberOfColumns(tableFile.readByte()); // number of columns is stored in the next location
				
				byte[] dataTypes = new byte[dataRecord.getNumberOfColumns()];
				tableFile.read(dataTypes);
				dataRecord.setDataTypeOfColumn(dataTypes);
				
				String[] payLoadContent = new String[dataRecord.getNumberOfColumns()];
				dataRecord.setPayLoadContent(payLoadContent);
				
				int i=0;
				long dateLength;
				for(byte dataType : dataTypes) { // check each datatype
					
					/*Logic to find the datatype*/
					switch(dataType) {

					case 0x00: // 1 byte null
						payLoadContent[i] = Integer.toString(tableFile.readByte());
						payLoadContent[i] = "null";
						break;

					case 0x01: // 2 byte null
						payLoadContent[i] = Integer.toString(tableFile.readShort());
						payLoadContent[i] = "null";
						break;

					case 0x02: // 4 byte null
						payLoadContent[i] = Integer.toString(tableFile.readInt());
						payLoadContent[i] = "null";
						break;

					case 0x03: // 8 byte null
						payLoadContent[i] = Long.toString(tableFile.readLong());
						payLoadContent[i] = "null";
						break;

					case 0x04: // Byte datatype
						payLoadContent[i] = Integer.toString(tableFile.readByte());
						break;

					case 0x05: // Short datatype
						payLoadContent[i] = Integer.toString(tableFile.readShort());
						break;

					case 0x06:  // int datatype
						payLoadContent[i] = Integer.toString(tableFile.readInt());
						break;

					case 0x07:  // long datatype
						payLoadContent[i] = Long.toString(tableFile.readLong());
						break;

					case 0x08: // float datatype
						payLoadContent[i] = String.valueOf(tableFile.readFloat());
						break;

					case 0x09: // double datatype
						payLoadContent[i] = String.valueOf(tableFile.readDouble());
						break;

					case 0x0A:
						dateLength = tableFile.readLong();
						Date dateTime = new Date(dateLength);
						payLoadContent[i] = new SimpleDateFormat(DavisBaseConstants.DATE_PATTERN).format(dateTime);
						break;

					case 0x0B:
						dateLength = tableFile.readLong();
						Date date = new Date(dateLength);
						payLoadContent[i] = new SimpleDateFormat(DavisBaseConstants.DATE_PATTERN).format(date).substring(0, 10);
						break;

					default:
						int textlength = new Integer(dataType - 0x0C);
						byte[] bytes = new byte[textlength];
						for (int j = 0; j < textlength; j++)
							bytes[j] = tableFile.readByte();
						payLoadContent[i] = new String(bytes);
						break;
					
					}
					i += 1;
				}
				dataSet.add(dataRecord);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Not able to create the table... Error occured...");
			e.printStackTrace();
		}
		
		return dataSet;
	}
}
