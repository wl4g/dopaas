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
package com.wl4g.paas.umc.web;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static org.apache.shiro.authz.annotation.Logical.AND;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.paas.common.bean.umc.CustomAlarmEvent;
import com.wl4g.paas.umc.service.CustomAlarmEventService;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/event")
public class CustomAlarmEventController extends BaseController {

	@Autowired
	private CustomAlarmEventService customAlarmEventService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "umc:alarm:event" }, logical = AND)
	public RespBase<?> list(String name, PageHolder<CustomAlarmEvent> pm) {
		RespBase<Object> resp = RespBase.create();
		return resp.withData(customAlarmEventService.list(pm, name));
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "umc:alarm:event" }, logical = AND)
	public RespBase<?> save(@RequestBody CustomAlarmEvent customAlarmEvent) {
		log.info("into CustomDatasourceController.save prarms::" + "customAlarmEvent = {} ", customAlarmEvent);
		notNull(customAlarmEvent, "customAlarmEvent is null");
		RespBase<Object> resp = RespBase.create();
		customAlarmEventService.save(customAlarmEvent);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "umc:alarm:event" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		CustomAlarmEvent customAlarmEvent = customAlarmEventService.detail(id);
		resp.setData(customAlarmEvent);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "umc:alarm:event" }, logical = AND)
	public RespBase<?> del(Long id) {
		log.info("into CustomDatasourceController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		customAlarmEventService.del(id);
		return resp;
	}

}