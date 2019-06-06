/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;

public class NothingConfigSourceHandler implements ConfigContextHandler {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ReleaseMessage findSource(GetRelease get) {
		log.info("Find config source: {}", get);
		return null;
	}

	@Override
	public void report(ReportInfo report) {
		log.info("Config release report: {}", report);
	}

	@Override
	public void release(PreRelease pre) {
		log.info("Config source release: {}", pre);
	}

	@Override
	public void refreshMeta(boolean focus) {
		log.info("Refresh Meta");
	}

}