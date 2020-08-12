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
package com.wl4g.devops.scm.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;

import com.wl4g.devops.scm.common.model.GetRelease;
import com.wl4g.devops.scm.common.model.PreRelease;
import com.wl4g.devops.scm.common.model.ReleaseMessage;
import com.wl4g.devops.scm.common.model.ReportInfo;
import com.wl4g.devops.scm.publish.WatchDeferredResult;

/**
 * Guide configuration source context handler.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public class CheckCentralConfigServerHandler implements CentralConfigServerHandler, InitializingBean {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ReleaseMessage getSource(GetRelease get) {
		return null;
	}

	@Override
	public void report(ReportInfo report) {
	}

	@Override
	public void release(PreRelease pre) {
	}

	@Override
	public WatchDeferredResult<ResponseEntity<?>> watch(GetRelease watch) {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		throw new IllegalStateException(String.format("Used SCM server, you must customize implenments the '%s' !",
				CentralConfigServerHandler.class.getName()));
	}

}