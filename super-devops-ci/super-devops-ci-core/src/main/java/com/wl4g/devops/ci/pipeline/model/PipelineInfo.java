package com.wl4g.devops.ci.pipeline.model;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

/**
 * Deploy information wrapper API.
 *
 * @author Wangl.sir
 * @version v1.0.0 2019-09-29
 * @since
 */
public interface PipelineInfo {

    Project getProject();

    void setProject(Project project);

    int getTarType();

    void setTarType(int tarType);

    String getPath();

    void setPath(String path);

    String getBranch();

    void setBranch(String branch);

    String getAlias();

    void setAlias(String alias);

    List<AppInstance> getInstances();

    void setInstances(List<AppInstance> instances);

    TaskHistory getTaskHistory();

    void setTaskHistory(TaskHistory taskHistory);

    TaskHistory getRefTaskHistory();

    void setRefTaskHistory(TaskHistory refTaskHistory);

    List<TaskHistoryDetail> getTaskHistoryDetails();

    void setTaskHistoryDetails(List<TaskHistoryDetail> taskHistoryDetails);

    String getTarName();

    void setTarName(String tarName);
}
