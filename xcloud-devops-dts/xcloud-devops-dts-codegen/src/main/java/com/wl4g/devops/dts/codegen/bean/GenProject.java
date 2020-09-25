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

import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.*;

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
@RenderProperty(includeFieldNames = {GEN_PROJECT_DESCRIPTION})
public class GenProject extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer datasourceId;

	@RenderProperty(propertyName = GEN_PROJECT_NAME)
	private String projectName;

	@RenderProperty(propertyName = GEN_PROJECT_ORGAN_TYPE)
	private String organType;

	@RenderProperty(propertyName = GEN_PROJECT_ORGAN_NAME)
	private String organName;

	@RenderProperty(propertyName = GEN_PROJECT_PROVIDER_SET)
	private String providerSet;

	@RenderProperty(propertyName = GEN_PROJECT_VERSION)
	private String version;

	@RenderProperty(propertyName = GEN_PROJECT_AUTHOR)
	private String author;

	@RenderProperty(propertyName = GEN_PROJECT_SINCE)
	private String since;

	@RenderProperty(propertyName = GEN_PROJECT_COPYRIGHT)
	private String copyright;

	@RenderProperty(propertyName = GEN_PROJECT_GEN_TABLES, describeForObjField = "No")
	private List<GenTable> genTables;

	private String extraOptionsJson;

	// --- Temporary fields. ---

	/**
	 * Configured extra options.
	 */
	@RenderProperty(propertyName = GEN_PROJECT_EXTRA_OPTIONS, describeForObjField = "No")
	private List<ConfigOption> extraOptions;

	public GenProject() {
		super();
	}

	public GenProject(Integer datasourceId, String projectName, String organType, String organName, String providerSet,
			String version, String author, String since, String copyright, List<GenTable> genTables, String extraOptionsJson,
			List<ConfigOption> extraOptions) {
		super();
		this.datasourceId = datasourceId;
		this.projectName = projectName;
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

}