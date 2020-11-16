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
package com.wl4g.devops.ci.pipeline.provider;

import com.wl4g.components.common.annotation.Stable;
import com.wl4g.devops.ci.core.context.PipelineContext;

/**
 * Pipeline provider SPI.
 * 
 * @author vjay
 * @author Wangl.sir <983708408@qq.com>
 * @date 2019-05-05 17:17:00
 */
@Stable
public interface PipelineProvider {

	/**
	 * Execution pipeline with provider process.
	 * 
	 * @throws Exception
	 */
	default void execute() throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Roll-back with provider process.
	 * 
	 * @throws Exception
	 */
	default void rollback() throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get pipeline information.
	 * 
	 * @return
	 */
	PipelineContext getContext();

	/**
	 * Invoke remote commands.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param command
	 * @param sshkey
	 * @throws Exception
	 */
	void doRemoteCommand(String remoteHost, String user, String command, String sshkey) throws Exception;

	char[] getUsableCipherSshKey(String sshkey) throws Exception;

	String getAssetsFingerprint();

	String getSourceFingerprint();

	/**
	 * Pipeline type definition.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月29日
	 * @since
	 */
	public static abstract class PipelineKind {

		/**
		 * MAVEN assemble tar provider alias.
		 */
		final public static String MVN_ASSEMBLE_TAR = "MvnAssTar";

		/**
		 * Spring boot executable jar provider alias.
		 */
		final public static String SPRING_EXECUTABLE_JAR = "SpringExecJar";

		/**
		 * War tomcat pipeline provider alias.
		 */
		final public static String WAR_TOMCAT = "warTomcat";

		/**
		 * NPM provider alias.
		 */
		final public static String NPM_VIEW = "NpmTar";

		/**
		 * view native ,needn't build
		 */
		final public static String VIEW_NATIVE = "ViewNative";

		/**
		 * Python3 standard provider alias.
		 */
		final public static String PYTHON3_STANDARD = "Python3";

		/**
		 * GOLANG standard mod provider alias.
		 */
		final public static String GOLANG_STANDARD = "Golang";

		/**
		 * Docker native provider alias.
		 */
		final public static String DOCKER_NATIVE = "DockerNative";

		/**
		 * CoreOS(Red hat) RKT native provider alias.
		 */
		final public static String RKT_NATIVE = "RktNative";

	}

}