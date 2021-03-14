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
package com.wl4g.dopaas.uci.tool;

import com.wl4g.component.common.task.RunnerProperties;
import com.wl4g.component.support.task.ApplicationTaskRunner;
import com.wl4g.dopaas.uci.config.CiProperties;
import com.wl4g.dopaas.uci.service.LogCleanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;

import java.util.Calendar;

import javax.annotation.Nullable;

import static com.wl4g.component.common.lang.TypeConverts.safeLongToInt;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Pipeline running record logs cleaner tool.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-11
 * @since
 */
public class LogPurger extends ApplicationTaskRunner<RunnerProperties> {

	@Autowired
	private CiProperties config;

	@Autowired
	private LogCleanService cleanService;

	public LogPurger() {
		super(new RunnerProperties(1));
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		super.run(args);

		// Starting schedule runner
		getWorker().scheduleAtFixedRate(this, config.getLogCleaner().getInitialDelaySec(), config.getLogCleaner().getPeriodSec(),
				SECONDS);
	}

	@Override
	public void run() {
		cleanupBuiltHistoryLogs(null);
	}

	/**
	 * Cleanup CI/CD built history logs.
	 * 
	 * @param cleanStopTime
	 */
	public void cleanupBuiltHistoryLogs(@Nullable Long cleanStopTime) {
		if (isNull(cleanStopTime)) { // External priority
			cleanStopTime = getDiscardStopTime(config.getLogCleaner().getPipeHistoryRetainSec());
		}
		log.info("Cleanup built history logs of stopTime: {}", cleanStopTime);

		try {
			cleanService.cleanOrchestrationHistory(cleanStopTime);
			cleanService.cleanPipelineHistory(cleanStopTime);
		} catch (Exception e) {
			log.warn("Failed to cleanup discard CI built history logs. caused by: {}", e.getMessage());
		}

	}

	/**
	 * Gets discard history logs end time.
	 * 
	 * @param retainTime
	 * @return
	 */
	private static final long getDiscardStopTime(long retainTime) {
		Calendar cale = Calendar.getInstance();
		cale.add(Calendar.SECOND, safeLongToInt(-retainTime));
		return cale.getTimeInMillis();
	}

	/**
	 * Default discard history log data time.
	 */
	public static final int DEFAULT_DISCARD_SECONDS = 30 * 24 * 60 * 60;

}