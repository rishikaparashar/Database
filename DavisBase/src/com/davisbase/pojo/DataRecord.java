package com.davisbase.pojo;

public class DataRecord {

	int pageNumber;
	short payLoadLength;
	int rowId;
	short recordLocation;
	byte numberOfColumns;
	byte[] dataTypeOfColumn;
	String[] payLoadContent;
	
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public short getPayLoadLength() {
		return payLoadLength;
	}
	public void setPayLoadLength(short payLoadLength) {
		this.payLoadLength = payLoadLength;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	public short getRecordLocation() {
		return recordLocation;
	}
	public void setRecordLocation(short recordLocation) {
		this.recordLocation = recordLocation;
	}
	public byte getNumberOfColumns() {
		return numberOfColumns;
	}
	public void setNumberOfColumns(byte numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}
	public byte[] getDataTypeOfColumn() {
		return dataTypeOfColumn;
	}
	public void setDataTypeOfColumn(byte[] dataTypeOfColumn) {
		this.dataTypeOfColumn = dataTypeOfColumn;
	}
	public String[] getPayLoadContent() {
		return payLoadContent;
	}
	public void setPayLoadContent(String[] payLoadContent) {
		this.payLoadContent = payLoadContent;
	}
	
	
}
