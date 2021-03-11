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
package com.wl4g.devops.uci.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.devops.uci.data.LogCleanDao;
import com.wl4g.devops.uci.service.LogCleanService;

/**
 * {@link LogCleanServiceImpl}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-11-13
 * @sine v1.0.0
 * @see
 */
@Service
public class LogCleanServiceImpl implements LogCleanService {

	@Autowired
	private LogCleanDao cleanDao;

	@Override
	public int cleanOrchestrationHistory(Long cleanStopTime) {
		// TODO Time zone issues??
		// Timestamps should be used uniformly
		return cleanDao.cleanOrchestrationHistory(new Date(cleanStopTime));
	}

	@Override
	public int cleanPipelineHistory(Long cleanStopTime) {
		// TODO Time zone issues??
		// Timestamps should be used uniformly
		return cleanDao.cleanPipelineHistory(new Date(cleanStopTime));
	}

}