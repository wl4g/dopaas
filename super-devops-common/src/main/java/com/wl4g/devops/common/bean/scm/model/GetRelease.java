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
package com.wl4g.devops.common.bean.scm.model;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

import java.util.List;

public class GetRelease extends GenericInfo {
	final private static long serialVersionUID = -4016863811283064989L;

	@NotNull
	private ReleaseInstance instance;

	public GetRelease() {
		super();
	}

	public GetRelease(String cluster, List<String> namespaces, ReleaseMeta meta, ReleaseInstance instance) {
		super(cluster, namespaces, meta);
		setInstance(instance);
	}

	public ReleaseInstance getInstance() {
		return instance;
	}

	public void setInstance(ReleaseInstance instance) {
		if (instance != null) {
			this.instance = instance;
		}
	}

	@Override
	public void validation(boolean versionValidate, boolean releaseValidate) {
		super.validation(versionValidate, releaseValidate);
		Assert.notNull(getInstance(), "Invalid empty release instance");
		getInstance().validation();
	}

}