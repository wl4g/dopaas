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
import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.cron.CronUtils;
import com.wl4g.devops.ci.cron.DynamicTask;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import com.wl4g.devops.dao.scm.AppClusterDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_TYPE_TIMMING;

/**
 * CI/CD controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/trigger")
public class TriggerController {

    final protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private TriggerDao triggerDao;

    @Autowired
    private TriggerService triggerService;

    @Autowired
    private AppClusterDao appClusterDao;

    @Autowired
    private DynamicTask dynamicTask;

    @Autowired
    private CiCdProperties config;

    @Autowired
    private CiService ciService;

    @Autowired
    private ProjectDao projectDao;


    /**
     * Page List
     * @param customPage
     * @param id
     * @param name
     * @param taskId
     * @param enable
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/list")
    public RespBase<?> list(CustomPage customPage, Integer id, String name, Integer taskId, Integer enable,
                            String startDate,String endDate) {
        log.info("into TriggerController.list prarms::"+ "customPage = {} , id = {} , name = {} , taskId = {} , enable = {} , startDate = {} , endDate = {} ",
                customPage, id, name, taskId, enable, startDate, endDate );
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page<Trigger> page = PageHelper.startPage(pageNum, pageSize, true);

        String endDateStr = null;
        if(StringUtils.isNotBlank(endDate)){
            endDateStr = DateUtils.formatDate(DateUtils.addDays(DateUtils.parseDate(endDate),1));
        }

        List<Trigger> list = triggerDao.list(id,name,taskId,enable,startDate,endDateStr);
        customPage.setPageNum(pageNum);

        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    /**
     * Save
     * @param trigger
     * @return
     */
    @RequestMapping(value = "/save")
    public RespBase<?> save(Trigger trigger) {
        log.info("into TriggerController.save prarms::"+ "trigger = {} ", trigger );
        RespBase<Object> resp = RespBase.create();
        checkTriggerCron(trigger);
        if (null != trigger.getId() && trigger.getId() > 0) {
            trigger.preUpdate();
            trigger = triggerService.update(trigger);
        } else {
            trigger.preInsert();
            trigger.setDelFlag(DEL_FLAG_NORMAL);
            trigger = triggerService.insert(trigger);
        }
        if(trigger.getType()!=null&&trigger.getType()== TASK_TYPE_TIMMING){
            restart(trigger.getId());
        }

        return resp;
    }

    /**
     * Check form
     * @param trigger
     */
    private void checkTriggerCron(Trigger trigger) {
        Assert.notNull(trigger, "trigger can not be null");
        Assert.notNull(trigger.getType(), "type can not be null");
        Assert.notNull(trigger.getAppClusterId(), "project can not be null");
        if (trigger.getType() == TASK_TYPE_TIMMING) {
            Assert.notNull(trigger.getCron(), "cron can not be null");
        }
    }

    /**
     * Restart Cron -- when modify or create the timing task , restart the cron
     * @param triggerId
     */
    private void restart(Integer triggerId) {
        Trigger trigger = triggerDao.selectByPrimaryKey(triggerId);
        dynamicTask.restartCron(trigger.getId().toString(), trigger.getCron(), trigger);
    }

    /**
     * Detail by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        log.info("into TriggerController.detail prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(id, "id can not be null");
        Trigger trigger = triggerDao.selectByPrimaryKey(id);
        Assert.notNull(trigger, "not found trigger");

        resp.getData().put("trigger", trigger);
        resp.getData().put("appClusterId", trigger.getAppClusterId());

        return resp;
    }

    /**
     * Delete by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        log.info("into TriggerController.del prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(id, "id can not be null");
        triggerService.delete(id);
        dynamicTask.stopCron(id.toString());
        return resp;
    }


    /**
     * Check cron expression is valid
     * @param expression
     * @return
     */
    @RequestMapping(value = "/checkCronExpression")
    public RespBase<?> checkCronExpression(String expression) {
        log.debug("into TriggerController.checkCronExpression prarms::"+ "expression = {} ", expression );
        RespBase<Object> resp = RespBase.create();
        boolean isValid = CronUtils.isValidExpression(expression);
        resp.getData().put("validExpression", isValid);
        return resp;
    }

    /**
     * Get Cron next Execute Times
     * @param expression
     * @param numTimes
     * @return
     */
    @RequestMapping(value = "/cronNextExecTime")
    public RespBase<?> cronNextExecTime(String expression, Integer numTimes) {
        log.debug("into TriggerController.cronNextExecTime prarms::"+ "expression = {} , numTimes = {} ", expression, numTimes );
        RespBase<Object> resp = RespBase.create();
        if (null == numTimes || numTimes <= 0) {
            numTimes = 5;
        }
        boolean isValid = CronUtils.isValidExpression(expression);
        resp.getData().put("validExpression", isValid);
        if (!isValid) {
            return resp;
        }
        try {
            List<String> nextExecTime = CronUtils.getNextExecTime(expression, numTimes);
            resp.getData().put("nextExecTime", StringUtils.join(nextExecTime, "\n"));
        } catch (Exception e) {
            resp.getData().put("validExpression", false);
            return resp;
        }
        return resp;
    }


}