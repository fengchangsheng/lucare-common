package com.fcs.common.excel.util;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fcs.common.excel.importer.vo.ExcelParserException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExcelUtil {

	static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * 先后尝试创建 XSSFWorkbook 和 HSSFWorkbook
	 * 
	 * @param inputStream
	 * @return null - 未成功创建会返回空
	 */
	public static Workbook createWorkBook(InputStream inputStream) throws ExcelParserException {
		Workbook workbook = null;
		if (!inputStream.markSupported()) {
			inputStream = new PushbackInputStream(inputStream, 8);
		}
		try {
			if (POIFSFileSystem.hasPOIFSHeader(inputStream)) {
				workbook = new HSSFWorkbook(inputStream);
			}
			if (POIXMLDocument.hasOOXMLHeader(inputStream)) {
				workbook = new XSSFWorkbook(OPCPackage.open(inputStream));
			}
		} catch (Exception e) {
			logger.error("Try createWorkBook error!", e);
		}
		if (workbook == null) {
			throw new ExcelParserException("解析excel出错[请确认文件类型是否为xlsx]");
		}
		return workbook;
	}

	/**
	 * 给 cell 添加注释
	 * 
	 * @param value
	 * @param cell
	 * @param style
	 */
	public static void setComment(Workbook workBook, String value, Cell cell, CellStyle style) {
		Comment com = cell.getCellComment();
		if (com != null) {
			cell.removeCellComment();
			CellStyle styleNull = workBook.createCellStyle();
			cell.setCellStyle(styleNull);
		}
		if (value != null) {
			Drawing drawing = cell.getSheet().createDrawingPatriarch();
			CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			anchor.setCol1(cell.getColumnIndex());
			anchor.setCol2(cell.getColumnIndex() + 3);
			anchor.setRow1(cell.getRowIndex());
			anchor.setRow2(cell.getRowIndex() + 2);
			Comment comment = drawing.createCellComment(anchor);
			comment.setVisible(Boolean.FALSE);
			RichTextString str = factory.createRichTextString(value);
			comment.setString(str);
			cell.setCellStyle(style);
			cell.setCellComment(comment);
		}
	}

	/**
	 * 删除数据
	 * 
	 * @param sheet
	 * @param startRow
	 *            -从0行开始
	 * @param lastRow
	 * @param startCol
	 *            -从0列开始
	 * @param lastCol
	 */
	public static void removeColumns(Sheet sheet, int startRow, int lastRow, int startCol, int lastCol) {
		for (int i = startRow; i < lastRow; i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				for (int j = startCol; j < lastCol; j++) {
					Cell cell = row.getCell(j);
					if (cell != null) {
						row.removeCell(cell);
					}
				}
			}
		}
	}

	/**
	 * 解析某一行
	 * 
	 * @param sheet
	 * @param rowIndex
	 * @return 为空会返回NULL
	 */
	public static String[] readRow(Sheet sheet, int rowIndex) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			return null;
		}
		int columns = row.getPhysicalNumberOfCells();
		if (columns == 0) {
			return null;
		}
		String[] values = new String[columns];
		// 解析header
		for (int i = 0; i < columns; i++) {
			values[i] = ExcelUtil.getStringCellValue(row.getCell(i));
		}
		return values;
	}

	public static String getStringCellValue(Cell cell) {
		String strCell = "";
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue().trim();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				try {
					Date d = cell.getDateCellValue();
					DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
					strCell = fmt.format(d);
				} catch (Exception e) {
					//
				}
			} else {
				strCell = ExcelUtil.trimZero(String.format("%f", cell.getNumericCellValue()));
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		if (strCell.equals("") || strCell == null) {
			return "";
		}
		return strCell;
	}

	public static boolean isBlankRow(Row row, int lastColumnIndex) {
		if (row == null)
			return true;
		boolean result = true;
		for (int i = row.getFirstCellNum(); i < lastColumnIndex; i++) {
			Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
			String value = "";
			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					value = String.valueOf((int) cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					value = String.valueOf(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					value = String.valueOf(cell.getCellFormula());
					break;
				default:
					break;
				}

				if (!value.trim().equals("")) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	public static boolean isBlankRow(Row row) {
		return isBlankRow(row, row.getLastCellNum());
	}

	/**
	 * 单元格下拉
	 * 
	 * @param sheet
	 * @param areaStr
	 *            - "$B$1:$B$10"
	 * @param startRow
	 * @param endRow
	 * @param startCol
	 * @param endCol
	 */
	public static void setCellDropDown(Sheet sheet, String areaStr, int startRow, int endRow, int startCol, int endCol) {
		DataValidationHelper dvHelper = sheet.getDataValidationHelper();
		DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(areaStr);
		CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, startCol, endCol);
		DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
		if (validation instanceof XSSFDataValidation) {
			validation.setSuppressDropDownArrow(true);
			validation.setShowErrorBox(true);
		} else {
			validation.setSuppressDropDownArrow(true);
		}
		sheet.addValidationData(validation);
	}

	/**
	 * 单元格下拉
	 * 
	 * @param sheet
	 * @param contents
	 * @param startRow
	 * @param endRow
	 * @param startCol
	 * @param endCol
	 */
	public static void setCellDropDown(Sheet sheet, String[] contents, int startRow, int endRow, int startCol, int endCol) {
		DataValidationHelper dvHelper = sheet.getDataValidationHelper();
		DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(contents);
		CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, startCol, endCol);
		DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
		if (validation instanceof XSSFDataValidation) {
			validation.setSuppressDropDownArrow(true);
			validation.setShowErrorBox(true);
		} else {
			validation.setSuppressDropDownArrow(true);
		}
		sheet.addValidationData(validation);
	}

	public static Row getRow(Sheet sheet, int index) {
		Row row = sheet.getRow(index);
		if (row == null) {
			row = sheet.createRow(index);
		}
		return row;
	}

	public static Cell getCell(Row row, int index) {
		Cell cell = row.getCell(index);
		if (cell == null) {
			cell = row.createCell(index);
		}
		return cell;
	}

	//---------------基础方法

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (Exception s) {
			return false;
		}
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception s) {
			return false;
		}
	}

	public static boolean isLong(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (Exception s) {
			return false;
		}
	}

	public static Date isDate(String str, String format) {
		try {
			DateFormat df = new SimpleDateFormat(format);
			return df.parse(str);
		} catch (Exception s) {
			return null;
		}
	}

	public static boolean isNullOrEmpty(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}

	public static String trimZero(String number) {
		if (number.indexOf(".") > 0) {
			while (number.endsWith("0")) {
				number = number.substring(0, number.length() - 1);
			}
			if (number.endsWith(".")) {
				number = number.replace(".", "");
			}
		}
		return number;
	}

	public static String trimZero(double number) {
		return trimZero(number + "");
	}

	public static Integer str2Int(String str) {
		Integer i = null;
		try {
			i = Integer.parseInt(str);
		} catch (Exception e) {
		}
		return i;
	}

	/**
	 * 使用java正则表达式去掉多余的.与0
	 * 
	 * @param s
	 * @return
	 */
	public static String subZeroAndDot(String s) {
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");// 去掉多余的0
			s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return s;
	}

	/**
	 * 去除分隔符前后的空格
	 * 
	 * @param resource
	 * @param separator
	 * @return
	 */
	public static String trimSeparator(String resource, String separator) {
		String[] strs = resource.split(separator);
		String s = "";
		for (String str : strs) {
			if (!isNullOrEmpty(str)) {
				if (!"".equals(s)) {
					s += separator;
				}
				s += str.trim();
			}
		}
		return s;
	}
}
