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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Customizaing page model.
 * 
 * @auhtor Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年9月7日
 * @since
 */
public class PageModel implements Serializable {
	private static final long serialVersionUID = -7002775417254397561L;

	/**
	 * Page of {@link Page}
	 */
	@JsonIgnore
	private Page<Object> page;

	/**
	 * Page record rows.
	 */
	private List<Object> records = emptyList();

	public Integer getPageNum() {
		return ensurePage().getPageNum();
	}

	public void setPageNum(Integer pageNum) {
		if (nonNull(pageNum)) {
			ensurePage().setPageNum(pageNum);
		}
	}

	public Integer getPageSize() {
		return ensurePage().getPageSize();
	}

	public void setPageSize(Integer pageSize) {
		if (nonNull(pageSize)) {
			ensurePage().setPageSize(pageSize);
		}
	}

	public Long getTotal() {
		return ensurePage().getTotal();
	}

	public void setTotal(Long total) {
		if (nonNull(total)) {
			ensurePage().setTotal(total);
		}
	}

	public List<Object> getRecords() {
		return records;
	}

	@SuppressWarnings("unchecked")
	public <T> void setRecords(List<T> records) {
		if (!isEmpty(records)) {
			this.records = (List<Object>) records;
		}
	}

	/**
	 * Setup page info.
	 *
	 * @param page
	 */
	@SuppressWarnings("unchecked")
	public <T> void page(Page<T> page) {
		this.page = (Page<Object>) page;
	}

	/**
	 * Ensure get page.
	 * 
	 * @return
	 */
	private Page<Object> ensurePage() {
		if (Objects.isNull(page)) {
			page = new Page<>();
		}
		return page;
	}

	@Override
	public String toString() {
		return "PageModel [pageNum=" + getPageNum() + ", pageSize=" + getPageSize() + ", total=" + getTotal() + ", records="
				+ getRecords().size() + "]";
	}

}