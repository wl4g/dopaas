package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.List;

public class Orchestration extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private String name;

    private Integer status;

    private Integer type;

    private List<OrchestrationPipeline> orchestrationPipelines;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<OrchestrationPipeline> getOrchestrationPipelines() {
        return orchestrationPipelines;
    }

    public void setOrchestrationPipelines(List<OrchestrationPipeline> orchestrationPipelines) {
        this.orchestrationPipelines = orchestrationPipelines;
    }
}