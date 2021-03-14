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
package com.wl4g.dopaas.uci.config;

import java.io.File;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

/**
 * CICD pipeline process, the related configuration classes sent to cluster
 * nodes after completion of construction.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class DeployProperties {

	/** Transfer bin file to remote host SSH2 key. */
	private String cipherKey = EMPTY;

	/**
	 * Transfer bin file to remote host timeout (Ms). </br>
	 * {@link com.wl4g.dopaas.uci.config.CiProperties#applyDefaultProperties()}
	 */
	private Long transferTimeoutMs;

	/**
	 * Transfer remote user home directory name.</br>
	 * 
	 * <pre>
	 * e.g. /home/{APP_USER}/.ci-temporary
	 * e.g. /root/.ci-temporary
	 * </pre>
	 */
	private String remoteHomeTmpDir = "$HOME" + File.separator + ".ci-temporary";

	private MvnAssTarProperties mvnAssTar = new MvnAssTarProperties();

	private DockerNativeProperties dockerNative = new DockerNativeProperties();

	public String getCipherKey() {
		hasText(cipherKey, "Transfer SSH2 cipherKey must not be empty.");
		return cipherKey;
	}

	public void setCipherKey(String cipherKey) {
		if (!isBlank(cipherKey)) {
			this.cipherKey = cipherKey;
		}
	}

	public Long getTransferTimeoutMs() {
		// notNull(transferTimeoutMs, "Transfer timeout must not be empty.");
		// isTrue(transferTimeoutMs > 0, "Transfer timeout must greater than
		// 0.");
		return transferTimeoutMs;
	}

	public void setTransferTimeoutMs(Long transferTimeoutMs) {
		if (nonNull(transferTimeoutMs)) {
			isTrue(transferTimeoutMs > 0, "Transfer timeout must greater than 0.");
			this.transferTimeoutMs = transferTimeoutMs;
		}
	}

	public String getRemoteHomeTmpDir() {
		return remoteHomeTmpDir;
	}

	public String getRemoteHomeTmpDir(String user) {
		return "/home/" + user + "/.ci-temporary";
	}

	public void setRemoteHomeTmpDir(String remoteHomeTmpDir) {
		if (!isBlank(remoteHomeTmpDir)) {
			this.remoteHomeTmpDir = remoteHomeTmpDir;
		}
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