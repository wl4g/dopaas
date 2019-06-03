/*
 * Copyright 2015 the original author or authors.
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

import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class GetRelease extends GenericInfo {
	final private static long serialVersionUID = -4016863811283064989L;

	@NotNull
	private ReleaseInstance instance = new ReleaseInstance();

	public GetRelease() {
		super();
	}

	public GetRelease(String application, String profile, ReleaseMeta releaseMeta, ReleaseInstance instance) {
		super(application, profile, releaseMeta);
		this.setInstance(instance);
	}

	public GetRelease(String application, String profile, ReleaseInstance instance) {
		super(application, profile, null);
		this.setInstance(instance);
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
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	@Override
	public void validation(boolean validVersion, boolean validReleaseId) {
		super.validation(validVersion, validReleaseId);
		Assert.notNull(getInstance(), "`releaseInstance` is not allowed to be null.");
		getInstance().validation();
	}

}