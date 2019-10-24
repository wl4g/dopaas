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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.AppClusterDao;
import com.wl4g.devops.share.service.AppClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用组管理
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月2日
 * @since
 */
@RestController
@RequestMapping("/appGroup")
public class AppClusterController extends BaseController {

	@Autowired
	private AppClusterService appClusterService;

	@Autowired
	private AppClusterDao appClusterDao;

	@RequestMapping(value = "/list")
	public RespBase<?> list(CustomPage customPage, String clusterName) {
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<Task> page = PageHelper.startPage(pageNum, pageSize, true);


		List<AppCluster> list = appClusterDao.list(clusterName);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

}