package com.fcs.common.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page<T> implements java.io.Serializable {

	private static final long serialVersionUID = -5638486944555354183L;

	List<T> list; // 返回数据列表

	int pageIndex; // 当前页数

	int pageSize; // 每页的数目

	long totalSize; // 总条数

	public Page() {
		super();
	}

	public Page(List<T> list, int pageIndex, int pageSize, long totalSize) {
		this.list = list;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.totalSize = totalSize;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	
	public static <T> Map<String, Object> createResultMap(Page<T> pageObj) {
		return createResultMap(pageObj, "list");
	}

	public static <T> Map<String, Object> createResultMap(Page<T> pageObj, String listName) {
		Map<String, Object> result = new HashMap<>();
		if (pageObj == null)
			return result;

		Map<String, Integer> pageMap = new HashMap<>();
		pageMap.put("count", Integer.parseInt("" + pageObj.getTotalSize()));
		pageMap.put("page", pageObj.getPageIndex());
		pageMap.put("size", pageObj.getPageSize());

		result.put(listName, pageObj.getList());
		result.put("pages", pageMap);
		return result;
	}
}
