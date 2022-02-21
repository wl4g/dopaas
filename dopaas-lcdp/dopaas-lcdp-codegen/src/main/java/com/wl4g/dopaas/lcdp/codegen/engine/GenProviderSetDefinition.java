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
package com.wl4g.dopaas.lcdp.codegen.engine;

import javax.annotation.Nullable;

import com.wl4g.dopaas.common.bean.lcdp.extra.ExtraOptionDefinition;
import com.wl4g.dopaas.common.bean.lcdp.extra.ExtraOptionDefinition.GenExtraOption;
import com.wl4g.dopaas.lcdp.codegen.engine.converter.DbTypeConverter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;

import static com.wl4g.component.common.collection.CollectionUtils2.safeList;
import static com.wl4g.component.common.lang.Assert2.*;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * {@link GeneratorProvider} group collection.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public enum GenProviderSetDefinition {

	IamWebMvcVueJS(asList(IAM_SPINGCLOUD_MVN, IAM_VUEJS), DbTypeConverter.JAVA),

	IamWebMvc(asList(IAM_SPINGCLOUD_MVN), DbTypeConverter.JAVA),

	DubboWebMvcVueJS(asList(SPINGDUBBO_MVN, IAM_VUEJS), DbTypeConverter.JAVA),

	GonicWebMVC(asList(GO_GONICWEB), DbTypeConverter.Golang),

	JustVueJS(asList(IAM_VUEJS), DbTypeConverter.JS),

	JustNgJS(asList(NGJS), DbTypeConverter.JS);

	/** {@link GenProviderAlias} */
	@NotEmpty
	private final List<String> providers;

	/**
	 * When the generator provider group contains the DAO layer of the generated
	 * database, it is necessary to set the source {@link DbTypeConverter} type
	 * to map the relationship between DbColumnType and attrType.
	 */
	@Nullable
	private final DbTypeConverter converter;

	private GenProviderSetDefinition(@NotEmpty List<String> providers, @Nullable DbTypeConverter converter) {
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
	 * Gets providers by {@link GenProviderSetDefinition}.
	 * 
	 * @param providerSet
	 * @return
	 */
	public static List<String> getProviders(@Nullable String providerSet) {
		for (GenProviderSetDefinition def : values()) {
			if (equalsIgnoreCase(def.name(), providerSet)) {
				return def.providers();
			}
		}
		return emptyList();
	}

	/**
	 * Parse {@link GenProviderSetDefinition} name.
	 * 
	 * @param providerSet
	 * @return
	 */
	public static GenProviderSetDefinition safeOf(@NotBlank String providerSet) {
		for (GenProviderSetDefinition s : values()) {
			if (equalsIgnoreCase(s.name(), providerSet)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Parse {@link GenProviderSetDefinition} name.
	 * 
	 * @param providerSet
	 * @return
	 */
	public static GenProviderSetDefinition of(@NotBlank String providerSet) {
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