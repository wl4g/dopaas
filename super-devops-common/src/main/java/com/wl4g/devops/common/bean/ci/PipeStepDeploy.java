package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class PipeStepDeploy  extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer pipeId;

    private Integer deployType;

    private String deployDockerfileContent;

    private Integer deployConfigType;

    private String deployConfigContent;

    public Integer getPipeId() {
        return pipeId;
    }

    public void setPipeId(Integer pipeId) {
        this.pipeId = pipeId;
    }

    public Integer getDeployType() {
        return deployType;
    }

    public void setDeployType(Integer deployType) {
        this.deployType = deployType;
    }

    public String getDeployDockerfileContent() {
        return deployDockerfileContent;
    }

    public void setDeployDockerfileContent(String deployDockerfileContent) {
        this.deployDockerfileContent = deployDockerfileContent == null ? null : deployDockerfileContent.trim();
    }

    public Integer getDeployConfigType() {
        return deployConfigType;
    }

    public void setDeployConfigType(Integer deployConfigType) {
        this.deployConfigType = deployConfigType;
    }

    public String getDeployConfigContent() {
        return deployConfigContent;
    }

    public void setDeployConfigContent(String deployConfigContent) {
        this.deployConfigContent = deployConfigContent == null ? null : deployConfigContent.trim();
    }
}