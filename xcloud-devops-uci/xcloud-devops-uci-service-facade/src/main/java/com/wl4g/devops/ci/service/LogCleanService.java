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
package com.wl4g.devops.ci.service;

import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * {@link LogCleanService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2019-12-24
 * @sine v1.0.0
 * @see
 */
@FeignConsumer(name = "${provider.serviceId.ci-facade:logClean-service}")
@RequestMapping("/logClean")
public interface LogCleanService {

	@RequestMapping(value = "/cleanOrchestrationHistory", method = POST)
	int cleanOrchestrationHistory(@RequestParam(value = "cleanStopTime",required = false) Long cleanStopTime);

	@RequestMapping(value = "/cleanPipelineHistory", method = POST)
	int cleanPipelineHistory(@RequestParam(value = "cleanStopTime",required = false) Long cleanStopTime);

}