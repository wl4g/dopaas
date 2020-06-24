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

import com.wl4g.devops.dao.erm.LogPipelineCleanerDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Pipeline running record logs cleaner tool.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-11
 * @since
 */
public class PipelineLogPurger extends GenericOperatorTool {

	public static final int beforeSec = 30 * 24 * 60 * 60;// 30 day

	@Autowired
	private LogPipelineCleanerDao logPipelineCleanerDao;

	@Override
	protected void doStartup(ScheduledExecutorService scheduler) {
		scheduler.scheduleAtFixedRate(this, config.getLogCleaner().getInitialDelaySec(), config.getLogCleaner().getPeriodSec(),
				SECONDS);
	}

	@Override
	public void run() {
		// TODO e.g. Do ci_task_history cleanup...
		// cleanJobStatusTraceLog();
		// cleanJobExecutionLog();
		// cleanUmcAlarmRecord();
		cleanCiTaskHistory();
	}

	@SuppressWarnings("unused")
	private void cleanJobStatusTraceLog() {
		Date currentTimeBySecound = beforeTimeSec(beforeSec);
		try {
			logPipelineCleanerDao.cleanJobStatusTraceLog(currentTimeBySecound);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void cleanJobExecutionLog() {
		Date currentTimeBySecound = beforeTimeSec(beforeSec);
		try {
			logPipelineCleanerDao.cleanJobExecutionLog(currentTimeBySecound);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void cleanUmcAlarmRecord() {
		Date currentTimeBySecound = beforeTimeSec(beforeSec);
		try {
			logPipelineCleanerDao.cleanUmcAlarmRecordSublist(currentTimeBySecound);
			logPipelineCleanerDao.cleanUmcAlarmRecord(currentTimeBySecound);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cleanCiTaskHistory() {
		// TODO
		Date currentTimeBySecound = beforeTimeSec(beforeSec);
		try {
			// logPipelineCleanerDao.cleanCiTaskHistorySublist(currentTimeBySecound);
			// logPipelineCleanerDao.cleanCiTaskHistory(currentTimeBySecound);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Date beforeTimeSec(int stuff) {
		stuff = -stuff;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, stuff);
		Date beforeDate = calendar.getTime();
		return beforeDate;
	}
}