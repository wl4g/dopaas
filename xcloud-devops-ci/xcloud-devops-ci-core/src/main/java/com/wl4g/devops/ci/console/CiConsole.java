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
package com.wl4g.devops.ci.console;

import com.wl4g.devops.ci.console.args.EvictionIntervalArgument;
import com.wl4g.devops.ci.console.args.ManualCleanArgument;
import com.wl4g.devops.ci.pipeline.TimeoutJobsEvictor;
import com.wl4g.devops.ci.tool.LogPurger;
import com.wl4g.shell.common.annotation.ShellMethod;
import com.wl4g.shell.core.handler.SimpleShellContext;
import com.wl4g.shell.springboot.annotation.ShellComponent;
import static com.wl4g.devops.ci.pipeline.TimeoutJobsEvictor.DEFAULT_MIN_WATCH_MS;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.components.common.lang.Exceptions.getStackTraceAsString;
import static java.lang.String.format;
import static java.util.Objects.isNull;

/**
 * CI/CD console point
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-21 15:41:00
 * @since
 */
@ShellComponent
public class CiConsole {

	/** {@link TimeoutJobsEvictor}. */
	@Autowired
	private TimeoutJobsEvictor evictor;

	/** {@link LogPurger}. */
	@Autowired
	private LogPurger logPurger;

	/**
	 * Modifiying eviction(expired) jobs cleanup internal(ms).
	 * 
	 * @param arg
	 * @return
	 */
	@ShellMethod(keys = "modifyEvictionInterval", group = GROUP, help = "Modify the execution interval of the timeout job evictor")
	public void modifyEvictionInternal(EvictionIntervalArgument arg, SimpleShellContext context) {
		try {
			notNull(arg.getEvictionIntervalMs(), "Modify eviction internal is required");
			isTrue(arg.getEvictionIntervalMs() > DEFAULT_MIN_WATCH_MS,
					format("Eviction internal must greater than <%s>ms.", DEFAULT_MIN_WATCH_MS));

			context.printf(format("Modifying eviction internal: <%s>ms", arg.getEvictionIntervalMs()));

			// Refresh eviction internal
			evictor.refreshEvictionIntervalMs(arg.getEvictionIntervalMs());

			context.printf(format("Modifyed evictor of internal:<%s>ms", arg.getEvictionIntervalMs()));
		} catch (Exception e) {
			context.printf(format("Failed to modify eviction internal. cause by: %s", getStackTraceAsString(e)));
		} finally {
			context.completed();
		}
	}

	@ShellMethod(keys = "manualCleanupLog", group = GROUP, help = "Modifying global jobs timeout finalizer max-interval")
	public void manualCleanupLog(ManualCleanArgument arg, SimpleShellContext context) {
		try {
			// Calculate clean up end time
			Long cleanStopTime = null;
			if (!isNull(arg.getRemainDay())) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, -arg.getRemainDay());
				cleanStopTime = cal.getTimeInMillis();
			}

			context.printf(format("Manual cleaning built history logs. remainDay: <%s>day (stopTime: <%s>ms)", arg.getRemainDay(),
					cleanStopTime));
			logPurger.cleanupBuiltHistoryLogs(cleanStopTime);

			context.printf(format("Rmoved built history logs, remaining of: <%s>day (stopTime: <%s>ms)", arg.getRemainDay(),
					cleanStopTime));
		} catch (Exception e) {
			context.printf(format("Failed to cleanup logs. cause by: %s", getStackTraceAsString(e)));
		} finally {
			context.completed();
		}
	}

	public static final String GROUP = "Devops CI/CD console commands";

}