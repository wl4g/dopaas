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

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.wl4g.components.common.bean.ConfigOption;

/**
 * {@link GenTableExtraOption}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public class GenTableExtraOption extends ConfigOption {

	public GenTableExtraOption() {
		super();
	}

	public GenTableExtraOption(@NotBlank String name, @NotEmpty String... values) {
		super(name, values);
	}

	public GenTableExtraOption(@NotBlank String name, @NotEmpty List<String> values) {
		super(name, values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public GenTableExtraOption validate() {
		return super.validate();
	}

}