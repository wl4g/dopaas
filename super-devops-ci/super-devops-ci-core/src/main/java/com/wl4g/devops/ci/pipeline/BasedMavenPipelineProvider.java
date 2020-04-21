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
import com.wl4g.devops.support.cli.command.DestroableCommand;
import com.wl4g.devops.support.cli.command.LocalDestroableCommand;

import static java.lang.String.format;

import java.io.File;

/**
 * Based MAVEN pipeline provider.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月12日
 * @since
 */
public abstract class BasedMavenPipelineProvider extends RestorableDeployPipelineProvider {

	/**
	 * Maven default build command.
	 */
	final public static String DEFAULT_MVN_CMD = "mvn -f %s/pom.xml -U clean install -Dmaven.test.skip=true -DskipTests -Dmaven.compile.fork=true -T 2C";

	public BasedMavenPipelineProvider(PipelineContext context) {
		super(context);
	}

	/**
	 * Maven build for default command.
	 * 
	 * <pre>
	 * -DskipTests  # Do not execute the test case, but compile the test case class to generate the corresponding class file under target/test classes.
	 * -Dmaven.test.skip=true  # Do not execute test cases and compile test case classes.
	 * </pre>
	 * 
	 * Note: In order to solve the problem that these two parameters may not be
	 * compatible in different versions of maven, it is recommended to use them
	 * at the same time.
	 */
	@Override
	protected void doBuildWithDefaultCommand(String projectDir, File jobLogFile, Integer taskId) throws Exception {
		String defaultMvnBuildCmd = format(DEFAULT_MVN_CMD, projectDir);
		log.info(writeBuildLog("Building with maven default command: %s", defaultMvnBuildCmd));

		// TODO timeoutMs/pwdDir?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskId), defaultMvnBuildCmd, null, 300000L)
				.setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

}