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
import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.collection.multimap.LinkedMultiValueMap;
import com.wl4g.components.common.collection.multimap.MultiValueMap;

import static com.wl4g.components.common.collection.Collections2.isEmptyArray;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.reflect.ReflectionUtils2.getFieldValues;
import static com.wl4g.devops.dts.codegen.engine.GeneratorProvider.GenProviderAlias.*;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
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
	 * An extensible configuration item {@link ExtraOptions} which is supported
	 * by itself, If NULL is returned, there is no extensible configuration
	 * item.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	@SuppressWarnings("serial")
	public static enum ExtraOptions {

		SPINGCLOUD_MVN_ITEMS(SPINGCLOUD_MVN, new LinkedMultiValueMap<String, String>() {
			{
				put("codegen.provider.springcloudmvn.build-assets-type", new ArrayList<String>() {
					{
						add("MvnAssTar");
						add("SpringExecJar");
					}
				});
			}
		});

		/** {@link GeneratorProvider} alias. */
		private final String provider;

		/** {@link GeneratorProvider} Configurable item list. */
		private final MultiValueMap<String, String> options;

		private ExtraOptions(@NotBlank String provider, @NotBlank MultiValueMap<String, String> options) {
			this.provider = hasTextOf(provider, "provider");
			this.options = notEmptyOf(options, "options");
		}

		public String getProvider() {
			return provider;
		}

		public final MultiValueMap<String, String> getOptions() {
			return options;
		}

		public final Set<String> getOptionKeys() {
			return options.keySet();
		}

		public final List<String> getOptionValues(@Nullable String key) {
			List<String> optionValues = new ArrayList<>();
			for (List<String> vals : options.values()) {
				if (isBlank(key) || vals.contains(key)) {
					optionValues.addAll(vals);
					break;
				}
			}
			return optionValues;
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

		JUST_DAO(asList(SPINGCLOUD_MVN)),

		DAO_SERVICE_CONTRELLER(asList(SPINGCLOUD_MVN, SPINGCLOUD_MVN)),

		DAO_SERVICE_CONTRELLER_VUE(asList(SPINGCLOUD_MVN, SPINGCLOUD_MVN, VUEJS)),

		JUST_VUEJS(asList(VUEJS)),

		JUST_AGJS(asList(NGJS));

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