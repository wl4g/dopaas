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
package com.wl4g.dopaas.umc.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.infra.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.umc.AlarmRecord;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
@FeignConsumer(name = "${provider.serviceId.umc-facade:umc-facade}")
@RequestMapping("/alarmRecord-service")
public interface AlarmRecordService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<AlarmRecord> list(@RequestBody PageHolder<AlarmRecord> pm, @RequestParam("name") String name,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate);

	@RequestMapping(value = "/detail", method = GET)
	AlarmRecord detail(@RequestParam("id") Long id);

}