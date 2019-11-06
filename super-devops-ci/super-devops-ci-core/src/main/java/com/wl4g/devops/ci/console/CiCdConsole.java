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

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.console.args.BuildArgument;
import com.wl4g.devops.ci.console.args.TimeoutCleanupIntervalMsArgument;
import com.wl4g.devops.ci.console.args.TaskListArgument;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.pipeline.GlobalTimeoutJobCleanupFinalizer;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.utils.lang.TableFormatters;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.processor.ShellHolder;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.wl4g.devops.common.utils.Exceptions.getStackTraceAsString;
import static com.wl4g.devops.shell.processor.ShellHolder.*;

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

	/** {@link GlobalTimeoutJobCleanupFinalizer}. */
	@Autowired
	private GlobalTimeoutJobCleanupFinalizer finalizer;

	/** {@link PipelineManager}. */
	@Autowired
	private PipelineManager pipeManager;

	/** {@link TaskDao}. */
	@Autowired
	private TaskDao taskDao;

	/**
	 * Reset timeout cleanup expression.
	 * 
	 * @param arg
	 * @return
	 */
	@ShellMethod(keys = "modifyCleanupInterval", group = GROUP, help = "Modifying global jobs timeout finalizer max-interval")
	public String modifyCleanupInterval(TimeoutCleanupIntervalMsArgument arg) {
		ShellHolder.open();
		try {
			printf(String.format("Modifying timeout cleanup finalizer intervalMs: <%s>", arg.getMaxIntervalMs()));
			// Refreshing global timeoutCleanupFinalizer
			finalizer.refreshGlobalJobCleanMaxIntervalMs(arg.getMaxIntervalMs());

			printf(String.format("Modifyed timeoutCleanup finalizer of intervalMs:<%s>", arg.getMaxIntervalMs()));
		} catch (Exception e) {
			printf(String.format("Failed to timeoutCleanup finalizer intervalMs. cause by: %s", getStackTraceAsString(e)));
		} finally {
			ShellHolder.close();
		}
		return "Reset timeoutCleanupFinalizer expression completed!";
	}

	/**
	 * Get task list.
	 * 
	 * @param arg
	 * @return
	 */
	@ShellMethod(keys = "pipelineList", group = GROUP, help = "Pipeline tasks list.")
	public String getPipelineList(TaskListArgument arg) {
		ShellHolder.open();
		try {
			// Setup pagers.
			PageHelper.startPage(arg.getPageNum(), arg.getPageSize(), true);

			// Print to client
			List<Task> list = taskDao.list(null, null, null, null, null, null, null);
			return TableFormatters.build(list).setH('=').setV('!').getTableString();
		} catch (Exception e) {
			printf(String.format("Failed to find taskList. cause by: %s", getStackTraceAsString(e)));
		} finally {
			ShellHolder.close();
		}

		return "Load pipeline task list completed!";
	}

	/**
	 * New pipeline task deployement.
	 * 
	 * @param arg
	 * @return
	 */
	@ShellMethod(keys = "deploy", group = GROUP, help = "Deployment of pipeline job")
	public String deploy(BuildArgument arg) {
		ShellHolder.open();
		try {
			pipeManager.newPipeline(arg.getTaskId());
		} catch (Exception e) {
			printf(String.format("Failed to pipeline job. cause by: %s", getStackTraceAsString(e)));
		} finally {
			ShellHolder.close();
		}
		return "Deployment pipeline completed!";
	}

}