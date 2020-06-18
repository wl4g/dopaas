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

import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PreRelease extends GenericInfo {
	final private static long serialVersionUID = -4016863811283064989L;

	@NotNull
	@NotEmpty
	private List<ReleaseInstance> instances = new ArrayList<>();

	public PreRelease() {
		super();
	}

	public PreRelease(String cluster, List<String> namespace, ReleaseMeta meta) {
		super(cluster, namespace, meta);
	}

	public List<ReleaseInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<ReleaseInstance> instances) {
		if (instances != null) {
			this.instances = instances;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	@Override
	public void validation(boolean validVersion, boolean validReleaseId) {
		super.validation(validVersion, validReleaseId);
		Assert.notEmpty(getInstances(), "`releaseInstances` is not allowed to be null.");
		getInstances().stream().forEach((i) -> {
			Assert.notNull(i, "`releaseInstances` is not allowed to be empty.");
			i.validation();
		});
	}

}