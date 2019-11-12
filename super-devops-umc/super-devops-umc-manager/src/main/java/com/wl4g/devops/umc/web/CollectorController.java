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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.umc.AlarmCollector;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmCollectorDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.umc.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/collector")
public class CollectorController extends BaseController {

	@Autowired
	private AlarmCollectorDao alarmCollectorDao;

	@Autowired
	private CollectorService collectorService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, String addr, PageModel pm) {
		if (log.isInfoEnabled()) {
			log.info("Find collectors prarms::" + "name = {} , pm = {} ", name, pm);
		}

		RespBase<Object> resp = RespBase.create();
		Page<PageModel> page = PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true);
		List<AlarmCollector> list = alarmCollectorDao.list(name, addr);
		pm.setPageNum(pm.getPageNum());
		pm.setPageSize(pm.getPageSize());
		pm.setTotal(page.getTotal());

		resp.buildMap().put("page", pm);
		resp.buildMap().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(AlarmCollector alarmCollector) {
		log.info("into CollectorController.save prarms::" + "alarmCollector = {} ", alarmCollector);
		RespBase<Object> resp = RespBase.create();
		collectorService.save(alarmCollector);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		log.info("into CollectorController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		AlarmCollector alarmCollector = alarmCollectorDao.selectByPrimaryKey(id);
		resp.buildMap().put("alarmCollector", alarmCollector);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into CollectorController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		collectorService.del(id);
		return resp;
	}

}