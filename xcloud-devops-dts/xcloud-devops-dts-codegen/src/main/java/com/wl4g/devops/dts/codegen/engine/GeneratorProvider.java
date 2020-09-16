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
package com.wl4g.devops.dts.codegen.engine;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.components.common.annotation.Nullable;

import static com.wl4g.components.common.collection.Collections2.isEmptyArray;
import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.isTrue;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.reflect.ReflectionUtils2.getFieldValues;
import static com.wl4g.devops.dts.codegen.engine.GeneratorProvider.GenProviderAlias.*;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@link GeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public interface GeneratorProvider extends Runnable {

	/**
	 * {@link GenProviderAlias}
	 */
	public static interface GenProviderAlias {

		/**
		 * Standard spring cloud + maven projecs gen provider.
		 */
		public static final String SPINGCLOUD_MVN = "springCloudMvnProvider";

		/**
		 * Standard golang(mod) projecs gen provider.
		 */
		public static final String GO_STANDARD = "standardGoProvider";

		/**
		 * VueJS projecs gen provider.
		 */
		public static final String VUEJS = "vuejsProvider";

		/**
		 * AngularJS projecs gen provider.
		 */
		public static final String NGJS = "ngjsProvider";

		/** List of field values of class {@link GenProviderAlias}. */
		public static final String[] VALUES = getFieldValues(GenProviderAlias.class, "VALUES").toArray(new String[] {});

	}

	/**
	 * An extensible configuration item {@link ExtraOptionSupport} which is
	 * supported by itself, If NULL is returned, there is no extensible
	 * configuration item.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static enum ExtraOptionSupport {

		SpringCloudMvnOption(SPINGCLOUD_MVN,
				new ExtraOption("codegen.provider.springcloudmvn.build-assets-type", "MvnAssTar", "SpringExecJar"));

		/** {@link GeneratorProvider} alias. */
		private final String provider;

		/** {@link GeneratorProvider} configurable item list. */
		private final List<ExtraOption> options;

		private ExtraOptionSupport(@NotBlank String provider, @NotBlank ExtraOption... options) {
			this.provider = hasTextOf(provider, "provider");
			this.options = asList(notEmptyOf(options, "option"));
		}

		public String getProvider() {
			return provider;
		}

		public final List<String> getOptionNames() {
			return safeList(options).stream().map(o -> o.getOptionName()).collect(toList());
		}

		public final List<String> getOptionValues(@NotBlank String name) {
			hasTextOf(name, "name");
			return safeList(options).stream().filter(o -> o.getOptionName().equals(name))
					.flatMap(o -> o.getOptionValues().stream()).collect(toList());
		}

		/**
		 * Safe parsing of provider.
		 * 
		 * @param provider
		 * @return
		 */
		public static ExtraOptionSupport safeOfProvider(@NotNull String provider) {
			hasTextOf(provider, "provider");
			for (ExtraOptionSupport define : values()) {
				if (isBlank(provider) || define.name().equals(provider)) {
					return define;
				}
			}
			return null;
		}

		/**
		 * Parsing of provider.
		 * 
		 * @param provider
		 * @return
		 */
		public static ExtraOptionSupport ofProvider(@NotNull String provider) {
			ExtraOptionSupport define = safeOfProvider(provider);
			notNull(define, IllegalArgumentException.class, "No such extra config define of provider '%s'", provider);
			return define;
		}

		/**
		 * Check {@link ExtraOptionSupport} {@link ExtraOption} name and values
		 * invalid?
		 * 
		 * @param option
		 */
		public static void checkOption(@NotBlank String provider, @NotNull ExtraOption option) {
			notNullOf(option, "option");
			hasTextOf(option.getOptionName(), "optionName");
			notEmptyOf(option.getOptionValues(), "optionValues");
			// Parse of provider
			ExtraOptionSupport define = ofProvider(provider);
			isTrue(define.getOptionNames().contains(option), "Invalid option '%s' of provider '%s'", option, provider);
		}

		/**
		 * {@link ExtraOption}
		 *
		 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
		 * @version v1.0 2020-09-16
		 * @since
		 */
		public static final class ExtraOption {

			/** Gen provider extra configuration option name. */
			private final String name;

			/** Gen provider extra configuration option values. */
			private final List<String> values;

			public ExtraOption(@NotBlank String name, @NotEmpty String... values) {
				this.name = hasTextOf(name, "name");
				this.values = asList(notEmptyOf(values, "values"));
			}

			public final String getOptionName() {
				return name;
			}

			public final List<String> getOptionValues() {
				return values;
			}

		}

	}

	/**
	 * {@link GenProviderGroup}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static enum GenProviderGroup {

		JustDao(asList(SPINGCLOUD_MVN)),

		DaoServiceController(asList(SPINGCLOUD_MVN, SPINGCLOUD_MVN)),

		DaoServiceControllerVueJS(asList(SPINGCLOUD_MVN, SPINGCLOUD_MVN, VUEJS)),

		JustVueJS(asList(VUEJS)),

		JustNgJS(asList(NGJS));

		/** {@link GenProviderAlias} */
		private final List<String> providers;

		private GenProviderGroup(List<String> providers) {
			this.providers = notEmptyOf(providers, "providers");
		}

		/**
		 * Gets providers by instance.
		 * 
		 * @return
		 */
		public final List<String> providers() {
			return providers;
		}

		/**
		 * Gets providers by components name.
		 * 
		 * @param components
		 * @return
		 */
		public static List<String> getProviders(@Nullable String... components) {
			List<String> providers = new ArrayList<>();
			List<String> conditions = null;
			if (!isEmptyArray(components)) {
				conditions = asList(components);
			}
			for (GenProviderGroup en : values()) {
				if (isNull(conditions) || conditions.contains(en.name())) {
					providers.addAll(en.providers());
					break;
				}
			}
			return providers;
		}

	}

}