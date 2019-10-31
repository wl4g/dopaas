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
package com.wl4g.devops.share.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmContactGroup;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmContactGroupDao;
import com.wl4g.devops.share.service.ContactGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/contactGroup")
public class ContactGroupController extends BaseController {

	@Autowired
	private AlarmContactGroupDao alarmContactGroupDao;

	@Autowired
	private ContactGroupService contactGroupService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, CustomPage customPage) {
		log.info("into ContactGroupController.list prarms::" + "name = {} , customPage = {} ", name, customPage);
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<CustomPage> page = PageHelper.startPage(pageNum, pageSize, true);
		List<AlarmContactGroup> list = alarmContactGroupDao.list(name);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(AlarmContactGroup alarmContactGroup) {
		log.info("into ContactGroupController.save prarms::" + "alarmContactGroup = {} ", alarmContactGroup);
		Assert.notNull(alarmContactGroup, "group is null");
		Assert.hasText(alarmContactGroup.getName(), "groupName is null");
		RespBase<Object> resp = RespBase.create();
		contactGroupService.save(alarmContactGroup);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into ContactController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		contactGroupService.del(id);
		return resp;
	}

	@RequestMapping(value = "/all")
	public RespBase<?> all() {
		RespBase<Object> resp = RespBase.create();
		List<AlarmContactGroup> list = alarmContactGroupDao.list(null);
		resp.getData().put("list", list);
		return resp;
	}

}