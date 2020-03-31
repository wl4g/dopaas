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
package com.wl4g.devops.umc.web;

import com.wl4g.devops.common.bean.umc.CustomEngine;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.tool.common.task.QuartzCronUtils;
import com.wl4g.devops.umc.service.CustomEngineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;

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
	public RespBase<?> list(String name, PageModel pm) {
		RespBase<Object> resp = RespBase.create();
		PageModel list = customEngineService.list(pm, name);
		resp.setData(list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody CustomEngine customEngine) {
		log.info("into CustomDatasourceController.save prarms::" + "customEngine = {} ", customEngine);
		notNull(customEngine, "customEngine is null");
		RespBase<Object> resp = RespBase.create();
		customEngineService.save(customEngine);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		CustomEngine customEngine = customEngineService.detal(id);
		resp.setData(customEngine);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
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