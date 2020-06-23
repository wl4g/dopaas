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
package com.wl4g.devops.ci.pipeline.container;

/**
 * Container (for Docker/CoreOS RKT/MESOS/LXC) pipeline provider SPI.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-16
 * @since
 */
public interface ContainerPipelineProvider {

	/**
	 * Building of target image.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param projectDir
	 * @throws Exception
	 */
	void buildImage() throws Exception;

	/**
	 * Pull container image.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param image
	 *            Container image name or ID.
	 * @throws Exception
	 */
	void imagePull(String remoteHost, String user, String sshkey, String image) throws Exception;

	/**
	 * Stopping in container.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param container
	 *            ContainerId or container name.
	 * @throws Exception
	 */
	void stopContainer(String remoteHost, String user, String sshkey, String container) throws Exception;

	/**
	 * Removing destroy container.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param container
	 *            ContainerId or container name.
	 * @throws Exception
	 */
	void destroyContainer(String remoteHost, String user, String sshkey, String container) throws Exception;

	/**
	 * Starting container.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param container
	 *            Running of container commands.
	 * @throws Exception
	 */
	void startContainer(String remoteHost, String user, String sshkey, String runContainerCommands) throws Exception;

}