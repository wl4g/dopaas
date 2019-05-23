package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.io.Serializable;

public class TriggerDetail extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private Integer triggerId;

	private Integer instanceId;

	public Integer getTriggerId() {
		return triggerId;
	}

	public void setTriggerId(Integer triggerId) {
		this.triggerId = triggerId;
	}

	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}
}