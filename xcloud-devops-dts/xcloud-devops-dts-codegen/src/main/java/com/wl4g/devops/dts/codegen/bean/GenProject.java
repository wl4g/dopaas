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

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.devops.dts.codegen.bean.extra.GenProjectExtraOption;
import com.wl4g.devops.dts.codegen.utils.RenderPropertyUtils.RenderProperty;
import lombok.Getter;
import lombok.Setter;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.collection.Collections2.isEmptyArray;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.IAM_SPINGCLOUD_MVN;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.NGJS;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.VUEJS;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinition.*;
import static org.springframework.util.CollectionUtils.isEmpty;

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

	private Long datasourceId;

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
	private List<GenProjectExtraOption> extraOptions;

	public GenProject() {
		super();
	}

	@RenderProperty(propertyName = GEN_PROJECT_DESCRIPTION)
	@Override
	public String getRemark() {
		return super.getRemark();
	}

	public GenProject withDatasourceId(Long datasourceId) {
		setDatasourceId(datasourceId);
		return this;
	}

	public GenProject withProjectName(String projectName) {
		setProjectName(projectName);
		return this;
	}

	public GenProject withOrganType(String organType) {
		setOrganType(organType);
		return this;
	}

	public GenProject withOrganName(String organName) {
		setOrganName(organName);
		return this;
	}

	public GenProject withProviderSet(String providerSet) {
		setProviderSet(providerSet);
		return this;
	}

	public GenProject withVersion(String version) {
		setVersion(version);
		return this;
	}

	public GenProject withAuthor(String author) {
		setAuthor(author);
		return this;
	}

	public GenProject withSince(String since) {
		setSince(since);
		return this;
	}

	public GenProject withCopyright(String copyright) {
		setCopyright(copyright);
		return this;
	}

	public GenProject withGenTables(List<GenTable> genTables) {
		setGenTables(genTables);
		return this;
	}

	public GenProject withExtraOptionsJson(String extraOptionsJson) {
		setExtraOptionsJson(extraOptionsJson);
		return this;
	}

	public GenProject withExtraOptions(List<GenProjectExtraOption> extraOptions) {
		setExtraOptions(extraOptions);
		return this;
	}

	/**
	 * {@link GenProject} extensible configuration options definitions.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static enum ExtraOptionDefinition {

		SpringCloudMvnBuildAssetsType(
				new GenProjectExtraOption(IAM_SPINGCLOUD_MVN, "gen.build.assets-type", "MvnAssTar", "SpringExecJar")),

		SpringCloudMvnIamSecurityMode(
				new GenProjectExtraOption(IAM_SPINGCLOUD_MVN, "gen.iam.security-mode", "local", "cluster", "gateway")),

		VueJSCompression(new GenProjectExtraOption(VUEJS, "gen.compression", "true", "false")),

		VueJSBasedOnAdminUi(new GenProjectExtraOption(VUEJS, "gen.basedon.adminui", "true", "false")),

		NgJSCompression(new GenProjectExtraOption(NGJS, "gen.compression", "true", "false"));

		/** Gen provider extra option of {@link GenProjectExtraOption} . */
		@NotNull
		private final GenProjectExtraOption option;

		private ExtraOptionDefinition(@NotNull GenProjectExtraOption option) {
			notNullOf(option, "option");
			this.option = option.validate();
		}

		public final GenProjectExtraOption getOption() {
			return option;
		}

		/**
		 * Gets {@link GenProjectExtraOption} by providers.
		 * 
		 * @param provider
		 * @return
		 */
		public static List<GenProjectExtraOption> getOptions(@Nullable String... providers) {
			final List<String> conditions = new ArrayList<>();
			if (!isEmptyArray(providers)) {
				conditions.addAll(asList(providers));
			}
			return asList(values()).stream()
					.filter(o -> (isEmpty(conditions) || conditions.contains(o.getOption().getProvider())))
					.map(o -> o.getOption()).collect(toList());
		}

	}

}