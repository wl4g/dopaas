package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class OrchestrationPipeline extends BaseBean {

    private static final long serialVersionUID = 6815608076300843748L;

    private Integer orchestrationId;

    private Integer pipelineId;

    private Integer priority;

    public Integer getOrchestrationId() {
        return orchestrationId;
    }

    public void setOrchestrationId(Integer orchestrationId) {
        this.orchestrationId = orchestrationId;
    }

    public Integer getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Integer pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}