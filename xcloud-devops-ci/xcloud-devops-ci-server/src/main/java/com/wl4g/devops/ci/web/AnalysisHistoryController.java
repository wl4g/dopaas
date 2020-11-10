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
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.ci.service.AnalysisHistoryService;
import com.wl4g.devops.common.bean.ci.AnalysisHistory;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * @author vjay
 * @date 2019-12-16 16:13:00
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisHistoryController extends BaseController {

	@Autowired
	private AnalysisHistoryService analysisHistoryService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "ci:analysis" }, logical = AND)
	public RespBase<?> list(String groupName, String projectName, PageModel<AnalysisHistory> pm) {
		log.info("Query projects for groupName: {}, projectName: {}, {} ", groupName, projectName, pm);
		RespBase<Object> resp = RespBase.create();
		resp.setData(analysisHistoryService.list(pm));
		return resp;
	}

}