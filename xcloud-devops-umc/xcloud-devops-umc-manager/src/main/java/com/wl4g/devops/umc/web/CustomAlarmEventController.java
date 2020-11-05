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

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.umc.CustomAlarmEvent;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.umc.service.CustomAlarmEventService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static org.apache.shiro.authz.annotation.Logical.AND;

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
	@RequiresPermissions(value = {"umc:alarm:event"}, logical = AND)
	public RespBase<?> list(String name, PageModel<?> pm) {
		RespBase<Object> resp = RespBase.create();
		PageModel<?> list = customAlarmEventService.list(pm, name);
		resp.setData(list);
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = {"umc:alarm:event"}, logical = AND)
	public RespBase<?> save(@RequestBody CustomAlarmEvent customAlarmEvent) {
		log.info("into CustomDatasourceController.save prarms::" + "customAlarmEvent = {} ", customAlarmEvent);
		notNull(customAlarmEvent, "customAlarmEvent is null");
		RespBase<Object> resp = RespBase.create();
		customAlarmEventService.save(customAlarmEvent);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = {"umc:alarm:event"}, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		CustomAlarmEvent customAlarmEvent = customAlarmEventService.detal(id);
		resp.setData(customAlarmEvent);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = {"umc:alarm:event"}, logical = AND)
	public RespBase<?> del(Long id) {
		log.info("into CustomDatasourceController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		customAlarmEventService.del(id);
		return resp;
	}

}