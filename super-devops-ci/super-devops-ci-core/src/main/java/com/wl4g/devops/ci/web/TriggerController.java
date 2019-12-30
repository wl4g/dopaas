/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.tool.common.task.quartz.CronUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * CI/CD controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/trigger")
public class TriggerController extends BaseController {

	@Autowired
	private TriggerService triggerService;

	/**
	 * Page List
	 * 
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
	@RequiresPermissions(value = {"ci","ci:trigger"},logical = AND)
	public RespBase<?> list(PageModel pm, Integer id, String name, Integer taskId, Integer enable, String startDate,
			String endDate) {
		log.info(
				"into TriggerController.list prarms::"
						+ "customPage = {} , id = {} , name = {} , taskId = {} , enable = {} , startDate = {} , endDate = {} ",
				pm, id, name, taskId, enable, startDate, endDate);
		RespBase<Object> resp = RespBase.create();
		PageModel list = triggerService.list(pm, id, name, taskId, enable, startDate, endDate);
		resp.setData(list);
		return resp;
	}

	/**
	 * Save
	 * 
	 * @param trigger
	 * @return
	 */
	@RequestMapping(value = "/save")
	@RequiresPermissions(value = {"ci","ci:trigger"},logical = AND)
	public RespBase<?> save(Trigger trigger) {
		log.info("into TriggerController.save prarms::" + "trigger = {} ", trigger);
		RespBase<Object> resp = RespBase.create();
		triggerService.save(trigger);
		return resp;
	}

	/**
	 * Detail by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = {"ci","ci:trigger"},logical = AND)
	public RespBase<?> detail(Integer id) {
		log.info("into TriggerController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		Trigger trigger = triggerService.getById(id);
		resp.setData(trigger);

		return resp;
	}

	/**
	 * Delete by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del")
	@RequiresPermissions(value = {"ci","ci:trigger"},logical = AND)
	public RespBase<?> del(Integer id) {
		log.info("into TriggerController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(id, "id can not be null");
		triggerService.delete(id);
		return resp;
	}

	/**
	 * Check cron expression is valid
	 * 
	 * @param expression
	 * @return
	 */
	@RequestMapping(value = "/checkCronExpression")
	public RespBase<?> checkCronExpression(String expression) {
		log.debug("into TriggerController.checkCronExpression prarms::" + "expression = {} ", expression);
		RespBase<Object> resp = RespBase.create();
		boolean isValid = CronUtils.isValidExpression(expression);
		resp.forMap().put("validExpression", isValid);
		return resp;
	}

	/**
	 * Get Cron next Execute Times
	 * 
	 * @param expression
	 * @param numTimes
	 * @return
	 */
	@RequestMapping(value = "/cronNextExecTime")
	public RespBase<?> cronNextExecTime(String expression, Integer numTimes) {
		log.debug("into TriggerController.cronNextExecTime prarms::" + "expression = {} , numTimes = {} ", expression, numTimes);
		RespBase<Object> resp = RespBase.create();
		if (null == numTimes || numTimes <= 0) {
			numTimes = 5;
		}
		boolean isValid = CronUtils.isValidExpression(expression);
		resp.forMap().put("validExpression", isValid);
		if (!isValid) {
			return resp;
		}
		try {
			List<String> nextExecTime = CronUtils.getNextExecTime(expression, numTimes);
			resp.forMap().put("nextExecTime", StringUtils.join(nextExecTime, "\n"));
		} catch (Exception e) {
			resp.forMap().put("validExpression", false);
			return resp;
		}
		return resp;
	}

}