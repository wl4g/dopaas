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
package com.wl4g.devops.ci.pipeline;

/**
 * Pipeline type definition.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月29日
 * @since
 */
public abstract class PipelineType {

	/**
	 * MAVEN assemble tar provider alias.
	 */
	final public static String MVN_ASSEMBLE_TAR = "PipeWithMvnAssTar";

	/**
	 * Spring boot executable jar provider alias.
	 */
	final public static String SPRING_EXECUTABLE_JAR = "PipeWithSpringExecJar";

	/**
	 * Docker native provider alias.
	 */
	final public static String DOCKER_NATIVE = "PipeWithDockerNative";

	/**
	 * DJANGO standard provider alias.
	 */
	final public static String DJANGO_STANDARD = "PipeWithDjangoStandard";

	/**
	 * NPM provider alias.
	 */
	final public static String NPM_VIEW = "PipeWithNpm";

}
