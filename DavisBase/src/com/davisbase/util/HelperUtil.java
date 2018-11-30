package com.davisbase.util;

import java.io.File;

public class HelperUtil {
	
	public boolean isTableExist(String tableName) {

		File tableFile;
		if(tableName.equalsIgnoreCase(DavisBaseConstants.DAVISBASE_TABLES)
				|| tableName.equalsIgnoreCase(DavisBaseConstants.DAVISBASE_COLUMNS)) {
			tableFile = new File(DavisBaseConstants.DATA_DIR_NAME + "\\" + DavisBaseConstants.CATALOG);
		}
		else {
			tableFile = new File(DavisBaseConstants.DATA_DIR_NAME + "\\" + DavisBaseConstants.USERDATA);
		}
		
		for(String table : tableFile.list()) {
			if(tableName.equals(table))
				return true;
		}
		return false;
	}
	
	public String[] CheckCondition(String str) {
		String condition[] = new String[3];
		String values[] = new String[2];
		if (str.contains("=")) {
			values = str.split("=");
			condition[0] = values[0].trim();
			condition[1] = "=";
			condition[2] = values[1].trim();
		}

		if (str.contains(">")) {
			values = str.split(">");
			condition[0] = values[0].trim();
			condition[1] = ">";
			condition[2] = values[1].trim();
		}

		if (str.contains("<")) {
			values = str.split("<");
			condition[0] = values[0].trim();
			condition[1] = "<";
			condition[2] = values[1].trim();
		}

		if (str.contains(">=")) {
			values = str.split(">=");
			condition[0] = values[0].trim();
			condition[1] = ">=";
			condition[2] = values[1].trim();
		}

		if (str.contains("<=")) {
			values = str.split("<=");
			condition[0] = values[0].trim();
			condition[1] = "<=";
			condition[2] = values[1].trim();
		}

		if (str.contains("<>")) {
			values = str.split("<>");
			condition[0] = values[0].trim();
			condition[1] = "<>";
			condition[2] = values[1].trim();
		}

		return condition;
	}

}
