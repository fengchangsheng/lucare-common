package com.fcs.common.excel.util;

public class ColumnAttr {

	/**
	 * 列标题(多行表头)
	 */
	private String[] titleArray;
	
	/**
	 * 列标题
	 */
	private String title;

	/**
	 * 数据key
	 */
	private String dataKey;

	/**
	 * 数据类型
	 */
	private String dataType;

	/**
	 * 列宽
	 */
	private Integer columnWidth;
	
	/**
	 * 默认值
	 */
	private String defaultValue;
	
	/**
	 * 汇总行：是否合并单元格，true表示需要合并
	 */
	private boolean mergedCol;

	public ColumnAttr(String title, String dataKey) {
		this.title = title;
		this.dataKey = dataKey;
	}
	
	public ColumnAttr(String title, String dataKey, boolean mergedCol) {
		this.title = title;
		this.dataKey = dataKey;
		this.mergedCol = mergedCol;
	}

	public ColumnAttr(String title, String dataKey, String dataType) {
		this.title = title;
		this.dataKey = dataKey;
		this.dataType = dataType;
	}

	public ColumnAttr(String title, String dataKey, String dataType,
                      Integer columnWidth) {
		this.title = title;
		this.dataKey = dataKey;
		this.dataType = dataType;
		this.columnWidth = columnWidth;
	}
	
	public ColumnAttr(String title, String dataKey, String dataType,
                      String defaultValue, boolean mergedCol) {
		this.title = title;
		this.dataKey = dataKey;
		this.dataType = dataType;
		this.defaultValue = defaultValue;
		this.mergedCol = mergedCol;
	}

	public ColumnAttr(String[] titleArray, String dataKey, boolean mergedCol) {
		this.titleArray = titleArray;
		this.dataKey = dataKey;
		this.mergedCol = mergedCol;
	}
	
	public ColumnAttr(String[] titleArray, String dataKey, String dataType) {
		this.titleArray = titleArray;
		this.dataKey = dataKey;
		this.dataType = dataType;
	}
	
	public ColumnAttr(String[] titleArray, String dataKey, String dataType,
                      String defaultValue, boolean mergedCol) {
		this.titleArray = titleArray;
		this.dataKey = dataKey;
		this.dataType = dataType;
		this.defaultValue = defaultValue;
		this.mergedCol = mergedCol;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDataKey() {
		return dataKey;
	}

	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getColumnWidth() {
		if (null == columnWidth) {
			return ExcelUtils.COLUMN_WIDTH_DEFAULT;
		}
		return columnWidth;
	}

	public void setColumnWidth(Integer columnWidth) {
		this.columnWidth = columnWidth;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isMergedCol() {
		return mergedCol;
	}

	public void setMergedCol(boolean mergedCol) {
		this.mergedCol = mergedCol;
	}

	public String[] getTitleArray() {
		return titleArray;
	}

	public void setTitleArray(String[] titleArray) {
		this.titleArray = titleArray;
	}

}
