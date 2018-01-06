package com.fcs.common.excel.vo;

public class THeadAttr {

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 合并行数（对应th的rowspan属性）
	 */
	private int rowspan;

	/**
	 * 合并列数（对应th的colspan属性）
	 */
	private int colspan;

	/**
	 * 最小宽度
	 */
	private int minWidth;

	public THeadAttr(String title) {
		this.title = title;
		this.rowspan = 1;
		this.colspan = 1;
		this.minWidth = 80;
	}

	public THeadAttr(String title, int rowspan, int colspan) {
		this.title = title;
		this.rowspan = rowspan;
		this.colspan = colspan;
		this.minWidth = 80;
	}

	public THeadAttr(String title, int minWidth) {
		this.title = title;
		this.rowspan = 1;
		this.colspan = 1;
		this.minWidth = minWidth;
	}

	public THeadAttr(String title, int rowspan, int colspan, int minWidth) {
		this.title = title;
		this.rowspan = rowspan;
		this.colspan = colspan;
		this.minWidth = minWidth;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("THeadAttr{");
		sb.append("title=").append(title);
		sb.append(", rowspan=").append(rowspan);
		sb.append(", colspan=").append(colspan);
		sb.append(", minWidth=").append(minWidth);
		sb.append('}');
		return sb.toString();
	}
}
