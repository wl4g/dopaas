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
package com.wl4g.devops.cmdb.service;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.devops.common.bean.cmdb.Log;
import com.wl4g.devops.common.bean.cmdb.QueryLogModel;

@FeignConsumer(name = "${provider.serviceId.cmdb-facade:logConsole-service}")
@RequestMapping("/logConsole")
public interface LogConsoleService {

	@RequestMapping(value = "/console", method = POST)
	List<Log> console(@RequestBody QueryLogModel model) throws Exception;

}