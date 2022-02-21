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
package com.wl4g.dopaas.umc.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.common.bean.umc.AlarmConfig;
import com.wl4g.dopaas.umc.service.AlarmConfigService;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/alarmConfig")
public class AlarmConfigController extends BaseController {

	private @Autowired AlarmConfigService alarmConfigService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "umc:config" })
	public RespBase<PageHolder<AlarmConfig>> list(PageHolder<AlarmConfig> pm, Long templateId, Long contactGroupId) {
		log.info("into ConfigController.list prarms::" + "templateId = {} , contactGroupId = {} , pm = {} ", templateId,
				contactGroupId, pm);
		RespBase<PageHolder<AlarmConfig>> resp = RespBase.create();
		return resp.withData(alarmConfigService.list(pm, templateId, contactGroupId));
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "umc:config" })
	public RespBase<?> save(@RequestBody AlarmConfig alarmConfig) {
		Assert.notNull(alarmConfig, "config is null");
		Assert.notNull(alarmConfig.getCollectId(), "instance is null");
		Assert.notNull(alarmConfig.getContactGroupId(), "contact is null");
		Assert.notNull(alarmConfig.getTemplateId(), "template is null");
		RespBase<Object> resp = RespBase.create();
		alarmConfigService.save(alarmConfig);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "umc:config" })
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		AlarmConfig alarmConfig = alarmConfigService.detail(id);
		resp.forMap().put("alarmConfig", alarmConfig);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "umc:config" })
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		alarmConfigService.del(id);
		return resp;
	}

}