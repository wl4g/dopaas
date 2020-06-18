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
package com.wl4g.devops.iam.common.web.model;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

import com.google.common.annotations.Beta;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Sessions query model.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-31
 * @since
 */
@Beta
public class SessionQueryModel implements Serializable {
	private static final long serialVersionUID = 5766036036946339544L;

	/**
	 * Scan search principal name.
	 */
	private String principal;

	/**
	 * Scan cursor.
	 */
	@NotBlank(message = "Invalid argument cursor.(e.g. cursor=0@0)")
	private String cursor = "0@0";

	/**
	 * Page size.
	 */
	private int limit = 200;

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		if (!isBlank(cursor)) {
			this.cursor = cursor;
		}
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		if (limit > 0) {
			this.limit = limit;
		}
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}