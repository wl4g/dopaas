/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.page;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.Page;

/**
 * Customizaing page model.
 * 
 * @auhtor Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年9月7日
 * @since
 */
public class PageModel implements Serializable {
	private static final long serialVersionUID = -7002775417254397561L;

	/** Page index number. */
	private Integer pageNum = 1;

	/** Page records size. */
	private Integer pageSize = 10;

	/** Total count. */
	private Long total = 0L;

	/**
	 * Page record rows.
	 */
	private List<Object> records = emptyList();

	public PageModel() {
		super();
	}

	public PageModel(Integer pageNum, Integer pageSize, Long total) {
		setPageNum(pageNum);
		setPageSize(pageSize);
		setTotal(total);
	}

	public PageModel(Page<Object> records) {
		setRecords(records);
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		if (nonNull(pageNum)) {
			this.pageNum = pageNum;
		}
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if (nonNull(pageSize)) {
			this.pageSize = pageSize;
		}
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		if (nonNull(total)) {
			this.total = total;
		}
	}

	public List<Object> getRecords() {
		return records;
	}

	/**
	 * Setup page records data.
	 * 
	 * @param records
	 */
	@SuppressWarnings("unchecked")
	public <T> void setRecords(List<T> records) {
		if (!isEmpty(records)) {
			this.records = (List<Object>) records;
			// if (records instanceof Page) {
			// Page<Object> page = (Page<Object>) records;
			// setPageNum(page.getPageNum());
			// setPageSize(page.getPageSize());
			// setTotal(page.getTotal());
			// }
		}
	}

	/**
	 * Setup page information.
	 * 
	 * @param page
	 */
	public <T> void page(Page<T> page) {
		setPageNum(page.getPageNum());
		setPageSize(page.getPageSize());
		setTotal(page.getTotal());
	}

	@Override
	public String toString() {
		return "PageModel [pageNum=" + getPageNum() + ", pageSize=" + getPageSize() + ", total=" + getTotal() + ", records="
				+ getRecords().size() + "]";
	}

}