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
package com.wl4g.devops.dts.codegen.engine.specs;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.collection.CollectionUtils2;
import com.wl4g.components.common.id.SnowflakeIdGenerator;
import com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenExtraOptionDefinition;
import com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenExtraOptionDefinition.ConfigOption;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static java.lang.Math.abs;
import static java.lang.String.valueOf;
import static java.util.Locale.US;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Generic base specification utility.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public class BaseSpecs {

	/**
	 * Gets the string that converts the first letter to uppercase
	 */
	public static String firstUCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		char[] cs = str.toCharArray();
		if (97 <= cs[0] && cs[0] <= 122) {
			cs[0] -= 32;
		}
		return valueOf(cs);
	}

	/**
	 * Gets the string that converts the first letter to lowercase
	 */
	public static String firstLCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		char[] cs = str.toCharArray();
		if (65 <= cs[0] && cs[0] <= 90) {
			cs[0] += 32;
		}

		return valueOf(cs);
	}

	/**
	 * Gets the string that converts the all letter to upper case
	 */
	public static String uCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		return str.toUpperCase(US);
	}

	/**
	 * Gets the string that converts the all letter to lower case
	 */
	public static String lCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		return str.toLowerCase(US);
	}

	/**
	 * Check configured extra options.
	 * 
	 * @param configuredOptions
	 * @param name
	 * @param value
	 * @return
	 * @see {@link GenExtraOptionDefinition}
	 */
	public boolean checkConfigured(@Nullable List<ConfigOption> configuredOptions, @NotBlank String name,
			@NotBlank String value) {
		hasTextOf(name, "name");
		hasTextOf(value, "value");

		// Extra config optional
		if (CollectionUtils2.isEmpty(configuredOptions)) {
			return false;
		}

		// Verify name and value contains in options.
		for (ConfigOption opt : configuredOptions) {
			if (equalsIgnoreCase(opt.getName(), name) && equalsIgnoreCase(opt.getSelectedValue(), value)) {
				return true;
			}
		}

		return false;
	}


	public long genId(){
		return abs((int) (SnowflakeIdGenerator.getDefault().nextId() % 10_000_000_000L));
	}

}
