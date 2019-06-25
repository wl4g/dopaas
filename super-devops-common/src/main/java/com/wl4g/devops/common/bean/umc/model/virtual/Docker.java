package com.wl4g.devops.common.bean.umc.model.virtual;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wl4g.devops.common.bean.umc.model.Base;

/**
 * @author vjay
 * @date 2019-06-17 16:42:00
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Docker extends Base {
	private static final long serialVersionUID = -6431729185849981843L;

	private DockerInfo[] dockerInfo;

	public DockerInfo[] getDockerInfo() {
		return dockerInfo;
	}

	public void setDockerInfo(DockerInfo[] dockerInfo) {
		this.dockerInfo = dockerInfo;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DockerInfo {
		private String containerId;
		private String name;
		private String cpuPerc;
		private String memUsage;
		private String memPerc;
		private String netIO;
		private String blockIO;
		private String PIDs;

		public String getContainerId() {
			return containerId;
		}

		public void setContainerId(String containerId) {
			this.containerId = containerId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCpuPerc() {
			return cpuPerc;
		}

		public void setCpuPerc(String cpuPerc) {
			this.cpuPerc = cpuPerc;
		}

		public String getMemUsage() {
			return memUsage;
		}

		public void setMemUsage(String memUsage) {
			this.memUsage = memUsage;
		}

		public String getMemPerc() {
			return memPerc;
		}

		public void setMemPerc(String memPerc) {
			this.memPerc = memPerc;
		}

		public String getNetIO() {
			return netIO;
		}

		public void setNetIO(String netIO) {
			this.netIO = netIO;
		}

		public String getBlockIO() {
			return blockIO;
		}

		public void setBlockIO(String blockIO) {
			this.blockIO = blockIO;
		}

		public String getPIDs() {
			return PIDs;
		}

		public void setPIDs(String PIDs) {
			this.PIDs = PIDs;
		}
	}

}
