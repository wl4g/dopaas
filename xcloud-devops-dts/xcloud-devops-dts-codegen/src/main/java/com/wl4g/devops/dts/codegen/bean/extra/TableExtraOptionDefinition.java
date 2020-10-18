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
package com.wl4g.devops.dts.codegen.bean.extra;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.bean.ConfigOption;
import com.wl4g.devops.dts.codegen.bean.GenTable;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.collection.Collections2.isEmptyArray;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * {@link GenTable} extensible configuration options definitions.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public enum TableExtraOptionDefinition {

	TableDeleteType(new GenTableExtraOption("tab.del-type", "deleteWithLogical", "deleteWithPhysical", "deleteWithNone")),

	TableEditType(new GenTableExtraOption("tab.edit-type", "editOnDialog", "editOnPage")),

	IsExportExcel(new GenTableExtraOption("tab.export-excel", "true", "false"));

	/** Gen provider extra option of {@link GenTableExtraOption} . */
	@NotNull
	private final GenTableExtraOption option;

	private TableExtraOptionDefinition(@NotNull GenTableExtraOption option) {
		notNullOf(option, "option");
		this.option = option.validate();
	}

	public final GenTableExtraOption getOption() {
		return option;
	}

	/**
	 * Gets {@link GenTableExtraOption} by names.
	 * 
	 * @param provider
	 * @return
	 */
	public static List<GenTableExtraOption> getOptions(@Nullable String... names) {
		final List<String> conditions = new ArrayList<>();
		if (!isEmptyArray(names)) {
			conditions.addAll(asList(names));
		}
		return asList(values()).stream().filter(o -> (isEmpty(conditions) || conditions.contains(o.getOption().getName())))
				.map(o -> o.getOption()).collect(toList());
	}

	/**
	 * Gen project extra options. see: {@link GenTable}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static class GenTableExtraOption extends ConfigOption {

		public GenTableExtraOption() {
			super();
		}

		public GenTableExtraOption(@NotBlank String name, @NotEmpty String... values) {
			super(name, values);
		}

		public GenTableExtraOption(@NotBlank String name, @NotEmpty List<String> values) {
			super(name, values);
		}

		@SuppressWarnings("unchecked")
		@Override
		public GenTableExtraOption validate() {
			return super.validate();
		}

	}

}