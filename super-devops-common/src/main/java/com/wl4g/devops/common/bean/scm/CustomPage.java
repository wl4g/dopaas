package com.wl4g.devops.common.bean.scm;

/**
 * 分页实体类
 * 
 * @date 2018年9月20日
 */
public class CustomPage {
	private Integer pageNum; // 页码
	private Integer pageSize; // 每页显示数量
	private Long total; // 总数

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "CustomPage [" + (pageNum != null ? "pageNum=" + pageNum + ", " : "")
				+ (pageSize != null ? "pageSize=" + pageSize + ", " : "") + (total != null ? "total=" + total : "") + "]";
	}

}
