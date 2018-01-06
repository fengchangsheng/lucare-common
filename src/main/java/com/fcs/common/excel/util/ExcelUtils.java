package com.fcs.common.excel.util;

import com.fcs.common.excel.vo.THeadAttr;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

	public static final int COLUMN_WIDTH_DEFAULT = 5000;// 默认列宽

	public static final short ROW_HEIGHT_DEFAULT = 500;// 默认行高

	public static final short TITLE_ROW_HEIGHT_DEFAULT = 700;// 列标题默认行高

	/**
	 * 自定义颜色
	 * 
	 * @param workbook
	 */
	public static void setCustomColorByRgb(HSSFWorkbook workbook,
			int colorIndex, int red, int green, int blue) {
		// replacing the standard red with freebsd.org red
		HSSFPalette palette = workbook.getCustomPalette();
		// RGB red green blue
		palette.setColorAtIndex((short) colorIndex, (byte) red, (byte) green, (byte) blue);
	}

	/**
	 * 单元格样式
	 * 
	 * @param workbook
	 *            表格对象
	 * @param colorIndex
	 *            背景颜色
	 * @param align
	 *            对齐方式
	 * @param fontSize
	 *            字体大小
	 */
	public static HSSFCellStyle getCustomCellStyle(HSSFWorkbook workbook,
			int colorIndex, int align, int fontSize) {
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		// 设置边框
		titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// 设置背景色
		if (colorIndex != -1) {
			titleStyle.setFillForegroundColor((short)colorIndex);
			titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}
		// 设置居中
		if (align != -1) {
			titleStyle.setAlignment((short) align);
		} else {
			titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		}
		titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 设置字体
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		if (fontSize != -1) {
			font.setFontHeightInPoints((short) fontSize);
		}else{
			font.setFontHeightInPoints((short) 12); // 设置字体大小
		}
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		titleStyle.setFont(font);// 选择需要用到的字体格式
		// 设置自动换行
		titleStyle.setWrapText(true);
		return titleStyle;
	}

	/**
	 * 列标题单元格样式
	 * 
	 * @param workbook
	 */
	public static HSSFCellStyle getTitleCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		// 设置边框
		titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// 设置背景色
		// titleStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		// titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置居中
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 设置字体
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12); // 设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		titleStyle.setFont(font);// 选择需要用到的字体格式
		// 设置自动换行
		titleStyle.setWrapText(true);
		return titleStyle;
	}

	/**
	 * 数据单元格样式
	 * 
	 * @param workbook
	 */
	public static HSSFCellStyle getDataCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle dataStyle = workbook.createCellStyle();
		// 设置边框
		dataStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		dataStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		dataStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		dataStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// 设置背景色
		// dataStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		// dataStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置居中
		dataStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dataStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 设置字体
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 11); // 设置字体大小
		dataStyle.setFont(font);// 选择需要用到的字体格式
		// 设置自动换行
		// dataStyle.setWrapText(true);
		return dataStyle;
	}

	/**
	 * 导出（支持单行、多行表头，支持表头合并单元格）
	 * 
	 * @param columnAttrList
	 *            列属性列表
	 * @param dataList
	 *            导出明细数据
	 * @param totalMap
	 *            合计数据
	 * @param outFile
	 *            导出文件名称
	 */
	public static void export(List<ColumnAttr> columnAttrList,
			List<Map<String, Object>> dataList, Map<String, Object> totalMap,
			String outFile) {
		if (StringUtils.isEmpty(outFile)) {
			return;
		}
		if (null == columnAttrList || columnAttrList.isEmpty()) {
			return;
		}
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");
		// 总列数
		int colSize = columnAttrList.size();
		// 设置列宽
		for (int i = 0; i < colSize; i++) {
			sheet.setColumnWidth(i, columnAttrList.get(i).getColumnWidth());
		}
		// 行标
		int rowIndex = 0;
		// 标题行
		rowIndex = ExcelUtils.buildTitleRows(workbook, columnAttrList, getTitleList(columnAttrList));
		// 数据行
		rowIndex = ExcelUtils.buildDataRows(workbook, columnAttrList, dataList, rowIndex);
		// 合计行
		ExcelUtils.buildTotalRow(workbook, columnAttrList, totalMap, rowIndex);

		OutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
			workbook.write(out);
		} catch (Exception ex) {
			throw new RuntimeException("导出excel错误!", ex);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 填充单元格内容
	 * 
	 * @param cell
	 *            单元格
	 * @param dataValue
	 *            填充值
	 * @param dataType
	 *            数据类型
	 * @param defaultValue
	 *            默认值
	 */
	public static void setCellValue(HSSFCell cell, Object dataValue, String dataType, String defaultValue) {
		String valueStr = null;
		if (null != dataValue) {
			valueStr = String.valueOf(dataValue);
		} else {
			if (null != defaultValue) {
				valueStr = defaultValue;
			}
		}
		if ("FEE_CENT".equals(dataType)) {// 结果值分转元
			double cellValue = 0;
			if (null != valueStr) {
				cellValue = ExcelFormat.converCentToYuan(new BigDecimal(valueStr)).doubleValue();
			}
			cell.setCellValue(cellValue);
		} else if ("PERCENTAGE".equals(dataType)) {// 结果值小数转百分比
			String cellValue = null;
			if (null != valueStr) {
				cellValue = ExcelFormat.getPercent(Double.valueOf(valueStr));
			} else {
				cellValue = "";
			}
			cell.setCellValue(cellValue);
		} else {
			String cellValue = null;
			if (null != valueStr) {
				cellValue = valueStr;
			} else {
				cellValue = "";
			}
			cell.setCellValue(cellValue);
		}
	}

	/**
	 * 构建数据行
	 * 
	 * @param workbook
	 *            excel表
	 * @param columnAttrList
	 *            列属性列表
	 * @param dataList
	 *            数据列表
	 * @param startRowIndex
	 *            开始行下标
	 * @return rowIndex 下一行下标
	 */
	public static int buildDataRows(HSSFWorkbook workbook,
			List<ColumnAttr> columnAttrList, List<Map<String, Object>> dataList,
			int startRowIndex) {
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFCellStyle dataStyle = ExcelUtils.getDataCellStyle(workbook);
		// 行标
		int rowIndex = startRowIndex;
		// 总列数
		int colSize = columnAttrList.size();
		HSSFCell cell = null;
		String dataKey = null;
		for (Map<String, Object> dataMap : dataList) {
			HSSFRow dataRow = sheet.createRow(rowIndex);
			dataRow.setHeight(ROW_HEIGHT_DEFAULT);
			for (int i = 0; i < colSize; i++) {
				dataKey = columnAttrList.get(i).getDataKey();
				cell = dataRow.createCell(i);
				cell.setCellStyle(dataStyle);
				ExcelUtils.setCellValue(cell, dataMap.get(dataKey),
						columnAttrList.get(i).getDataType(), columnAttrList.get(i).getDefaultValue());
			}
			rowIndex += 1;
		}
		return rowIndex;
	}
	
	/**
	 * 构建合计行
	 * 
	 * @param workbook
	 *            excel表
	 * @param columnAttrList
	 *            列属性列表
	 * @param totalMap
	 *            合计数据
	 * @param rowIndex
	 *            合计行下标
	 */
	public static void buildTotalRow(HSSFWorkbook workbook,
			List<ColumnAttr> columnAttrList, Map<String, Object> totalMap,
			int rowIndex) {
		if (null == totalMap) {
			return;
		}
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFCellStyle titleStyle = ExcelUtils.getTitleCellStyle(workbook);
		HSSFCell cell = null;
		String dataKey = null;
		// 总列数
		int colSize = columnAttrList.size();
		// 合计行
		HSSFRow dataRow = sheet.createRow(rowIndex);
		dataRow.setHeight(TITLE_ROW_HEIGHT_DEFAULT);
		// 处理合并单元格
		boolean isMergedCol = false;
		int firstCol = 0;
		int lastCol = 0;
		for (int i = 0; i < colSize; i++) {
			dataKey = columnAttrList.get(i).getDataKey();
			cell = dataRow.createCell(i);
			cell.setCellStyle(titleStyle);

			isMergedCol = columnAttrList.get(i).isMergedCol();
			if (isMergedCol) {
				lastCol = i;
				ExcelUtils.setCellValue(cell, "合计", null, null);
			} else {
				if (lastCol > firstCol) {
					sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol));
				}
				firstCol = i + 1;
				ExcelUtils.setCellValue(cell, totalMap.get(dataKey),
						columnAttrList.get(i).getDataType(), columnAttrList.get(i).getDefaultValue());
			}
		}
		if (lastCol > firstCol) { // 处理最后一个单元格也是合并的情况
			sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol));
		}
	}
	
	/**
	 * 构建标题行
	 * 
	 * @param workbook
	 *            excel表格
	 * @param columnAttrList
	 *            列属性列表
	 * @param titleList
	 *            标题数据
	 * @return rowSize 总行数
	 */
	public static int buildTitleRows(HSSFWorkbook workbook,
			List<ColumnAttr> columnAttrList, List<List<String>> titleList) {
		int rowSize = 1;
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFCellStyle titleStyle = ExcelUtils.getTitleCellStyle(workbook);
		HSSFCell cell = null;
		// 总列数
		int colSize = columnAttrList.size();
		if (null != titleList && !titleList.isEmpty()) {
			// 多行标题
			rowSize = ExcelUtils.buildMultiTitleRows(sheet, titleStyle, titleList, colSize);
		} else {
			// 单行标题
			HSSFRow titleRow = sheet.createRow(0);
			titleRow.setHeight(TITLE_ROW_HEIGHT_DEFAULT);
			for (int i = 0; i < colSize; i++) {
				cell = titleRow.createCell(i);
				cell.setCellValue(columnAttrList.get(i).getTitle());
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellStyle(titleStyle);
			}
		}
		return rowSize;
	}
    
	/**
	 * 构建标题行（多行，合并相同单元格）
	 * 
	 * @param sheet
	 *            excel表sheet页
	 * @param titleStyle
	 *            标题行样式
	 * @param titleList
	 *            标题行列表
	 * @param colSize
	 *            总列数
	 * @return rowSize 标题行总行数
	 */
	public static int buildMultiTitleRows(HSSFSheet sheet,
			HSSFCellStyle titleStyle, List<List<String>> titleList, int colSize) {
		List<CellRangeAddress> mergedRegionList = Lists.newArrayList(); // 记录已合并的单元格区域
		//标题行总行数
		int rowSize = titleList.size();
		HSSFCell cell = null;
		List<String> colList = null;
		for (int i = 0; i < rowSize; i++) {
			colList = titleList.get(i);
			// 标题行
			HSSFRow titleRow = sheet.createRow(i);
			titleRow.setHeight(TITLE_ROW_HEIGHT_DEFAULT);
			for (int j = 0; j < colSize; j++) {
				cell = titleRow.createCell(j);
				cell.setCellValue(colList.get(j));
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellStyle(titleStyle);
			}
		}
		String curCellValue = null;
		int firstRow = 0;
		int lastRow = 0;
		int firstCol = 0;
		int lastCol = 0;
		// 合并单元格内容相同的行和列
		for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
			colList = titleList.get(rowIndex);
			firstRow = rowIndex;
			lastRow = rowIndex;
			for (int colIndex = 0; colIndex < colSize; colIndex++) {
				firstCol = colIndex;
				lastCol = colIndex;
				curCellValue = colList.get(colIndex);
				lastRow = ExcelUtils.getLastRowIndex(rowSize, rowIndex, colIndex, curCellValue, titleList);
				lastCol = ExcelUtils.getLastColIndex(colSize, rowIndex, colIndex, curCellValue, titleList);
				if (lastRow > firstRow || lastCol > firstCol) {
					if (!ExcelUtils.isExistMergedRegion(mergedRegionList, firstRow, lastRow, firstCol, lastCol)) {
						mergedRegionList.add(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
						sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
					}
				}
			}
		}
		mergedRegionList = null;
		return rowSize;
    }
    
	/**
	 * 获取当前列单元格值相同的最后一行行标
	 * 
	 * @param rowSize
	 *            总行数
	 * @param curRow
	 *            当前单元格行标
	 * @param curCol
	 *            当前单元格列标
	 * @param curCellValue
	 *            当前单元格内容
	 * @param titleList
	 *            标题行列表
	 * @return
	 */
	public static int getLastRowIndex(int rowSize, int curRow, int curCol,
			String curCellValue, List<List<String>> titleList) {
		if (curRow == rowSize - 1) {// 当前行为最后一行
			return curRow;
		}
		int lastRow = curRow;
		String tmpValue = null;
		for (int i = curRow + 1; i < rowSize; i++) {
			tmpValue = titleList.get(i).get(curCol);
			if (tmpValue.equals(curCellValue)) {
				lastRow = i;
			} else {
				break;
			}
		}
		return lastRow;
	}
	
	/**
	 * 获取当前行单元格值相同的最后一列列标
	 * 
	 * @param colSize
	 *            总列数
	 * @param curRow
	 *            当前单元格行标
	 * @param curCol
	 *            当前单元格列标
	 * @param curCellValue
	 *            当前单元格内容
	 * @param titleList
	 *            标题行列表
	 * @return
	 */
	public static int getLastColIndex(int colSize, int curRow, int curCol,
			String curCellValue, List<List<String>> titleList) {
		if (curCol == colSize - 1) {// 当前列为最后一列
			return curCol;
		}
		int lastCol = curCol;
		String tmpValue = null;
		for (int i = curCol + 1; i < colSize; i++) {
			tmpValue = titleList.get(curRow).get(i);
			if (tmpValue.equals(curCellValue)) {
				lastCol = i;
			} else {
				break;
			}
		}
		return lastCol;
	}
	
	/**
	 * 判断合并单元格是否已存在
	 * 
	 * @param mergedRegionList
	 *            已合并的单元格列表
	 * @param firstRow
	 *            合并单元格开始行
	 * @param lastRow
	 *            合并单元格结束行
	 * @param firstCol
	 *            合并单元格开始列
	 * @param lastCol
	 *            合并单元格结束列
	 * @return true 表示已存在
	 */
	public static boolean isExistMergedRegion(List<CellRangeAddress> mergedRegionList, int firstRow,
			int lastRow, int firstCol, int lastCol) {
		boolean isExist = false;
		//int mSize = sheet.getNumMergedRegions();
		int mSize = mergedRegionList.size();
		CellRangeAddress cra = null;
		for (int i = 0; i < mSize; i++) {
			//cra = sheet.getMergedRegion(i);
			cra = mergedRegionList.get(i);
			if (firstRow >= cra.getFirstRow() && lastRow <= cra.getLastRow()
					&& firstCol >= cra.getFirstColumn() && lastCol <= cra.getLastColumn()) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}
	
	/**
	 * 获取页面table多行表头列表（支持跨行跨列合并相同单元格）
	 * 
	 * @param columnAttrList
	 *            列属性列表
	 * @return
	 */
	public static List<List<THeadAttr>> getTheadAttrList(List<ColumnAttr> columnAttrList) {
		List<List<String>> titleList = ExcelUtils.getTitleList(columnAttrList);
		List<CellRangeAddress> mergedRegionList = Lists.newArrayList(); // 记录已合并的单元格区域
		List<List<THeadAttr>> trList = Lists.newArrayList();
		List<THeadAttr> thList = null;
		//标题行总行数
		int rowSize = titleList.size();
		//总列数
		int colSize = titleList.get(0).size();
		List<String> colList = null;
		String curCellValue = null;
		int firstRow = 0;
		int lastRow = 0;
		int firstCol = 0;
		int lastCol = 0;
		// 合并单元格内容相同的行和列
		for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
			thList = Lists.newArrayList();
			trList.add(thList);
			
			colList = titleList.get(rowIndex);
			firstRow = rowIndex;
			lastRow = rowIndex;
			for (int colIndex = 0; colIndex < colSize; colIndex++) {
				firstCol = colIndex;
				lastCol = colIndex;
				curCellValue = colList.get(colIndex);
				lastRow = ExcelUtils.getLastRowIndex(rowSize, rowIndex, colIndex, curCellValue, titleList);
				lastCol = ExcelUtils.getLastColIndex(colSize, rowIndex, colIndex, curCellValue, titleList);
				if (!ExcelUtils.isExistMergedRegion(mergedRegionList, firstRow, lastRow, firstCol, lastCol)) {
					if (lastRow > firstRow || lastCol > firstCol) {
						mergedRegionList.add(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
						int rowspan = lastRow - firstRow + 1;
						int colspan = lastCol - firstCol + 1;
						thList.add(new THeadAttr(curCellValue, rowspan, colspan));
					}else{
						thList.add(new THeadAttr(curCellValue));
					}
				}
			}
		}
		mergedRegionList = null;
		return trList;
    }
	
	/**
	 * 获取多行表头列表（结构：行<列>，即外层list为每行数据，内层list为每列数据）
	 * 
	 * @param columnAttrList
	 *            列属性列表
	 * @return 多行标题列表
	 */
	public static List<List<String>> getTitleList(List<ColumnAttr> columnAttrList) {
		if (null == columnAttrList || columnAttrList.isEmpty()) {
			return null;
		}
		String[] titleArray = columnAttrList.get(0).getTitleArray();
		//判断是否存在多行表头
		if (null == titleArray || titleArray.length == 0) {
			return null;
		}
		List<List<String>> rowList = Lists.newArrayList();
		List<String> colList = null;
		String title = null;
		int rowSize = titleArray.length;
		int colSize = columnAttrList.size();
		for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
			colList = Lists.newArrayList();
			rowList.add(colList);
			for (int colIndex = 0; colIndex < colSize; colIndex++) {
				title = columnAttrList.get(colIndex).getTitleArray()[rowIndex];
				colList.add(title);
			}
		}
		return rowList;
    }
	
}
