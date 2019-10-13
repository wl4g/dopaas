package com.wl4g.devops.ci.config;

/**
 * CICD pipeline process, the related configuration classes sent to cluster
 * nodes after completion of construction.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class TranformProperties {

	private String cipherKey;
	private MvnAssTar mvnAssTar;
	private DockerNative dockerNative;

	public String getCipherKey() {
		return cipherKey;
	}

	public void setCipherKey(String cipherKey) {
		this.cipherKey = cipherKey;
	}

	public MvnAssTar getMvnAssTar() {
		return mvnAssTar;
	}

	public void setMvnAssTar(MvnAssTar mvnAssTar) {
		this.mvnAssTar = mvnAssTar;
	}

	public DockerNative getDockerNative() {
		return dockerNative;
	}

	public void setDockerNative(DockerNative dockerNative) {
		this.dockerNative = dockerNative;
	}

	public static class MvnAssTar {

	}

	public static class DockerNative {
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
