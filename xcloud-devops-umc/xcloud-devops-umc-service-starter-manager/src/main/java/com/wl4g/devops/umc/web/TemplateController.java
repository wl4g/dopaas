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

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.umc.service.TemplateService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/template")
public class TemplateController extends BaseController {

	@Autowired
	private TemplateService templateService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "umc:templat" })
	public RespBase<?> list(String name, Long metricId, String classify, PageHolder<AlarmTemplate> pm) {
		log.info("into TemplateController.list prarms::" + "name = {} , metric = {} , classify = {} , pm = {} ", name, metricId,
				classify, pm);

		RespBase<Object> resp = RespBase.create();
		return resp.withData(templateService.list(pm, name, metricId, classify));
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "umc:templat" })
	public RespBase<?> save(@RequestBody AlarmTemplate alarmTemplate) {
		log.info("into TemplateController.save prarms::" + "alarmTemplate = {} ", alarmTemplate);
		Assert.notNull(alarmTemplate, "template is null");
		Assert.notNull(alarmTemplate.getMetricId(), "meetric is null");
		Assert.notEmpty(alarmTemplate.getRules(), "rules is null");
		RespBase<Object> resp = RespBase.create();
		templateService.save(alarmTemplate);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "umc:templat" })
	public RespBase<?> detail(Long id) {
		log.info("into TemplateController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		AlarmTemplate alarmTemplate = templateService.detail(id);
		resp.forMap().put("alarmTemplate", alarmTemplate);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "umc:templat" })
	public RespBase<?> del(Long id) {
		log.info("into TemplateController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		templateService.del(id);

		return resp;
	}

	@RequestMapping(value = "/getByClassify")
	public RespBase<?> getByClassify(String classify) {
		RespBase<Object> resp = RespBase.create();
		List<AlarmTemplate> list = templateService.getByClassify(classify);
		resp.forMap().put("list", list);
		return resp;
	}

}