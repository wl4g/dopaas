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
package com.wl4g.devops.ci.config;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.isTrue;

/**
 * CICD pipeline process, the related configuration classes sent to cluster
 * nodes after completion of construction.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class TranformProperties {

	private String cipherKey = EMPTY;

	/** Delivery timeout for distribution to a single instance. */
	private long waitCompleteTimeout = 10_000L;

	private MvnAssTarProperties mvnAssTar = new MvnAssTarProperties();

	private DockerNativeProperties dockerNative = new DockerNativeProperties();

	public String getCipherKey() {
		return cipherKey;
	}

	public void setCipherKey(String cipherKey) {
		if (!isBlank(cipherKey)) {
			this.cipherKey = cipherKey;
		}
	}

	public long getWaitCompleteTimeout() {
		return waitCompleteTimeout;
	}

	public void setWaitCompleteTimeout(long waitCompleteTimeout) {
		isTrue(waitCompleteTimeout > 0, "Wait complete timeout must greater than 0");
		this.waitCompleteTimeout = waitCompleteTimeout;
	}

	public MvnAssTarProperties getMvnAssTar() {
		return mvnAssTar;
	}

	public void setMvnAssTar(MvnAssTarProperties mvnAssTar) {
		if (nonNull(mvnAssTar)) {
			this.mvnAssTar = mvnAssTar;
		}
	}

	public DockerNativeProperties getDockerNative() {
		return dockerNative;
	}

	public void setDockerNative(DockerNativeProperties dockerNative) {
		if (nonNull(dockerNative)) {
			this.dockerNative = dockerNative;
		}
	}

	/**
	 * MAVEN assemble tar transform properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月23日
	 * @since
	 */
	public static class MvnAssTarProperties {

	}

	/**
	 * DOCKER native transform properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月23日
	 * @since
	 */
	public static class DockerNativeProperties {
		public String dockerPushUsername;
		public String dockerPushPasswd;

		public String getDockerPushUsername() {
			return dockerPushUsername;
		}

		public void setDockerPushUsername(String dockerPushUsername) {
			this.dockerPushUsername = dockerPushUsername;
		}

		public String getDockerPushPasswd() {
			return dockerPushPasswd;
		}

		public void setDockerPushPasswd(String dockerPushPasswd) {
			this.dockerPushPasswd = dockerPushPasswd;
		}
	}
}