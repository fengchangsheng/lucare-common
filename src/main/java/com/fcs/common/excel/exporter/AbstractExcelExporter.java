package com.fcs.common.excel.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fcs.common.util.CommonUtils;
import com.fcs.common.excel.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;


/**
 * @author yanxin
 * @update oliverchen
 * @param <T>
 */
public abstract class AbstractExcelExporter<T> {
	
	/**
	 * 每个sheet导出的最大行数
	 */
	public static int export_excel_sheet_size = 60000;

	protected abstract String[] getExcelHeaders();
	protected abstract String[] getExcelTitles();

	public ByteArrayInputStream export(List<T> list, String sheetName) {
		return export(list, sheetName, 1);
	}
	
	/**
	 * Create a library of cell styles
	 */
	private Map<String, CellStyle> initStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 18);
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(titleFont);
		styles.put("title", style);
		
		Font monthFont = wb.createFont();
		monthFont.setFontName("宋体");
		monthFont.setFontHeightInPoints((short) 11);
		monthFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(monthFont);
		style.setWrapText(true);
		styles.put("header", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		//表格
		/*		
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());*/
		styles.put("cell", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styles.put("formula", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styles.put("formula_2", style);

		return styles;
	}
	
	/**
	 * 
	 * @param list 查询结果集，map 的key 对应 excel title列标题
	 * @param excelName 生成的excel名字
	 * @param flag 0:有表头  1:无表头
	 */
	public ByteArrayInputStream export(List<T> list, String excelName, int flag) {
		Workbook workBook = null;
		ByteArrayOutputStream os = null;
		ByteArrayInputStream is = null;
		try {
			os = new ByteArrayOutputStream();			
			workBook = new HSSFWorkbook();
			Map<String, CellStyle> styles = initStyles(workBook);
			int count = list == null ? 0 : list.size(), maxPage = 0;
			if (count % export_excel_sheet_size == 0) { // 刚好整数就不要加1
				maxPage = count / export_excel_sheet_size;
			} else {
				if (count < export_excel_sheet_size) {
					maxPage = 1;
				} else {
					maxPage = count / export_excel_sheet_size + 1;
				}
			}
			if (count <= 0) {
				maxPage = 1;//无数据也显示表头
			}
			for (int i = 0; i < maxPage; i++) {
				int fromIndex = i * export_excel_sheet_size;
				int toIndex = (i + 1) * export_excel_sheet_size;
				if (i == (maxPage - 1)) {
					toIndex = toIndex > count ? count : toIndex;
				}
				List<T> tempList = list == null ? null : list.subList(fromIndex, toIndex);
				Sheet sheet = workBook.createSheet("sheet" + i);
				PrintSetup printSetup = sheet.getPrintSetup();
				printSetup.setLandscape(true);
				sheet.setFitToPage(true);
				sheet.setHorizontallyCenter(true);
				
				//获取Excel的头部分
				String[] titles = getExcelTitles();
				String[] headers = getExcelHeaders();
				int startRow = 1;
			
				if (flag == 0) {
					// 文件名备注 第一行
					Row titleRow = sheet.createRow(0);
					titleRow.setHeightInPoints(45);
					Cell titleCell = titleRow.createCell(0);
					titleCell.setCellValue(excelName);
					titleCell.setCellStyle(styles.get("title"));
					sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$L$1"));
					startRow = 2;
				}
				
				// 第二行 表头
				Row headerRow = sheet.createRow(startRow-1);
				headerRow.setHeightInPoints(40);
				for (int j = 0; j < titles.length; j++) {
					Cell headerCell = headerRow.createCell(j);
					headerCell.setCellValue(titles[j]);
					headerCell.setCellStyle(styles.get("header"));
					sheet.setColumnWidth(j, 28 * 256); // 需要配置宽度，后期修改为可配置
				}
				int columnCount = headers.length;
				if (!CommonUtils.isEmpty(tempList)) {
					for (T entity : tempList) {
						Row row = sheet.createRow(startRow++);
						Map<String, Object> map = resolveEntity(entity);
						for (int column = 0; column < columnCount; column++) {
							Object cellValue = map.get(headers[column]);
							if (cellValue == null) {
								cellValue = "";
							}
							Cell cell = row.createCell(column);
							cell.setCellStyle(styles.get("cell"));
							if (cellValue != null) {
								String cellStrValue = cellValue + "";
								if (cellStrValue.length() > 11) { //多于11位，避免科学计数法，一律赋值string
									cell.setCellValue(cellStrValue);
								} else {
									if (ExcelUtil.isLong(cellStrValue)) {
										cell.setCellValue(CommonUtils.parseLong(cellStrValue));
									} 
									else if (ExcelUtil.isDouble(cellValue + "")) {
										cell.setCellValue(CommonUtils.parseDouble(cellStrValue));
									} else {
										cell.setCellValue(cellStrValue);
									}
								}
							}
						}
					}
				}
			}
			workBook.write(os);
			is = new ByteArrayInputStream(os.toByteArray());
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	protected abstract Map<String, Object> resolveEntity(T entity);
}
