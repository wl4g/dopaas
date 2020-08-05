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
package com.wl4g.devops.ci.analyses.coordinate;

import static java.util.Collections.singletonList;
import static org.apache.shiro.util.Assert.notEmpty;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.components.support.cli.DestroableProcessManager;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.components.support.task.ApplicationTaskRunner;
import com.wl4g.devops.ci.analyses.config.CiAnalyzerProperties;
import com.wl4g.devops.ci.analyses.config.ExecutorProperties;
import com.wl4g.devops.ci.analyses.model.AnalysingModel;
import com.wl4g.devops.ci.analyses.tasks.TaskManager;

/**
 * Abstract basic codes analyzers.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-18
 * @since
 */
public abstract class AbstractAnalysisCoordinator<P extends AnalysingModel> extends ApplicationTaskRunner<RunnerProperties>
		implements AnalysisCoordinator<P> {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiAnalyzerProperties config;
	@Autowired
	protected DestroableProcessManager processManager;
	@Autowired
	protected JedisService jedisService;
	@Autowired
	protected TaskManager manager;

	public AbstractAnalysisCoordinator(ExecutorProperties executor) {
		super(executor);
	}

	@Override
	public void analyze(P model) throws Exception {

		// Submit task.
		List<Future<File>> futures = getWorker().invokeAll(singletonList(new Callable<File>() {
			@Override
			public File call() throws Exception {
				try {
					doAnalyze(model);
				} catch (Exception e) {
					log.error("", e);
				}
				return null;
			}
		}), 15 * 60 * 1000, TimeUnit.SECONDS);

		// Register task future.
		notEmpty(futures, "empty codesAnalyzing task futures.");
		manager.registerFuture(model.getProjectName(), futures.get(0));

	}

	/**
	 * Execution analyze.
	 * 
	 * @param model
	 * @throws Exception
	 */
	protected abstract void doAnalyze(P model) throws Exception;

}