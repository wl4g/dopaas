package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:44:00
 */
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
}
