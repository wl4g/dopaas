package com.wl4g.devops.ci.bean;

/**
 * @author vjay
 * @date 2020-03-09 10:02:00
 */
public class PipelineModel extends RunModel.Pipeline {

	private String runId;

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

}
