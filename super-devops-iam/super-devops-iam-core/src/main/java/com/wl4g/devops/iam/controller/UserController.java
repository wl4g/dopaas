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
package com.wl4g.devops.iam.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.devops.common.bean.iam.User;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.UserService;
import com.wl4g.devops.page.PageModel;

/**
 * @author vjay
 * @date 2019-10-29 10:10:00
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	// @RequiresPermissions({"iam:user:list","iam:group:tree","iam:role:getRolesByUserGroups"})
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {"iam:user"})
	public RespBase<?> list(PageModel pm, String userName, String displayName) {
		RespBase<Object> resp = RespBase.create();
		PageModel list = userService.list(pm, userName, displayName);
		resp.setData(list);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = {"iam:user"})
	public RespBase<?> detail(Integer userId) {
		Assert.notNull(userId, "userId is null");
		RespBase<Object> resp = RespBase.create();
		User detail = userService.detail(userId);
		resp.forMap().put("data", detail);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = {"iam:user"})
	public RespBase<?> del(Integer userId) {
		Assert.notNull(userId, "userId is null");
		RespBase<Object> resp = RespBase.create();
		userService.del(userId);
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = {"iam:user"})
	public RespBase<?> save(@RequestBody User user) {
		Assert.notNull(user, "user is null");
		RespBase<Object> resp = RespBase.create();
		userService.save(user);
		return resp;
	}

}