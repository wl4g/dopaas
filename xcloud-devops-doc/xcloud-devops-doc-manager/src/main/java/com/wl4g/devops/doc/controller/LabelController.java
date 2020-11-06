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
package com.wl4g.devops.doc.controller;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.doc.Label;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.doc.service.LabelService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * @author vjay
 * @date 2019-11-12 11:03:00
 */
@RestController
@RequestMapping("/label")
public class LabelController extends BaseController {

	@Autowired
	private LabelService labelService;

	@RequestMapping("/list")
	@RequiresPermissions(value = { "doc:label" }, logical = AND)
	public RespBase<?> list(PageModel<Label> pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(labelService.list(pm, name));
		return resp;
	}

	@RequestMapping("/save")
	@RequiresPermissions(value = { "doc:label" }, logical = AND)
	public RespBase<?> save(Label label) {
		RespBase<Object> resp = RespBase.create();
		labelService.save(label);
		return resp;
	}

	@RequestMapping("/del")
	@RequiresPermissions(value = { "doc:label" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		labelService.del(id);
		return resp;
	}

	@RequestMapping("/detail")
	@RequiresPermissions(value = { "doc:label" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		Label label = labelService.detail(id);
		resp.setData(label);
		return resp;
	}

	@RequestMapping("/allLabel")
	public RespBase<?> allLabel() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(labelService.allLabel());
		return resp;
	}

}