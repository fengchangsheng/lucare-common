package com.fcs.common.excel.importer.vo;

import java.util.HashMap;

public class ImportContext extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6573643980200863334L;

	private String errorMsg;

	private String resultMsg;

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
