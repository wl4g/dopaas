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

import com.wl4g.devops.ci.core.context.PipelineContext;

import java.io.File;

/**
 * Based MAVEN pipeline provider.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月12日
 * @since
 */
public abstract class BasedMavenPipelineProvider extends BasedPhysicalBackupPipelineProvider {

	public BasedMavenPipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	protected void doBuildWithDefaultCommands(String projectDir, File logPath, Integer taskId) throws Exception {
		String defaultCommand = "mvn -f " + projectDir + "/pom.xml clean install -Dmaven.test.skip=true -DskipTests";
		processManager.exec(String.valueOf(taskId), defaultCommand, null, logPath, 300000);
	}

}