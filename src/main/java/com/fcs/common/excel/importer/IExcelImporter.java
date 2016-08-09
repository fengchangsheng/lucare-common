package com.fcs.common.excel.importer;

import com.fcs.common.excel.importer.vo.ExcelParserException;
import com.fcs.common.excel.importer.vo.ImportContext;

import java.io.IOException;
import java.io.InputStream;


public interface IExcelImporter {
	public boolean doImport(byte[] bytes, ImportContext context) throws ExcelParserException, IOException;
	public boolean doImport(InputStream ips, ImportContext context) throws ExcelParserException, IOException;
}
