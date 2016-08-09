package com.fcs.common.excel.exporter;
import java.util.Map;

public class CommonExcelExporter extends AbstractExcelExporter<Map<String,Object>> {
	String[] excelHeaders;
	String[] excelTitles;
	
	public CommonExcelExporter() {
	}

	public CommonExcelExporter(String[][] headerAndTitle) {
		this.excelHeaders = headerAndTitle[0];
		this.excelTitles = headerAndTitle[1];
	}
	
	@Override
	protected String[] getExcelHeaders() {
		return excelHeaders;
	}
	
	@Override
	protected String[] getExcelTitles() {
		return excelTitles;
	}

	@Override
	protected Map<String, Object> resolveEntity(Map<String, Object> entity) {
		return entity;
	}
	
	public void SetExcelHeaders(String [] excelHeaders) {
		this.excelHeaders = excelHeaders;
	}
	
	public void SetExcelTitles(String [] excelTitles) {
		this.excelTitles = excelTitles;
	}
	
}
