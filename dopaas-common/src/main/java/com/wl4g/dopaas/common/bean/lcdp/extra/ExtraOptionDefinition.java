/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.common.bean.lcdp.extra;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import javax.annotation.Nullable;
import com.wl4g.infra.common.bean.ConfigOption;
import com.wl4g.dopaas.common.bean.lcdp.GenProject;

import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.IAM_SPINGCLOUD_MVN;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.IAM_VUEJS;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.NGJS;
import static com.wl4g.infra.common.collection.CollectionUtils2.isEmpty;
import static com.wl4g.infra.common.collection.CollectionUtils2.isEmptyArray;
import static com.wl4g.infra.common.lang.Assert2.*;

/**
 * {@link GenProject} extensible configuration options definitions.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public enum ExtraOptionDefinition {

	SpringCloudMvnBuildAssetsType(new GenExtraOption(IAM_SPINGCLOUD_MVN, "build.asset-type", "MvnAssTar", "SpringExecJar")),

	SpringCloudMvnIamSecurityMode(new GenExtraOption(IAM_SPINGCLOUD_MVN, "iam.mode", "local", "cluster", "gateway")),

	SpringCloudSwagger(new GenExtraOption(IAM_SPINGCLOUD_MVN, "swagger.ui", "none", "officialOas", "bootstrapSwagger2")),

	VueJSCompression(new GenExtraOption(IAM_VUEJS, "compression", "true", "false")),

	VueJSBasedOnAdminUi(new GenExtraOption(IAM_VUEJS, "basedon.adminui", "true", "false")),

	NgJSCompression(new GenExtraOption(NGJS, "compression", "true", "false"));

	/** Gen provider extra option of {@link GenExtraOption} . */
	@NotNull
	private final GenExtraOption option;

	private ExtraOptionDefinition(@NotNull GenExtraOption option) {
		notNullOf(option, "option");
		this.option = option.validate();
	}

	public final GenExtraOption getOption() {
		return option;
	}

	/**
	 * Gets {@link GenExtraOption} by providers.
	 * 
	 * @param provider
	 * @return
	 */
	public static List<GenExtraOption> getOptions(@Nullable String... providers) {
		final List<String> conditions = new ArrayList<>();
		if (!isEmptyArray(providers)) {
			conditions.addAll(asList(providers));
		}
		return asList(values()).stream().filter(o -> (isEmpty(conditions) || conditions.contains(o.getOption().getProvider())))
				.map(o -> o.getOption()).collect(toList());
	}

	/**
	 * Gen project extra options. see: {@link GenProject}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static class GenExtraOption extends ConfigOption {

		/** {@link GeneratorProvider} alias. */
		@NotBlank
		private String provider;

		public GenExtraOption() {
			super();
		}

		public GenExtraOption(@NotBlank String provider, @NotBlank String name, @NotEmpty String... values) {
			this(provider, name, asList(notEmptyOf(values, "values")));
		}

		public GenExtraOption(@NotBlank String provider, @NotBlank String name, @NotEmpty List<String> values) {
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
		public GenExtraOption withProvider(@NotBlank String provider) {
			setProvider(provider);
			return this;
		}

		/**
		 * Validation for itself attributes.
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final GenExtraOption validate() {
			hasTextOf(getProvider(), "provider");
			super.validate();
			return this;
		}

	}

}