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
package com.wl4g.devops.ci.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.devops.ci.service.OrchestrationHistoryService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task History controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/orchestrationHistory")
public class OrchestrationHistoryController extends BaseController {

	@Autowired
	private OrchestrationHistoryService orchestrationHistoryService;

	/**
	 * List
	 * 
	 * @param groupName
	 * @param projectName
	 * @param branchName
	 * @param customPage
	 * @return
	 */
	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String runId) {
		RespBase<Object> resp = RespBase.create();
		PageModel list = orchestrationHistoryService.list(pm, runId);
		resp.setData(list);
		return resp;
	}

}