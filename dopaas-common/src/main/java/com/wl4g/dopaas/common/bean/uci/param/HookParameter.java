/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.common.bean.uci.param;

import javax.validation.constraints.NotBlank;

import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.hasText;

/**
 * Hook pipeline handle parameter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月12日
 * @since
 */
public class HookParameter  extends BaseParameter {
	private static final long serialVersionUID = 9219348162378842689L;

	@NotBlank
	private String projectName;

	@NotBlank
	private String branchName;

	public HookParameter() {
		super();
	}

	public HookParameter(String projectName, String branchName) {
		setProjectName(projectName);
		setBranchName(branchName);
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		hasText(projectName, "Hook pipeline projectName can't is empty.");
		this.projectName = projectName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		hasText(branchName, "Hook pipeline branch can't is empty.");
		this.branchName = branchName;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " => " + toJSONString(this);
	}

}