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
package com.wl4g.dopaas.udm.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.dopaas.udm.service.ShareService;

import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link LinkController}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-05
 * @sine v1.0.0
 * @see https://github.com/sindresorhus/github-markdown-css
 */
@RestController
@RequestMapping("/link")
public class LinkController {

	final protected Logger log = getLogger(getClass());

	@Autowired
	private ShareService shareService;

	/**
	 * Rendering markdown doc
	 * 
	 * @param code
	 * @param passwd
	 * @return
	 * @see https://github.com/sindresorhus/github-markdown-css
	 */
	@CrossOrigin
	@RequestMapping(value = "/rendering")
	public RespBase<?> rendering(String code, String passwd) {
		log.info("rendering file code={} passwd={}", code, passwd);
		return shareService.rendering(code, passwd);
	}

}