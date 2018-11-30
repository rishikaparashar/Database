package com.davisbase.util;

import java.util.Comparator;
import com.davisbase.pojo.DataRecord;

public class RowIdComparator implements Comparator<DataRecord> {
	@Override
	public int compare(DataRecord a, DataRecord b) {
		return a.getRowId() < b.getRowId() ? -1 : a.getRowId() == b.getRowId() ? 0 : 1;
	}
}
