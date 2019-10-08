package com.wl4g.devops.ci.bean;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.dto.TaskResult;

/**
 * @author vjay
 * @date 2019-09-30 10:32:00
 */
public class BaseDeployBean {

    TaskHistory taskHistory;

    Project project;

    TaskResult taskResult = new TaskResult();

    public TaskHistory getTaskHistory() {
        return taskHistory;
    }

    public void setTaskHistory(TaskHistory taskHistory) {
        this.taskHistory = taskHistory;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
