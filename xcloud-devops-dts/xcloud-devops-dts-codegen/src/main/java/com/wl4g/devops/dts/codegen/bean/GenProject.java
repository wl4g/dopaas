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
package com.wl4g.devops.dts.codegen.bean;

import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.devops.dts.codegen.engine.generator.AbstractGeneratorProvider.RenderingProperty;
import com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenExtraOptionSupport.ConfigOption;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Wither;

import java.util.List;

/**
 * {@link GenProject}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-14
 * @since
 */
@Getter
@Setter
public class GenProject extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	@RenderingProperty
	private String projectName;

	@RenderingProperty
	private Integer datasourceId;

	@RenderingProperty
	private String organType;

	@RenderingProperty
	private String organName;

	@RenderingProperty
	private String providerSet;

	@RenderingProperty
	private String version;

	@RenderingProperty
	private String author;

	@RenderingProperty
	private String since;

	@RenderingProperty
	private String copyright;

	@RenderingProperty
	private List<GenTable> genTables;

	private String extraOptionsJson;

	// --- Temporary fields. ---

	/**
	 * Configured extra options.
	 */
	@RenderingProperty
	private ConfigOptions extraOptions;

	/**
	 * {@link ConfigOptions}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-17
	 * @since
	 */
	@Getter
	@Setter
	@Wither
	public static class ConfigOptions {

		private List<ConfigOption> options;

		public ConfigOptions() {
		}

		public ConfigOptions(List<ConfigOption> options) {
			super();
			this.options = options;
		}

	}

}