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
package com.wl4g.devops.dts.codegen.engine.generator;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.devops.dts.codegen.bean.extra.GenExtraOption;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter;
import com.wl4g.devops.dts.codegen.bean.GenProject.ExtraOptionDefinition;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.io.Closeable;
import java.util.List;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.reflect.ReflectionUtils2.getFieldValues;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * {@link GeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public interface GeneratorProvider extends Runnable, Closeable {

	@Override
	default public void run() {
		try {
			doGenerate();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Do execution generate.
	 *
	 * @throws Exception
	 */
	void doGenerate() throws Exception;

	/**
	 * {@link GenProviderAlias}
	 */
	public static interface GenProviderAlias {

		/**
		 * IAM + SpringCloud + Maven projecs gen provider.
		 */
		public static final String IAM_SPINGCLOUD_MVN = "iamSpringCloudMvnProvider";

		/**
		 * Dubbo + SpringCloud + Maven projecs gen provider.
		 */
		public static final String SPINGDUBBO_MVN = "springDubboMvnProvider";

		/**
		 * Standard golang(mod) projecs gen provider.
		 */
		public static final String GO_GONICWEB = "gonicWebProvider";

		/**
		 * Standard csharp projecs gen provider.
		 */
		public static final String CSHARP_STANDARD = "standardCsharpProvider";

		/**
		 * Standard python projecs gen provider.
		 */
		public static final String PYTHON_STANDARD = "standardPythonProvider";

		/**
		 * VueJS projecs gen provider.
		 */
		public static final String VUEJS = "vuejsProvider";

		/**
		 * AngularJS projecs gen provider.
		 */
		public static final String NGJS = "ngjsProvider";

		/** List of field values of class {@link GenProviderAlias}. */
		public static final String[] VALUES = getFieldValues(GenProviderAlias.class, null, "VALUES").toArray(new String[] {});

	}

	/**
	 * {@link GeneratorProvider} group collection.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static enum GenProviderSet {

		IamWebMvcVueJS(asList(IAM_SPINGCLOUD_MVN, VUEJS), DbTypeConverter.JAVA),

		IamWebMvc(asList(IAM_SPINGCLOUD_MVN), DbTypeConverter.JAVA),

		DubboWebMvcVueJS(asList(SPINGDUBBO_MVN, VUEJS), DbTypeConverter.JAVA),

		GonicWebMVC(asList(GO_GONICWEB), DbTypeConverter.Golang),

		JustVueJS(asList(VUEJS), DbTypeConverter.JS),

		JustNgJS(asList(NGJS), DbTypeConverter.JS);

		/** {@link GenProviderAlias} */
		@NotEmpty
		private final List<String> providers;

		/**
		 * When the generator provider group contains the DAO layer of the
		 * generated database, it is necessary to set the source
		 * {@link DbTypeConverter} type to map the relationship between
		 * DbColumnType and attrType.
		 */
		@Nullable
		private final DbTypeConverter converter;

		private GenProviderSet(@NotEmpty List<String> providers, @Nullable DbTypeConverter converter) {
			this.providers = notEmptyOf(providers, "genProviders");
			this.converter = notNullOf(converter, "typeConverter");
		}

		/**
		 * Gets that providers.
		 * 
		 * @return
		 */
		public final List<String> providers() {
			return providers;
		}

		/**
		 * Gets that {@link DbTypeConverter}.
		 * 
		 * @return
		 */
		public final DbTypeConverter converter() {
			return converter;
		}

		/**
		 * Gets providers by group name.
		 * 
		 * @param providerSet
		 * @return
		 */
		public static List<String> getProviders(@Nullable String providerSet) {
			for (GenProviderSet en : values()) {
				if (equalsIgnoreCase(en.name(), providerSet)) {
					return en.providers();
				}
			}
			return emptyList();
		}

		/**
		 * Parse {@link GenProviderSet} name.
		 * 
		 * @param providerSet
		 * @return
		 */
		public static GenProviderSet safeOf(@NotBlank String providerSet) {
			for (GenProviderSet s : values()) {
				if (equalsIgnoreCase(s.name(), providerSet)) {
					return s;
				}
			}
			return null;
		}

		/**
		 * Parse {@link GenProviderSet} name.
		 * 
		 * @param providerSet
		 * @return
		 */
		public static GenProviderSet of(@NotBlank String providerSet) {
			return notNull(safeOf(providerSet), "No such generator providerSet of '%s'", providerSet);
		}

		/**
		 * Validation {@link GenExtraOption} name and values invalid?
		 * 
		 * @param option
		 */
		public static void validateOption(@NotBlank String providerSet, @NotNull List<GenExtraOption> options) {
			hasTextOf(providerSet, "providerSet");
			notNullOf(options, "options");

			safeList(of(providerSet).providers()).stream().forEach(provider -> {
				List<GenExtraOption> defineOptions = safeList(ExtraOptionDefinition.getOptions(provider));
				// Validate name & value.
				for (GenExtraOption opt : options) {
					if (provider.equals(opt.getProvider())) {
						isTrue(defineOptions.stream().filter(
								dopt -> dopt.getName().equals(opt.getName()) && dopt.getValues().contains(opt.getSelectedValue()))
								.count() > 0, "Invalid option name: '%s' of provider: '%s'", opt.getName(), provider);
					}
				}
			});

		}

	}

}