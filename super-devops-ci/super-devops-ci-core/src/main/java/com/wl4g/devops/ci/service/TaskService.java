package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.CustomPage;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:43:00
 */
public interface TaskService {

    List<Task> list(CustomPage customPage);

    List<TaskDetail> getDetailByTaskId(Integer id);



}
