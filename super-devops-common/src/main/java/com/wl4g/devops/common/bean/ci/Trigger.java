package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Trigger extends BaseBean implements Serializable {

    private static final long serialVersionUID = 381411777614066880L;

    private Integer projectId;

    private String branchName;

    private String command;

    private Integer tarType;

    private List<TriggerDetail> triggerDetails;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getTarType() {
        return tarType;
    }

    public void setTarType(Integer tarType) {
        this.tarType = tarType;
    }

    public List<TriggerDetail> getTriggerDetails() {
        return triggerDetails;
    }

    public void setTriggerDetails(List<TriggerDetail> triggerDetails) {
        this.triggerDetails = triggerDetails;
    }
}