package com.fcs.common.excel.vo;

public class TabInfoVo {

	/**
	 * 序号
	 */
	private Integer id;

	/**
	 * 标题名称
	 */
	private String name;

	/**
	 * url
	 */
	private String url;

	public TabInfoVo(Integer id, String name, String url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TabInfoVo{");
		sb.append("id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", url=").append(url);
		sb.append('}');
		return sb.toString();
	}
}
