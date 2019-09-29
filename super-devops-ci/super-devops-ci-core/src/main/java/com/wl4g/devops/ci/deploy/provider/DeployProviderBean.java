package com.wl4g.devops.ci.deploy.provider;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

/**
 * @author vjay
 * @date 2019-09-29 14:21:00
 */
public class DeployProviderBean {

    private Project project;
    private int tarType;
    private String path;
    private String branch;
    private String alias;
    private List<AppInstance> instances;
    private TaskHistory taskHistory;
    private TaskHistory refTaskHistory;
    private List<TaskHistoryDetail> taskHistoryDetails;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getTarType() {
        return tarType;
    }

    public void setTarType(int tarType) {
        this.tarType = tarType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<AppInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<AppInstance> instances) {
        this.instances = instances;
    }

    public TaskHistory getTaskHistory() {
        return taskHistory;
    }

    public void setTaskHistory(TaskHistory taskHistory) {
        this.taskHistory = taskHistory;
    }

    public TaskHistory getRefTaskHistory() {
        return refTaskHistory;
    }

    public void setRefTaskHistory(TaskHistory refTaskHistory) {
        this.refTaskHistory = refTaskHistory;
    }

    public List<TaskHistoryDetail> getTaskHistoryDetails() {
        return taskHistoryDetails;
    }

    public void setTaskHistoryDetails(List<TaskHistoryDetail> taskHistoryDetails) {
        this.taskHistoryDetails = taskHistoryDetails;
    }
}
