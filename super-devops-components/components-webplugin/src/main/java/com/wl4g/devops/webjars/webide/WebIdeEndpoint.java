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
package com.wl4g.devops.webjars.webide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wl4g.devops.common.web.embedded.GenericEmbeddedWebappsEndpoint;
import com.wl4g.devops.webjars.config.WebIdeProperties;
import com.wl4g.devops.webjars.webide.model.CompleteRequest;

/**
 * {@link WebIdeEndpoint}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月31日
 * @since
 */
public class WebIdeEndpoint extends GenericEmbeddedWebappsEndpoint {

	/**
	 * {@link WebIdeCompleter}
	 */
	@Autowired
	protected WebIdeCompleter completer;

	public WebIdeEndpoint(WebIdeProperties config) {
		super(config);
	}

	@PostMapping(path = "complete")
	public void complete(@RequestBody CompleteRequest req) throws Exception {
		log.debug("Complete request: {}", req);
		String completed = completer.complete(req);
		log.debug("Completed text: {}", completed);
	}

}
