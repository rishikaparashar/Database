package com.davisbase.pojo;

import java.util.Set;

public class Page {

	int pageNumber;
	byte pageType;
	Set<DataRecord> pageRecords;
	
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public byte getPageType() {
		return pageType;
	}
	public void setPageType(byte pageType) {
		this.pageType = pageType;
	}
	public Set<DataRecord> getPageRecords() {
		return pageRecords;
	}
	public void setPageRecords(Set<DataRecord> pageRecords) {
		this.pageRecords = pageRecords;
	}
}
