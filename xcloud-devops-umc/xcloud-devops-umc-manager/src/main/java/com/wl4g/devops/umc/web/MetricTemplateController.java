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
import com.wl4g.component.data.page.PageModel;
import com.wl4g.devops.common.bean.umc.MetricTemplate;
import com.wl4g.devops.umc.service.MetricTemplateService;
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
@RequestMapping("/metric")
public class MetricTemplateController extends BaseController {

	@Autowired
	private MetricTemplateService metricTemplateService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "umc:metrictemplate" })
	public RespBase<?> list(String metric, String classify, PageModel<MetricTemplate> pm) {
		RespBase<Object> resp = RespBase.create();
		return resp.withData(metricTemplateService.list(pm, metric, classify));
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "umc:metrictemplate" })
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
	@RequiresPermissions(value = { "umc:metrictemplate" })
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		MetricTemplate metricTemplate = metricTemplateService.detal(id);
		resp.setData(metricTemplate);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "umc:metrictemplate" })
	public RespBase<?> del(Long id) {
		log.info("into MetricTemplateController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		metricTemplateService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getByClassify")
	public RespBase<?> getByClassify(String classify) {
		RespBase<Object> resp = RespBase.create();
		List<MetricTemplate> metricTemplate = metricTemplateService.getByClassify(classify);
		resp.forMap().put("list", metricTemplate);
		return resp;
	}

}