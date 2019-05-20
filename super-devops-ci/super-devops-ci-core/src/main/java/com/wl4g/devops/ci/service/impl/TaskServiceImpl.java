package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:44:00
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskDetailDao taskDetailDao;

    @Override
    public List<Task> list(CustomPage customPage) {
        return taskDao.list(customPage);
    }

    @Override
    public List<TaskDetail> getDetailByTaskId(Integer id) {
        return taskDetailDao.getDetailByTaskId(id);
    }

    @Override
    @Transactional
    public Task createTask(Project project, List<AppInstance> instances,int type,int status,String branchName,String sha
            ,Integer parentId,String command,Integer tarType){

        Task task = new Task();
        task.preInsert();
        task.setType(type);
        task.setProjectId(project.getId());
        task.setStatus(status);
        task.setBranchName(branchName);
        task.setSha(sha);
        task.setParentId(parentId);
        task.setCommand(command);
        task.setTarType(tarType);
        taskDao.insertSelective(task);
        for(AppInstance instance : instances){
            TaskDetail taskDetail = new TaskDetail();
            taskDetail.preInsert();
            taskDetail.setTaskId(task.getId());
            taskDetail.setInstanceId(instance.getId());
            taskDetail.setStatus(CiDevOpsConstants.TASK_STATUS_CREATE);
            taskDetailDao.insertSelective(taskDetail);
        }
        return task;
    }

    @Override
    public void updateTaskStatus(int taskId,int status){
        Task task = new Task();
        task.preUpdate();
        task.setId(taskId);
        task.setStatus(status);
        taskDao.updateByPrimaryKeySelective(task);
    }

    @Override
    public void updateTaskDetailStatus(int taskDetailId,int status){
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.preUpdate();
        taskDetail.setId(taskDetailId);
        taskDetail.setStatus(status);
        taskDetailDao.updateByPrimaryKeySelective(taskDetail);
    }


}
