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
package com.wl4g.devops.common.bean;

import static java.util.Objects.nonNull;

import java.io.Serializable;

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
	private Long total;

	public PageModel() {
		super();
	}

	public PageModel(Integer pageNum, Integer pageSize, Long total) {
		setPageNum(pageNum);
		setPageSize(pageSize);
		setTotal(total);
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

	@Override
	public String toString() {
		return "CustomPage [pageNum=" + pageNum + ", pageSize=" + pageSize + ", total=" + total + "]";
	}

}