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
package com.wl4g.devops.ci.tool;

import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.components.support.task.ApplicationTaskRunner;
import com.wl4g.devops.ci.config.CiProperties;
import com.wl4g.devops.dao.erm.LogPipelineCleanerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;

import java.util.Calendar;
import java.util.Date;

import static com.wl4g.components.common.lang.TypeConverts.safeLongToInt;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Pipeline running record logs cleaner tool.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-11
 * @since
 */
public class PipelineLogPurger extends ApplicationTaskRunner<RunnerProperties> {

	@Autowired
	protected CiProperties config;

	@Autowired
	private LogPipelineCleanerDao cleanerDao;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		super.run(args);

		getWorker().scheduleAtFixedRate(this, config.getLogCleaner().getInitialDelaySec(), config.getLogCleaner().getPeriodSec(),
				SECONDS);

		Date endTime = getDiscardEndTime(config.getLogCleaner().getPipeHistoryRetainSec());
		cleanCiBuiltHistoryLog(endTime);

		// cleanJobStatusTraceLog(endTime);
		// cleanJobExecutionLog(endTime);
		// cleanUmcAlarmLog(endTime);
	}

	private void cleanCiBuiltHistoryLog(Date cleanEndTime) {
		try {
			cleanerDao.cleanCiTaskHistorySublist(cleanEndTime);
			cleanerDao.cleanCiTaskHistory(cleanEndTime);
		} catch (Exception e) {
			log.warn("Failed to cleanup discard ciBuiltHistoryLog. caused by: {}", e.getMessage());
		}
	}

	// private void cleanJobStatusTraceLog(Date cleanEndTime) {
	// Date currentTimeBySecound = getDiscardEndTime(DEFAULT_DISCARD_SECONDS);
	// try {
	// cleanerDao.cleanJobStatusTraceLog(currentTimeBySecound);
	// } catch (Exception e) {
	// log.warn("Failed to cleanup discard jobStatusTraceLog. caused by: {}",
	// e.getMessage());
	// }
	// }
	//
	// private void cleanJobExecutionLog(Date cleanEndTime) {
	// try {
	// cleanerDao.cleanJobExecutionLog(cleanEndTime);
	// } catch (Exception e) {
	// log.warn("Failed to cleanup discard jobExecutionLog. caused by: {}",
	// e.getMessage());
	// }
	// }
	//
	// private void cleanUmcAlarmLog(Date cleanEndTime) {
	// try {
	// cleanerDao.cleanUmcAlarmRecordSublist(cleanEndTime);
	// cleanerDao.cleanUmcAlarmRecord(cleanEndTime);
	// } catch (Exception e) {
	// log.warn("Failed to cleanup discard UmcAlarmLog. caused by: {}",
	// e.getMessage());
	// }
	// }

	private static Date getDiscardEndTime(long retainTime) {
		retainTime = -retainTime;
		Calendar cale = Calendar.getInstance();
		cale.add(Calendar.SECOND, safeLongToInt(retainTime));
		return cale.getTime();
	}

	/**
	 * Default discard history log data time.
	 */
	public static final int DEFAULT_DISCARD_SECONDS = 30 * 24 * 60 * 60;

}