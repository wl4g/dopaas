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

import com.wl4g.devops.ci.console.args.TasksArgument;
import com.wl4g.devops.ci.console.args.TimeoutCleanupIntervalArgument;
import com.wl4g.devops.ci.pipeline.coordinate.GlobalTimeoutJobCleanupCoordinator;
import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.handler.ShellContext;

import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.tool.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.devops.tool.common.lang.TableFormatters.*;
import static java.lang.String.format;

/**
 * CI/CD console point
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-21 15:41:00
 * @since
 */
@ShellComponent
public class CiCdConsole {
	final public static String GROUP = "Devops CI/CD console commands";

	/** {@link GlobalTimeoutJobCleanupCoordinator}. */
	@Autowired
	private GlobalTimeoutJobCleanupCoordinator coordinator;

	/** {@link TaskService}. */
	@Autowired
	private TaskService taskService;

	/**
	 * Reset timeout cleanup expression.
	 * 
	 * @param arg
	 * @return
	 */
	@ShellMethod(keys = "modifyCleanupInterval", group = GROUP, help = "Modifying global jobs timeout finalizer max-interval")
	public void modifyCleanupInterval(TimeoutCleanupIntervalArgument arg, ShellContext context) {
		try {
			context.printf(format("Modifying timeout cleanup finalizer intervalMs: <%s>", arg.getMaxIntervalMs()));

			// Refresh global timeoutCleanupFinalizer
			coordinator.refreshGlobalJobCleanMaxIntervalMs(arg.getMaxIntervalMs());

			context.printf(format("Modifyed timeoutCleanup finalizer of intervalMs:<%s>", arg.getMaxIntervalMs()));
		} catch (Exception e) {
			context.printf(format("Failed to timeoutCleanup finalizer intervalMs. cause by: %s", getStackTraceAsString(e)));
		} finally {
			context.completed();
		}
	}

	/**
	 * Get task pipeline list.
	 * 
	 * @param arg
	 * @return
	 */
	@ShellMethod(keys = "pipelineList", group = GROUP, help = "Pipeline tasks list.")
	public void pipelineList(TasksArgument arg, ShellContext context) {
		try {
			// Find tasks.
			PageModel pm = new PageModel(arg.getPageNum(), arg.getPageSize());
			taskService.list(pm, arg.getId(), arg.getTaskName(), arg.getGroupName(), arg.getBranchName(), arg.getTarType(),
					arg.getStartDate(), arg.getEndDate(), null);

			// Print write to console.
			context.printf(build(pm.getRecords()).setH('=').setV('!').getTableString());
		} catch (Exception e) {
			context.printf(format("Failed to find taskList. cause by: %s", getStackTraceAsString(e)));
		} finally {
			context.completed();
		}
	}

}