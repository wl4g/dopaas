/*
 * Copyright 2017 ~ 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.rest.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.scm.AppClusterDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Task controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    final protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CiService ciService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AppClusterDao appClusterDao;


    /**
     * Page List
     * @param customPage
     * @param id
     * @param taskName
     * @param groupName
     * @param branchName
     * @param tarType
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/list")
    public RespBase<?> list(CustomPage customPage,Integer id,String taskName ,String groupName, String branchName, Integer tarType,String startDate, String endDate) {
        log.info("into TaskController.list prarms::"+ "customPage = {} , id = {} , taskName = {} , groupName = {} , branchName = {} , tarType = {} , startDate = {} , endDate = {} ",
                customPage, id, taskName, groupName, branchName, tarType, startDate, endDate );
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page<Task> page = PageHelper.startPage(pageNum, pageSize, true);

        String endDateStr = null;
        if(StringUtils.isNotBlank(endDate)){
            endDateStr = DateUtils.formatDate(DateUtils.addDays(DateUtils.parseDate(endDate),1));
        }

        List<Task> list = taskDao.list(id,taskName,groupName, branchName,tarType,startDate,endDateStr);
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    /**
     * Save
     * @param task
     * @param instance
     * @return
     */
    @RequestMapping(value = "/save")
    public RespBase<?> save(Task task, Integer[] instance) {
        log.info("into TaskController.save prarms::"+ "task = {} , instance = {} ", task, instance );
        Assert.notNull(task,"task can not be null");
        Assert.notEmpty(instance, "instances can not be empty");
        checkTask(task);
        RespBase<Object> resp = RespBase.create();
        taskService.save(task,instance);
        return resp;
    }

    /**
     * Detail by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        log.info("into TaskController.detail prarms::"+ "id = {} ", id );
        Assert.notNull(id,"id can not be null");
        RespBase<Object> resp = RespBase.create();
        Task task = taskService.getTaskDetailById(id);

        AppInstance appInstance = null;
        for (TaskDetail taskDetail : task.getTaskDetails()) {
            Integer instanceId = taskDetail.getInstanceId();
            appInstance = appClusterDao.getAppInstance(instanceId.toString());
            if (appInstance != null && appInstance.getEnvId() !=null) {
                break;
            }
        }
        Integer[] instances = new Integer[task.getTaskDetails().size()];
        for (int i = 0; i < task.getTaskDetails().size(); i++) {
            instances[i] = task.getTaskDetails().get(i).getInstanceId();
        }

        resp.getData().put("task",task);
        if(null!=appInstance){
            resp.getData().put("envId", Integer.valueOf(appInstance.getEnvId()));
        }
        resp.getData().put("instances",instances);
        return resp;
    }

    /**
     * Delete by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        Assert.notNull(id,"id can not be null");
        RespBase<Object> resp = RespBase.create();
        taskService.delete(id);
        return resp;
    }

    /**
     * Check the form
     * @param task
     */
    private void checkTask(Task task){
        Assert.hasText(task.getTaskName(),"taskName is null");
        Assert.notNull(task.getAppClusterId(),"clusterId is null");
        Assert.notNull(task.getTarType(),"packType is null");
        Assert.hasText(task.getBranchName(),"branchName is null");
    }

    /**
     * Get List By appClusterId
     * @param appClusterId
     */
    @RequestMapping(value = "/getListByAppClusterId")
    public RespBase<?> getListByAppClusterId(Integer appClusterId) {
        Assert.notNull(appClusterId,"appClusterId can not be null");
        RespBase<Object> resp = RespBase.create();
        List<Task> tasks = taskDao.selectByAppClusterId(appClusterId);
        resp.getData().put("tasks",tasks);
        return resp;
    }

    /**
     * create Task History and run Task
     * @param taskId
     */
    @RequestMapping(value = "/create")
    public RespBase<?> create(Integer taskId) {
        RespBase<Object> resp = RespBase.create();
        ciService.createTask(taskId);
        return resp;
    }





}