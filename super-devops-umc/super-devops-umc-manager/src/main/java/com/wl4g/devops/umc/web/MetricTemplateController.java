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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.PageModel;
import com.wl4g.devops.common.bean.umc.MetricTemplate;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.MetricTemplateDao;
import com.wl4g.devops.umc.service.MetricTemplateService;
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
@RequestMapping("/metric")
public class MetricTemplateController extends BaseController {

	@Autowired
	private MetricTemplateDao metricTemplateDao;

	@Autowired
	private MetricTemplateService metricTemplateService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String metric, String classify, PageModel pm) {
		RespBase<Object> resp = RespBase.create();

		Page<PageModel> page = PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true);
		List<MetricTemplate> list = metricTemplateDao.list(metric, classify);

		pm.setTotal(page.getTotal());
		resp.buildMap().put("page", pm);
		resp.buildMap().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody MetricTemplate metricTemplate) {
		log.info("into MetricTemplateController.save prarms::" + "metricTemplate = {} ", metricTemplate);
		Assert.notNull(metricTemplate, "metricTemplate is null");
		Assert.hasText(metricTemplate.getClassify(), "classify is null");
		Assert.hasText(metricTemplate.getMetric(), "metric is null");
		RespBase<Object> resp = RespBase.create();
		metricTemplateService.save(metricTemplate);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		MetricTemplate metricTemplate = metricTemplateDao.selectByPrimaryKey(id);
		resp.buildMap().put("metricTemplate", metricTemplate);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into MetricTemplateController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		metricTemplateService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getByClassify")
	public RespBase<?> getByClassify(String classify) {
		RespBase<Object> resp = RespBase.create();
		List<MetricTemplate> metricTemplate = metricTemplateService.getByClassify(classify);
		resp.buildMap().put("list", metricTemplate);
		return resp;
	}

}