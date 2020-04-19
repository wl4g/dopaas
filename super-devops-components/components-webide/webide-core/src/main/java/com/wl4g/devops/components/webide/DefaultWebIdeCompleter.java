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
package com.wl4g.devops.components.webide;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import com.wl4g.devops.components.webide.generate.parse.GenericClassInfo;
import com.wl4g.devops.components.webide.model.CompleteRequest;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Default webide completer.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月31日
 * @since
 */
public class DefaultWebIdeCompleter implements WebIdeCompleter {

	final protected SmartLogger log = getLogger(getClass());

	@Override
	public String complete(CompleteRequest req) {
		log.debug("On completion of start: {}, line: {}, input: {}", req.getStart(), req.getLine(), req.getInput());
		return req.getInput();
	}

	@Override
	public GenericClassInfo getGenericClassesInfo() {
		return null;
	}

}
