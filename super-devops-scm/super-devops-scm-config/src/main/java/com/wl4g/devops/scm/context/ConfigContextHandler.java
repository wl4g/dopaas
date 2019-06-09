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

import org.springframework.boot.ApplicationRunner;

import com.wl4g.devops.common.bean.scm.model.*;

/**
 * Config soruce context handler interface
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public interface ConfigContextHandler extends ApplicationRunner {

	/**
	 * Find configuration property-source.
	 * 
	 * @param get
	 *            config source get message.
	 * @return
	 */
	public ReleaseMessage findSource(GetRelease get);

	/**
	 * Access configuration client report configure result.
	 * 
	 * @param report
	 *            request parameter.
	 * @param resp
	 *            response parameter.
	 * @return
	 */
	public void report(ReportInfo report);

	/**
	 * Release configuration property-sources.
	 * 
	 * @param pre
	 *            request parameter.
	 */
	public void release(PreRelease pre);

	/**
	 * refreshMeta
	 */
	public void refreshMeta(boolean focus);

}