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

import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.share.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/contact")
public class ContactController extends BaseController {

	@Autowired
	private ContactService contactService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, String email, String phone, PageModel pm) {
		log.info("into ContactController.list prarms::" + "name = {} , email = {} , phone = {} , pm = {} ", name, email, phone,
				pm);
		RespBase<Object> resp = RespBase.create();
		PageModel list = contactService.list(pm, name, email, phone);
		resp.setData(list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody AlarmContact alarmContact) {
		log.info("into ProjectController.save prarms::" + "alarmContact = {} ", alarmContact);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(alarmContact, "contact is null");
		Assert.hasText(alarmContact.getName(), "name is null");
		Assert.hasText(alarmContact.getEmail(), "email is null");
		Assert.notEmpty(alarmContact.getGroups(), "contactGroup is null");
		contactService.save(alarmContact);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		log.info("into ContactController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		AlarmContact contact = contactService.detail(id);
		resp.forMap().put("contact", contact);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into ContactController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		contactService.del(id);
		return resp;
	}

}