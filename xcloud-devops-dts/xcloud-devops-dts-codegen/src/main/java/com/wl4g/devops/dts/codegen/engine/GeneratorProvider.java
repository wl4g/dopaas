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
import java.util.LinkedList;
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
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.reflect.ReflectionUtils2.getFieldValues;
import static com.wl4g.devops.dts.codegen.engine.GeneratorProvider.GenProviderAlias.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
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
	 * An extensible configuration item {@link ExtraConfigSupport} which is
	 * supported by itself, If NULL is returned, there is no extensible
	 * configuration item.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	@SuppressWarnings("serial")
	public static abstract class ExtraConfigSupport {

		/** {@link ConfigOption} register. */
		private static final List<ConfigOption> extraOptionsRegistry = unmodifiableList(new LinkedList<ConfigOption>() {
			{
				add(new ConfigOption(SPINGCLOUD_MVN, "codegen.provider.springcloudmvn.build-assets-type", "MvnAssTar",
						"SpringExecJar"));
			}
		});

		private ExtraConfigSupport() {
			extraOptionsRegistry.forEach(o -> o.validate());
		}

		/**
		 * Gets {@link ConfigOption} by provider.
		 * 
		 * @param provider
		 * @return
		 */
		public static List<ConfigOption> getOptions(@Nullable String provider) {
			return extraOptionsRegistry.stream().filter(o -> (isBlank(provider) || provider.equals(o.getName())))
					.collect(toList());
		}

		/**
		 * Check {@link ConfigOption} name and values invalid?
		 * 
		 * @param option
		 */
		public static void checkOption(@NotBlank String provider, @NotBlank String name, @NotBlank String value) {
			hasTextOf(provider, "provider");
			hasTextOf(name, "name");
			hasTextOf(value, "value");

			// Validate option name & values.
			List<ConfigOption> options = safeList(getOptions(provider));
			isTrue(options.stream().filter(o -> o.getName().equals(name)).count() > 0,
					"Invalid option name: '%s' of provider: '%s'", name, provider);
			isTrue(options.stream().filter(o -> o.getValues().contains(value)).count() > 0,
					"Invalid option name: '%s', value: '%s' of provider: '%s'", name, value, provider);
		}

		/**
		 * {@link ConfigOption}
		 *
		 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
		 * @version v1.0 2020-09-16
		 * @since
		 */
		public static final class ConfigOption {

			/** {@link GeneratorProvider} alias. */
			@NotBlank
			private String provider;

			/** Gen provider extra configuration option name. */
			@NotBlank
			private String name;

			/** Gen provider extra configuration option values. */
			@NotEmpty
			private List<String> values;

			public ConfigOption() {
				super();
			}

			public ConfigOption(@NotBlank String provider, @NotBlank String name, @NotEmpty String... values) {
				this(provider, name, asList(notEmptyOf(values, "values")));
			}

			public ConfigOption(@NotBlank String provider, @NotBlank String name, @NotEmpty List<String> values) {
				setProvider(provider);
				setName(name);
				setValues(values);
			}

			/**
			 * Gets extra option of gen provider.
			 * 
			 * @return
			 */
			public String getProvider() {
				return provider;
			}

			/**
			 * Sets extra option of gen provider.
			 * 
			 * @param provider
			 */
			public void setProvider(String provider) {
				this.provider = hasTextOf(provider, "provider");
			}

			/**
			 * Gets extra option name.
			 * 
			 * @return
			 */
			public final String getName() {
				return name;
			}

			/**
			 * Sets extra option name.
			 * 
			 * @param name
			 */
			public void setName(String name) {
				this.name = hasTextOf(name, "name");
			}

			/**
			 * Gets extra option values.
			 * 
			 * @return
			 */
			public final List<String> getValues() {
				return values;
			}

			/**
			 * Sets extra option values.
			 * 
			 * @param values
			 */
			public void setValues(List<String> values) {
				this.values = notEmptyOf(values, "values");
			}

			/**
			 * Validation for itself attributes.
			 * 
			 * @return
			 */
			public final ConfigOption validate() {
				hasTextOf(provider, "provider");
				hasTextOf(name, "name");
				notEmptyOf(values, "values");
				return this;
			}

			/**
			 * Validation for attributes.
			 * 
			 * @param option
			 */
			public static void validate(@NotNull ConfigOption option) {
				notNullOf(option, "option");
				hasTextOf(option.provider, "provider");
				hasTextOf(option.name, "name");
				notEmptyOf(option.values, "values");
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