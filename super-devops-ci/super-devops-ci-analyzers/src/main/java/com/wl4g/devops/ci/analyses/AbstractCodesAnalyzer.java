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
package com.wl4g.devops.ci.analyses;

import static java.util.Collections.singletonList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.ci.analyses.config.CiAnalyzerProperties;
import com.wl4g.devops.ci.analyses.model.AnalysingModel;
import com.wl4g.devops.common.task.GenericTaskRunner;
import com.wl4g.devops.common.task.RunnerProperties;
import com.wl4g.devops.support.cli.DestroableProcessManager;

/**
 * Abstract basic codes analyzers.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-18
 * @since
 */
public abstract class AbstractCodesAnalyzer<P extends AnalysingModel> extends GenericTaskRunner<RunnerProperties>
		implements CodesAnalyzer<P> {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiAnalyzerProperties config;

	@Autowired
	protected DestroableProcessManager processManager;

	@Override
	public void analyze(P param) throws Exception {
		submitForComplete(singletonList(() -> {
			try {
				doAnalyze(param);
			} catch (Exception e) {
				log.error("", e);
			}
		}), 15 * 60 * 1000);

	}

	protected abstract void doAnalyze(P param) throws Exception;

}
