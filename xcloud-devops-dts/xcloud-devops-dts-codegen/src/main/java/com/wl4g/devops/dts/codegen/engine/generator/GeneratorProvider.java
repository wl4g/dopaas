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
import com.wl4g.devops.dts.codegen.bean.extra.GenProjectExtraOption;
import com.wl4g.devops.dts.codegen.bean.GenProject.ExtraOptionDefinition;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.CodeLanguage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public interface GeneratorProvider extends Runnable {

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
		 * Standard spring cloud + maven projecs gen provider.
		 */
		public static final String IAM_SPINGCLOUD_MVN = "iamSpringCloudMvnProvider";

		/**
		 * Standard golang(mod) projecs gen provider.
		 */
		public static final String GO_STANDARD = "standardGoProvider";
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

		DaoServiceController(asList(IAM_SPINGCLOUD_MVN), CodeLanguage.JAVA),

		DaoServiceControllerVueJS(asList(IAM_SPINGCLOUD_MVN, VUEJS), CodeLanguage.JAVA),

		// Nothing to do with DAO layer
		JustVueJS(asList(VUEJS), null),

		// Nothing to do with DAO layer
		JustNgJS(asList(NGJS), null);

		/** {@link GenProviderAlias} */
		@NotEmpty
		private final List<String> providers;

		/**
		 * When the generator provider group contains the DAO layer of the
		 * generated database, it is necessary to set the source
		 * {@link CodeLanguage} type to map the relationship between
		 * DbColumnType and attrType.
		 */
		@Nullable
		private final CodeLanguage language;

		private GenProviderSet(@NotEmpty List<String> providers, @Nullable CodeLanguage language) {
			this.providers = notEmptyOf(providers, "providers");
			this.language = language;
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
		 * Gets that {@link CodeLanguage}.
		 * 
		 * @return
		 */
		public final CodeLanguage language() {
			return language;
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
			for (GenProviderSet gpg : values()) {
				if (equalsIgnoreCase(gpg.name(), providerSet)) {
					return gpg;
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
			return notNull(safeOf(providerSet), "Cannot parse gen providerSet of '%s'", providerSet);
		}

		/**
		 * Validation {@link GenProjectExtraOption} name and values invalid?
		 * 
		 * @param option
		 */
		public static void validateOption(@NotBlank String providerSet, @NotNull List<GenProjectExtraOption> options) {
			hasTextOf(providerSet, "providerSet");
			notNullOf(options, "options");

			safeList(of(providerSet).providers()).stream().forEach(provider -> {
				List<GenProjectExtraOption> defineOptions = safeList(ExtraOptionDefinition.getOptions(provider));
				// Validate name & value.
				for (GenProjectExtraOption opt : options) {
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