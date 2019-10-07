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

import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.dto.TaskResult;

/**
 * Based deploy provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @date 2019-05-05 17:17:00
 */
public abstract interface PipelineProvider {

	/**
	 * Pipeline type definition.
	 * 
	 * @return
	 */
	PipelineType pipelineType();

	void execute() throws Exception;

	void rollback() throws Exception;

	Project getProject();

	TaskResult getTaskResult();

	TaskHistory getTaskHistory();

	String getShaLocal();

	String getShaGit();

	/**
	 * Integrate pipeline type definition.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月29日
	 * @since
	 */
	public static enum PipelineType {

		PIPE_MVN_ASSEMBLE_TAR("PipeMvnAssembleTar"),

		PIPE_SPRING_EXECUTABLE_JAR("PipeSpringExecutableJar"),

		PIPE_DOCKER_NATIVE("PipeDockerNative"),

		PIPE_DJANGO_STDAND("PipeDjangoStdand");

		/**
		 * Integrate pipeline type alias value.
		 */
		final private String alias;

		private PipelineType(String alias) {
			this.alias = alias;
		}

		public String getAlias() {
			return alias;
		}

		public static PipelineType of(String type) {
			Assert.hasText(type, "Pipeline type is required.");
			for (PipelineType t : values()) {
				if (t.getAlias().equals(type) || t.name().equals(type)) {
					return t;
				}
			}
			throw new IllegalArgumentException(String.format("Invalid pipeline type '%s'", type));
		}

	}

}