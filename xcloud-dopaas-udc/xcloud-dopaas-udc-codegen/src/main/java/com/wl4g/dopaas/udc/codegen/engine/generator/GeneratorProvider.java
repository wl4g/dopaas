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
package com.wl4g.dopaas.udc.codegen.engine.generator;

import java.io.Closeable;

import static com.wl4g.component.common.reflect.ReflectionUtils2.getFieldValues;

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
		public static final String IAM_VUEJS = "iamVuejsProvider";

		/**
		 * AngularJS projecs gen provider.
		 */
		public static final String NGJS = "ngjsProvider";

		/** List of field values of class {@link GenProviderAlias}. */
		public static final String[] VALUES = getFieldValues(GenProviderAlias.class, null, "VALUES").toArray(new String[] {});

	}

}