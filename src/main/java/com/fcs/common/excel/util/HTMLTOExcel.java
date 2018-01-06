package com.fcs.common.excel.util;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * HTML格式转换为Excel
 * 复杂表头构造，根据页面样式转换成动态Excel表头，html文本可直接复制到模板中
 * Created by fengcs on 2018/1/4.
 */
public class HTMLTOExcel {

    /**
     * 动态构建Excel
     *
     * @param tableStr        表头字符串 "<table><thead><tr></tr></thead></table>"
     * @param columnAttrList  每列的属性名称、格式和默认值集合
     * @param dataList        填充的报表数据集合
     * @param totalMap        总计
     * @param excelPath       导出路径
     * @throws IOException
     */
    public static void toExcelDynamic(String tableStr, List<ColumnAttr> columnAttrList, List<Map<String, Object>> dataList,
            Map<String, Object> totalMap, String excelPath) throws IOException {
        Document doc = getDocFromStr(tableStr);
        // 标题和样式暂且不用
//        String title = doc.title();
        ///得到样式，以后可以根据正则表达式解析css，暂且没有找到cssparse
//        Elements style = doc.getElementsByTag("style");
        Elements tables = doc.getElementsByTag("TABLE");
        if (tables.size() == 0) {
            return;
        }
        Element table = tables.get(0);
        //得到所有行
        Elements trs = table.getElementsByTag("tr");
        ///得到列宽集合
        Elements colgroups = table.getElementsByTag("colgroup");

        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(excelPath));
            WritableSheet sheet = book.createSheet("导出数据", 0);
            setColWidth(colgroups, sheet);
            mergeColRow(trs, sheet);
            // 填充数据
            applyDataOwn(columnAttrList, dataList, trs.size(), totalMap, sheet);
            book.write();
            book.close();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public static Document getDocFromStr(String str){
        Document doc = Jsoup.parse(str);
        return doc;
    }

    /**
     * 根据trs行数和sheet画出整个表格
     */
    private static void mergeColRow(Elements trs, WritableSheet sheet) throws WriteException {
        int[][] rowhb = new int[300][50];
        for (int i = 0; i < trs.size(); i++) {
            Element tr = trs.get(i);
            Elements tds = tr.getElementsByTag("th");

            int realColNum = 0;
            for (int j = 0; j < tds.size(); j++) {
                Element td = tds.get(j);
                if (rowhb[i][realColNum] != 0) {
                    realColNum = getRealColNum(rowhb, i, realColNum);
                }
                int rowspan = 1;
                int colspan = 1;
                if (td.attr("rowspan") != "") {
                    rowspan = Integer.parseInt(td.attr("rowspan"));
                }
                if (td.attr("colspan") != "") {
                    colspan = Integer.parseInt(td.attr("colspan"));
                }
                String text = td.text();
                drawMegerCell(rowspan, colspan, sheet, realColNum, i, text, rowhb);
                realColNum = realColNum + colspan;
            }

        }
    }

    /**
     * 根据样式画出单元格，并且根据rowpan和colspan合并单元格
     */
    private static void drawMegerCell(int rowspan, int colspan, WritableSheet sheet, int realColNum,
            int realRowNum, String text, int[][] rowhb) throws WriteException {
        WritableFont writableFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
        WritableCellFormat cellFormat = getCellFormat(writableFont);
        for (int i = 0; i < rowspan; i++) {
            for (int j = 0; j < colspan; j++) {
                if (i != 0 || j != 0) {
                    text = "";
                }
                Label label = new Label(realColNum + j, realRowNum + i, text);
                label.setCellFormat(cellFormat);
                sheet.addCell(label);
                rowhb[realRowNum + i][realColNum + j] = 1;
            }
        }
        sheet.mergeCells(realColNum, realRowNum, realColNum + colspan - 1, realRowNum + rowspan - 1);
    }

    /**
     * 填充自己的数据（中间部分）
     * @param columnAttrList
     * @param dataList
     * @param startRowIndex
     * @param sheet
     * @throws WriteException
     */
    private static void applyDataOwn(List<ColumnAttr> columnAttrList, List<Map<String, Object>> dataList,
            int startRowIndex, Map<String, Object> totalMap, WritableSheet sheet) throws WriteException {
        String dataKey;
        String dataVal;
        Object dataObj;
        int rowIndex = startRowIndex;
        int colSize = columnAttrList.size();
        WritableFont writableFont = new WritableFont(WritableFont.TIMES, 11);
        WritableCellFormat cellFormat = getCellFormat(writableFont);
        for (Map<String, Object> dataMap : dataList) {
            for (int i = 0; i < colSize; i++) {
                dataKey = columnAttrList.get(i).getDataKey();
                dataObj = dataMap.get(dataKey);
                dataVal = handleDataValue(dataObj, columnAttrList.get(i).getDefaultValue(), columnAttrList.get(i).getDataType());
                Label label = new Label(i, rowIndex, dataVal);
                label.setCellFormat(cellFormat);
                sheet.addCell(label);
            }
            rowIndex++;
        }
        if (totalMap != null) {
            buildTotalRow(columnAttrList, rowIndex, totalMap, sheet);
        }
    }

    /**
     * 处理数据的格式 分转元/小数转百分比
     * @param dataObj     数据值对象
     * @param defaultVal  默认值
     * @param dataType    "FEE_CENT" - 分转元  "PERCENTAGE" - 小数转百分比
     * @return  处理后的数据值
     */
    private static String handleDataValue(Object dataObj, String defaultVal, String dataType) {
        String cellValue = "";
        String valueStr = dataObj == null ? defaultVal : dataObj.toString();
        if (null == valueStr) {
            return cellValue;
        }
        if ("FEE_CENT".equals(dataType)) {// 结果值分转元
            cellValue = ExcelFormat.converCentToYuan(new BigDecimal(valueStr)).toPlainString();
        } else if ("PERCENTAGE".equals(dataType)) {// 结果值小数转百分比
            cellValue = ExcelFormat.getPercent(Double.valueOf(valueStr));
        } else {
            cellValue = valueStr;
        }
        return cellValue;
    }

    /**
     * 构建总计行
     */
    private static void buildTotalRow(List<ColumnAttr> columnAttrList, int startRowIndex,
            Map<String, Object> totalMap,  WritableSheet sheet) throws WriteException {
        String dataKey;
        String dataVal;
        Object dataObj;
        int mergeEnd = 0;
        int colSize = columnAttrList.size();
        // 设置单元格内容，字号12，加粗
        WritableFont writableFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
        WritableCellFormat cellFormat = getCellFormat(writableFont);
        for (int i = 0; i < colSize; i++) {
            dataKey = columnAttrList.get(i).getDataKey();
            boolean megreStr = columnAttrList.get(i).isMergedCol();
            if (megreStr) {
                dataVal = "总计";
                mergeEnd++;
            } else {
                dataObj = totalMap.get(dataKey);
                dataVal = handleDataValue(dataObj, columnAttrList.get(i).getDefaultValue(), columnAttrList.get(i).getDataType());
            }
            Label label = new Label(i, startRowIndex, dataVal);
            label.setCellFormat(cellFormat);
            sheet.addCell(label);
        }
        if (mergeEnd > 1) {
            sheet.mergeCells(0, startRowIndex, mergeEnd-1, startRowIndex);
        }
    }

    /**
     * 获取WritableCellFormat
     * @param writableFont  自定义格式
     */
    private static WritableCellFormat getCellFormat(WritableFont writableFont) throws WriteException {
        WritableCellFormat cellFormat = new WritableCellFormat(writableFont, NumberFormats.TEXT);
        cellFormat.setAlignment(jxl.format.Alignment.CENTRE);//把水平对齐方式指定为居中
        cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//把垂直对齐方式指定为居
        cellFormat.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);//给单元格加边
        return cellFormat;
    }

    public static int getRealColNum(int[][] rowhb, int i, int realColNum) {
        while (rowhb[i][realColNum] != 0) {
            realColNum++;
        }
        return realColNum;
    }

    /**
     * 根据colgroups设置表格的列宽
     *
     * @param colgroups
     * @param sheet
     */
    public static void setColWidth(Elements colgroups, WritableSheet sheet) {
        if (colgroups.size() > 0) {
            Element colgroup = colgroups.get(0);
            Elements cols = colgroup.getElementsByTag("col");
            for (int i = 0; i < cols.size(); i++) {
                Element col = cols.get(i);
                String strwd = col.attr("width");
                if (col.attr("width") != "") {
                    int wd = Integer.parseInt(strwd);
                    sheet.setColumnView(i, wd / 8);
                }
            }
        }
    }

}
