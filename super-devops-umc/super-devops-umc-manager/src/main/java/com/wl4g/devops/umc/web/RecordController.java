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

import com.wl4g.devops.common.bean.umc.AlarmRecord;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.umc.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/record")
public class RecordController extends BaseController {

	@Autowired
	private RecordService recordService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, PageModel pm, String startDate, String endDate) {
		log.info("into RecordController.list prarms::" + "name = {} , pm = {} , startDate = {} , endDate = {} ", name, pm,
				startDate, endDate);
		RespBase<Object> resp = RespBase.create();
		resp.setData(recordService.list(pm,name,startDate,endDate));
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		log.info("into CollectorController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		AlarmRecord alarmRecord = recordService.detail(id);
		resp.forMap().put("alarmRecord", alarmRecord);
		return resp;
	}

}