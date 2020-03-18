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

import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.MenuService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-10-30 15:44:00
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

	@Autowired
	private MenuService menuService;

	@RequestMapping(value = "/tree")
	public RespBase<?> getMenuTree() {
		RespBase<Object> resp = RespBase.create();
		Map<String, Object> result = menuService.getMenuTree();
		resp.setData(result);
		return resp;
	}

	@RequestMapping(value = "/list")
	public RespBase<?> getMenuList() {
		RespBase<Object> resp = RespBase.create();
		List<Menu> menus = menuService.getMenuList();
		Assert.notEmpty(menus, "not menu role found , Please ask you manager and check the user-role-menu config");
		resp.forMap().put("data", menus);
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = {"iam:menu"})
	public RespBase<?> save(@RequestBody Menu menu) {
		RespBase<Object> resp = RespBase.create();
		menuService.save(menu);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = {"iam:menu"})
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		menuService.del(id);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = {"iam:menu"})
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		Menu menu = menuService.detail(id);
		resp.forMap().put("data", menu);
		return resp;
	}

}