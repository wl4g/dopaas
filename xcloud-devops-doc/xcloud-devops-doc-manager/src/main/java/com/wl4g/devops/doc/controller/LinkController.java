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
package com.wl4g.devops.doc.controller;

import com.wl4g.components.core.web.RespBase;
import com.wl4g.devops.doc.service.ShareService;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
public class LinkController {

	final protected Logger log = getLogger(getClass());

	@Autowired
	private ShareService shareService;

	@CrossOrigin
	@RequestMapping(value = "/rendering")
	public RespBase<?> rendering(String code, String passwd) {
		log.info("rendering file code={} passwd={}", code, passwd);
		return shareService.rendering(code, passwd);
	}

}