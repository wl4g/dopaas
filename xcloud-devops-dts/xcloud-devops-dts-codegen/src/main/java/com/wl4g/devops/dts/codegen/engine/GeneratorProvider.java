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

import static com.wl4g.components.common.reflect.ReflectionUtils2.getFieldValues;
import static com.wl4g.devops.dts.codegen.engine.GeneratorProvider.GenProviderAlias.*;

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
	 * 
	 * @see
	 */
	public static interface GenProviderAlias {

		public static final String MVN_SPINGCLOUD = "mvnSpringCloudGenProvider";
		public static final String GO_STANDARD = "goStandardGenProvider";
		public static final String VUEJS = "vueGenProvider";
		public static final String AGJS = "agGenProvider";

		/** List of field values of class {@link GenProviderAlias}. */
		public static final String[] VALUES = getFieldValues(GenProviderAlias.class, "VALUES").toArray(new String[] {});

	}

	public static enum GenCategory {

		JUST_DAO(new String[]{MVN_SPINGCLOUD}),

		JUST_VUEJS(new String[]{VUEJS}),

		JUST_AGJS(new String[]{AGJS}),

		DAO_SERVICE_CONTRELLER(new String[]{MVN_SPINGCLOUD, MVN_SPINGCLOUD}),

		DAO_SERVICE_CONTRELLER_VUE(new String[]{MVN_SPINGCLOUD, MVN_SPINGCLOUD, VUEJS});

		private final String[] providers;

		GenCategory(String[] providers) {
			this.providers = providers;
		}

		public String[] getProviders() {
			return providers;
		}

		public static String[] getProvidersByTplCategory(String tplCategory) {
			for (GenCategory anEnum : values()) {
				if (anEnum.name().equals(tplCategory)) {
					return anEnum.getProviders();
				}
			}
			return null;
		}

		public static List<String> getAllTplCategory() {
			List<String> list = new ArrayList<String>();
			GenCategory[] enums = GenCategory.values();
			for (int i = 0; i < enums.length; i++) {
				list.add(enums[i].name());
			}
			return list;
		}

	}

}