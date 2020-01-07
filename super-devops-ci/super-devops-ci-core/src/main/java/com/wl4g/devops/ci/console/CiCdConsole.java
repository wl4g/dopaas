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
import com.wl4g.devops.shell.processor.ShellHolder;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.shell.processor.ShellHolder.printf;
import static com.wl4g.devops.tool.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.devops.tool.common.lang.TableFormatters.*;

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
	private GlobalTimeoutJobCleanupCoordinator tjcCoordinator;

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
	public String modifyCleanupInterval(TimeoutCleanupIntervalArgument arg) {
		ShellHolder.open();
		try {
			printf(String.format("Modifying timeout cleanup finalizer intervalMs: <%s>", arg.getMaxIntervalMs()));
			// Refreshing global timeoutCleanupFinalizer
			tjcCoordinator.refreshGlobalJobCleanMaxIntervalMs(arg.getMaxIntervalMs());

			printf(String.format("Modifyed timeoutCleanup finalizer of intervalMs:<%s>", arg.getMaxIntervalMs()));
		} catch (Exception e) {
			printf(String.format("Failed to timeoutCleanup finalizer intervalMs. cause by: %s", getStackTraceAsString(e)));
		} finally {
			ShellHolder.close();
		}
		return "Reset timeoutCleanupFinalizer expression completed!";
	}

	/**
	 * Get task pipeline list.
	 * 
	 * @param arg
	 * @return
	 */
	@ShellMethod(keys = "pipelineList", group = GROUP, help = "Pipeline tasks list.")
	public String pipelineList(TasksArgument arg) {
		ShellHolder.open();
		try {
			// Find tasks.
			PageModel pm = new PageModel(arg.getPageNum(), arg.getPageSize());
			taskService.list(pm, arg.getId(), arg.getTaskName(), arg.getGroupName(), arg.getBranchName(), arg.getTarType(),
					arg.getStartDate(), arg.getEndDate(), null);

			// Print write to console.
			printf(build(pm.getRecords()).setH('=').setV('!').getTableString());
		} catch (Exception e) {
			printf(String.format("Failed to find taskList. cause by: %s", getStackTraceAsString(e)));
		} finally {
			ShellHolder.close();
		}

		return "Load pipeline task list completed!";
	}

}