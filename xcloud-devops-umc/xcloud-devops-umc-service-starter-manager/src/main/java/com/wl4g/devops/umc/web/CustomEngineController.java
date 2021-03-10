/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.umc.web;

import com.wl4g.component.common.task.QuartzCronUtils;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.umc.CustomEngine;
import com.wl4g.devops.common.bean.umc.CustomEngineModel;
import com.wl4g.devops.umc.service.CustomEngineService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/engine")
public class CustomEngineController extends BaseController {

	@Autowired
	private CustomEngineService customEngineService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "umc:custom:engine" }, logical = AND)
	public RespBase<?> list(String name, PageHolder<CustomEngine> pm) {
		RespBase<Object> resp = RespBase.create();
		return resp.withData(customEngineService.list(pm, name));
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "umc:custom:engine" }, logical = AND)
	public RespBase<?> save(@RequestBody CustomEngineModel customEngineModel) {
		log.info("into CustomDatasourceController.save prarms::" + "customEngine = {} ", customEngineModel);
		notNull(customEngineModel, "customEngine is null");
		RespBase<Object> resp = RespBase.create();

		CustomEngine customEngine = new CustomEngine();
		BeanUtils.copyProperties(customEngineModel, customEngine, "notifyGroupIds");

		Integer[] notifyGroupIds = customEngineModel.getNotifyGroupIds();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < notifyGroupIds.length; i++) {
			if (i == notifyGroupIds.length - 1) {
				stringBuilder.append(notifyGroupIds[i]);
			} else {
				stringBuilder.append(notifyGroupIds[i]).append(",");
			}
		}
		customEngine.setNotifyGroupIds(stringBuilder.toString());

		customEngineService.save(customEngine);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "umc:custom:engine" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		CustomEngine customEngine = customEngineService.detal(id);

		CustomEngineModel customEngineModel = new CustomEngineModel();
		BeanUtils.copyProperties(customEngine, customEngineModel, "notifyGroupIds");
		if (StringUtils.isNotBlank(customEngine.getNotifyGroupIds())) {
			String[] split = customEngine.getNotifyGroupIds().split(",");
			Integer[] integers = new Integer[split.length];
			for (int i = 0; i < split.length; i++) {
				integers[i] = Integer.valueOf(split[i]);
			}
			customEngineModel.setNotifyGroupIds(integers);
		}
		resp.setData(customEngineModel);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "umc:custom:engine" }, logical = AND)
	public RespBase<?> del(Long id) {
		log.info("into CustomDatasourceController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		customEngineService.del(id);
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
		boolean isValid = QuartzCronUtils.isValidExpression(expression);
		resp.forMap().put("validExpression", isValid);
		if (!isValid) {
			return resp;
		}
		try {
			List<String> nextExecTime = QuartzCronUtils.getNextExecTime(expression, numTimes);
			resp.forMap().put("nextExecTime", StringUtils.join(nextExecTime, "\n"));
		} catch (Exception e) {
			resp.forMap().put("validExpression", false);
			return resp;
		}
		return resp;
	}

}