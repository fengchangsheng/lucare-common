package com.fcs.common.excel.importer.vo;

import java.util.HashMap;

public class ExcelRecord extends HashMap<String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3971563546294680142L;

	final int rowIndex; //行号

	ExcelRecordError errorMsg; // 错误信息

	public ExcelRecord(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public boolean isError() {
		return errorMsg != null && !errorMsg.isEmpty();
	}

	public void putError(String key, String msg) {
		if (errorMsg == null) {
			errorMsg = new ExcelRecordError();
		}
		errorMsg.put(key, msg);
	}

	public String getError(String key) {
		if (errorMsg == null) {
			return "";
		}
		return errorMsg.get(key);
	}

	public int getRowIndex() {
		return rowIndex;
	}
}
