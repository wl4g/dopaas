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
import com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenExtraOptionSupport.ConfigOption;
import com.wl4g.devops.dts.codegen.utils.RenderPropertyUtils.RenderProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Wither
@ToString
@RenderProperty(includeFieldNames = { "remark" })
public class GenProject extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	@RenderProperty
	private String projectName;

	@RenderProperty
	private Integer datasourceId;

	@RenderProperty
	private String organType;

	@RenderProperty
	private String organName;

	@RenderProperty
	private String providerSet;

	@RenderProperty
	private String version;

	@RenderProperty
	private String author;

	@RenderProperty
	private String since;

	@RenderProperty
	private String copyright;

	@RenderProperty
	private List<GenTable> genTables;

	private String extraOptionsJson;

	// --- Temporary fields. ---

	/**
	 * Configured extra options.
	 */
	@RenderProperty
	private ConfigOptions extraOptions;

	public GenProject() {
		super();
	}

	public GenProject(String projectName, Integer datasourceId, String organType, String organName, String providerSet,
			String version, String author, String since, String copyright, List<GenTable> genTables, String extraOptionsJson,
			ConfigOptions extraOptions) {
		super();
		this.projectName = projectName;
		this.datasourceId = datasourceId;
		this.organType = organType;
		this.organName = organName;
		this.providerSet = providerSet;
		this.version = version;
		this.author = author;
		this.since = since;
		this.copyright = copyright;
		this.genTables = genTables;
		this.extraOptionsJson = extraOptionsJson;
		this.extraOptions = extraOptions;
	}

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