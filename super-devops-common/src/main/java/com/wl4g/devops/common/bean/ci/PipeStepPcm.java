package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class PipeStepPcm extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer enable;

    private Integer pipeId;

    private Integer pcmId;

    private String xProjectId;

    private String xTracker;

    private String xAssignTo;

    private String xStatus;

    private String xPriority;

    private String xCustomFields;

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getPipeId() {
        return pipeId;
    }

    public void setPipeId(Integer pipeId) {
        this.pipeId = pipeId;
    }

    public Integer getPcmId() {
        return pcmId;
    }

    public void setPcmId(Integer pcmId) {
        this.pcmId = pcmId;
    }

    public String getxProjectId() {
        return xProjectId;
    }

    public void setxProjectId(String xProjectId) {
        this.xProjectId = xProjectId;
    }

    public String getxTracker() {
        return xTracker;
    }

    public void setxTracker(String xTracker) {
        this.xTracker = xTracker == null ? null : xTracker.trim();
    }

    public String getxStatus() {
        return xStatus;
    }

    public void setxStatus(String xStatus) {
        this.xStatus = xStatus == null ? null : xStatus.trim();
    }

    public String getxPriority() {
        return xPriority;
    }

    public void setxPriority(String xPriority) {
        this.xPriority = xPriority == null ? null : xPriority.trim();
    }

    public String getxCustomFields() {
        return xCustomFields;
    }

    public void setxCustomFields(String xCustomFields) {
        this.xCustomFields = xCustomFields == null ? null : xCustomFields.trim();
    }

    public String getxAssignTo() {
        return xAssignTo;
    }

    public void setxAssignTo(String xAssignTo) {
        this.xAssignTo = xAssignTo;
    }
}