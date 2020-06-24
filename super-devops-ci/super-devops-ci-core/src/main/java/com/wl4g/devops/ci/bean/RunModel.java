package com.wl4g.devops.ci.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author vjay
 * @date 2020-03-06 17:17:00
 */
public class RunModel implements Serializable {
	private static final long serialVersionUID = 8940373806493080114L;

	private String runId;// RUN-{flowId|pipeId}-{timestamp}

	private String type;// FLOW|PIPE

	private Long createTime;// timestamp

	private List<Pipeline> pipelines;

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public List<Pipeline> getPipelines() {
		return pipelines;
	}

	public void setPipelines(List<Pipeline> pipelines) {
		this.pipelines = pipelines;
	}

	public static class Pipeline {

		private Integer pipeId;

		private String provider;// MvnAssTar

		private String service;// e.g mobile

		private Long createTime;// timestamp

		private Integer current;// e.g mp

		private String status;// WAITING|RUNNING|FAILED|SUCCESS

		private int priority;

		private int attempting;

		private String node;

		// List<String> modules;

		List<ModulesPorject> modulesPorjects;

		public Integer getPipeId() {
			return pipeId;
		}

		public void setPipeId(Integer pipeId) {
			this.pipeId = pipeId;
		}

		public String getProvider() {
			return provider;
		}

		public void setProvider(String provider) {
			this.provider = provider;
		}

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}

		public Long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Long createTime) {
			this.createTime = createTime;
		}

		public Integer getCurrent() {
			return current;
		}

		public void setCurrent(Integer current) {
			this.current = current;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public int getAttempting() {
			return attempting;
		}

		public void setAttempting(int attempting) {
			this.attempting = attempting;
		}

		/*
		 * public List<String> getModules() { return modules; }
		 * 
		 * public void setModules(List<String> modules) { this.modules =
		 * modules; }
		 */

		public List<ModulesPorject> getModulesPorjects() {
			return modulesPorjects;
		}

		public void setModulesPorjects(List<ModulesPorject> modulesPorjects) {
			this.modulesPorjects = modulesPorjects;
		}

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public static class ModulesPorject {

			private Integer projectId;

			private String ref;// Branch | Tag

			private String status;// WAITING|BUILDING|FAILED|SUCCESS

			public Integer getProjectId() {
				return projectId;
			}

			public void setProjectId(Integer projectId) {
				this.projectId = projectId;
			}

			public String getRef() {
				return ref;
			}

			public void setRef(String ref) {
				this.ref = ref;
			}

			public String getStatus() {
				return status;
			}

			public void setStatus(String status) {
				this.status = status;
			}
		}
	}

}
