/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.common.bean.iam.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.lang.StringUtils2;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class BasedModel implements Serializable {
	private static final long serialVersionUID = 151897009229689455L;

	@NotBlank
	private String application;

	public BasedModel() {
		super();
	}

	public BasedModel(String application) {
		super();
		this.setApplication(application);
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		Assert.notNull(application, "'application' must not be null");
		if (!StringUtils2.isEmpty(application) && !"NULL".equalsIgnoreCase(application)) {
			this.application = application;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}