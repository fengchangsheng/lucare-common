package com.fcs.common.excel.importer.vo;

import java.util.List;

public class ExcelData {

	private String[] headers;

	private List<ExcelRecord> data;

	public ExcelData() {

	}

	public ExcelData(String[] headers, List<ExcelRecord> data) {
		super();
		this.headers = headers;
		this.data = data;
	}

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public List<ExcelRecord> getData() {
		return data;
	}

	public void setData(List<ExcelRecord> data) {
		this.data = data;
	}
}
