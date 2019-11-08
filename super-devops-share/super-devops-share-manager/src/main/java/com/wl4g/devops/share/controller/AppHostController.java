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

import com.wl4g.devops.common.bean.share.AppHost;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.AppHostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/host")
public class AppHostController extends BaseController {

	@Autowired
	private AppHostDao appHostDao;

	@RequestMapping(value = "/all")
	public RespBase<?> allType() {
		RespBase<Object> resp = RespBase.create();
		List<AppHost> list = appHostDao.list(null, null, null);
		resp.buildMap().put("list", list);
		return resp;
	}

}