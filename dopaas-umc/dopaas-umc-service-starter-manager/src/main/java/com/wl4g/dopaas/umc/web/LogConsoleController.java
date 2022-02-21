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

import static org.apache.shiro.authz.annotation.Logical.AND;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.common.bean.cmdb.Log;
import com.wl4g.dopaas.common.bean.cmdb.QueryLogModel;
import com.wl4g.dopaas.umc.service.LogConsoleService;

@RestController
@RequestMapping("/console")
public class LogConsoleController extends BaseController {

	@Autowired
	protected LogConsoleService logConsoleService;

	@RequestMapping("/consoleLog")
	@ResponseBody
	@RequiresPermissions(value = { "cmdb:log" }, logical = AND)
	public RespBase<?> logfile(@Validated @RequestBody QueryLogModel model) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Reading logfile... {}", model);
		}

		RespBase<Object> resp = RespBase.create();
		try {
			List<Log> result = logConsoleService.console(model);
			resp.setData(result);
		} catch (Exception e) {
			log.info("Failed to reading logfile.", e);
			resp.setThrowable(e);
		}
		return resp;
	}
}