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
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.umc.service.ConfigService;
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
@RequestMapping("/config")
public class ConfigController extends BaseController {

	@Autowired
	private AlarmConfigDao alarmConfigDao;

	@Autowired
	private ConfigService configService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(Integer templateId, Integer contactGroupId, CustomPage customPage) {
		log.info("into ConfigController.list prarms::" + "templateId = {} , contactGroupId = {} , customPage = {} ", templateId,
				contactGroupId, customPage);
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<CustomPage> page = PageHelper.startPage(pageNum, pageSize, true);
		List<AlarmConfig> list = alarmConfigDao.list(templateId, contactGroupId);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.forMap().put("page", customPage);
		resp.forMap().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody AlarmConfig alarmConfig) {
		Assert.notNull(alarmConfig, "config is null");
		Assert.notNull(alarmConfig.getCollectId(), "instance is null");
		Assert.notNull(alarmConfig.getContactGroupId(), "contact is null");
		Assert.notNull(alarmConfig.getTemplateId(), "template is null");
		RespBase<Object> resp = RespBase.create();
		configService.save(alarmConfig);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		AlarmConfig alarmConfig = configService.detail(id);
		resp.forMap().put("alarmConfig", alarmConfig);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		configService.del(id);
		return resp;
	}

}