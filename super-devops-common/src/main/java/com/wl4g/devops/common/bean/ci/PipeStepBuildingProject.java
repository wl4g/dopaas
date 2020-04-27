package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class PipeStepBuildingProject extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer buildingId;

    private Integer projectId;

    private String buildCommand;

    private String ref;

    private Integer enable;

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getBuildCommand() {
        return buildCommand;
    }

    public void setBuildCommand(String buildCommand) {
        this.buildCommand = buildCommand == null ? null : buildCommand.trim();
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref == null ? null : ref.trim();
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}