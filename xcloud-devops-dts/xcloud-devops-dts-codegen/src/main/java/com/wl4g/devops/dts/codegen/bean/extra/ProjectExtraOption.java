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
package com.wl4g.devops.dts.codegen.bean.extra;

import com.wl4g.components.common.bean.ConfigOption;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.wl4g.components.common.lang.Assert2.*;
import static java.util.Arrays.asList;

/**
 * {@link ProjectExtraOption}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public class ProjectExtraOption extends ConfigOption {

	/** {@link GeneratorProvider} alias. */
	@NotBlank
	private String provider;

	public ProjectExtraOption() {
		super();
	}

	public ProjectExtraOption(@NotBlank String provider, @NotBlank String name, @NotEmpty String... values) {
		this(provider, name, asList(notEmptyOf(values, "values")));
	}

	public ProjectExtraOption(@NotBlank String provider, @NotBlank String name, @NotEmpty List<String> values) {
		setProvider(provider);
		setName(name);
		setValues(values);
	}

	/**
	 * Gets extra option of gen provider.
	 * 
	 * @return
	 */
	@NotBlank
	public String getProvider() {
		return provider;
	}

	/**
	 * Sets extra option of gen provider.
	 * 
	 * @param provider
	 */
	public void setProvider(@NotBlank String provider) {
		this.provider = hasTextOf(provider, "provider");
	}

	/**
	 * Sets extra option of gen provider.
	 * 
	 * @param provider
	 */
	public ProjectExtraOption withProvider(@NotBlank String provider) {
		setProvider(provider);
		return this;
	}

	/**
	 * Validation for itself attributes.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final ProjectExtraOption validate() {
		hasTextOf(getProvider(), "provider");
		super.validate();
		return this;
	}

}