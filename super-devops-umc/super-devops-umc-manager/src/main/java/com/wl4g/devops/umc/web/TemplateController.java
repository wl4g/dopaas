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

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.umc.service.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/template")
public class TemplateController extends BaseController {

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private TemplateService templateService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, Integer metricId, String classify, CustomPage customPage) {
		log.info("into TemplateController.list prarms::" + "name = {} , metric = {} , classify = {} , customPage = {} ", name,
				metricId, classify, customPage);
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<CustomPage> page = PageHelper.startPage(pageNum, pageSize, true);
		List<AlarmTemplate> list = alarmTemplateDao.list(name, metricId, classify);
		for (AlarmTemplate alarmTpl : list) {
			String tags = alarmTpl.getTags();
			if (StringUtils.isNotBlank(tags)) {
				alarmTpl.setTagMap(parseJSON(tags, new TypeReference<List<Map<String, String>>>() {
				}));
			}
		}
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
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
	public RespBase<?> detail(Integer id) {
		log.info("into TemplateController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		AlarmTemplate alarmTemplate = templateService.detail(id);
		resp.getData().put("alarmTemplate", alarmTemplate);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into TemplateController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		templateService.del(id);

		return resp;
	}

	@RequestMapping(value = "/getByClassify")
	public RespBase<?> getByClassify(String classify) {
		RespBase<Object> resp = RespBase.create();
		List<AlarmTemplate> list = alarmTemplateDao.list(null, null, classify);
		resp.getData().put("list", list);
		return resp;
	}

}