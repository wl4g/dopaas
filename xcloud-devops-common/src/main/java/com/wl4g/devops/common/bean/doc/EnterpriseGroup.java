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
package com.wl4g.devops.common.bean.doc;

import com.wl4g.components.core.bean.BaseBean;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * {@link EnterpriseGroup}
 *
 * @author root
 * @version master
 * @Date 2020-11-25
 * @since v1.0
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EnterpriseGroup extends BaseBean {
	private static final long serialVersionUID = 169054610276618400L;

	/**
	 * 
	 */
	@NotNull
	private String name;

	/**
	 * 
	 */
	private String remark;

	/**
	 * 组织编码
	 */
	private String organizationCode;

	// expand

	private List<EnterpriseProject> enterpriseProjectList;

	public EnterpriseGroup() {
	}

	public EnterpriseGroup withName(String name) {
		setName(name);
		return this;
	}

	public EnterpriseGroup withRemark(String remark) {
		setRemark(remark);
		return this;
	}

	public EnterpriseGroup withOrganizationCode(String organizationCode) {
		setOrganizationCode(organizationCode);
		return this;
	}
}