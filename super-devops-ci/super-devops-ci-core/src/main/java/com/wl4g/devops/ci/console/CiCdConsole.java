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
import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.console.args.BuildArgument;
import com.wl4g.devops.ci.console.args.InstanceListArgument;
import com.wl4g.devops.ci.console.args.ModifyTimingTaskExpressionArgument;
import com.wl4g.devops.ci.console.args.TaskListArgument;
import com.wl4g.devops.ci.core.Pipeline;
import com.wl4g.devops.ci.pipeline.GlobalTimeoutJobCleanupFinalizer;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.bean.share.Environment;
import com.wl4g.devops.common.utils.lang.TableFormatters;
import com.wl4g.devops.common.utils.task.CronUtils;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.scm.AppClusterDao;
import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.LOCK_DEPENDENCY_BUILD;
import static com.wl4g.devops.shell.utils.ShellContextHolder.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

	@Autowired
	private CiCdProperties config;

	@Autowired
	private AppClusterDao appClusterDao;

	@Autowired
	private Pipeline pipelineCoreProcessor;

	@Autowired
	private JedisLockManager lockManager;

	@Autowired
	private GlobalTimeoutJobCleanupFinalizer timingTasks;

	@Autowired
	private TaskDao taskDao;

	@ShellMethod(keys = "expression", group = GROUP, help = "modify the expression of the timing task")
	public String modifyTimingTaskExpression(ModifyTimingTaskExpressionArgument argument) {
		String expression = argument.getExpression();
		// Open console printer.
		open();
		try {
			// Print to client
			printfQuietly(String.format("expression = <%s>", expression));
			if (CronUtils.isValidExpression(expression)) {
				timingTasks.resetTimeoutCheckerExpression(expression);
				printfQuietly(String.format("modify the success , expression = <%s>", expression));
			} else {
				printfQuietly(String.format("the expression is not valid , expression = <%s>", expression));
			}
		} catch (Exception e) {
			printfQuietly(String.format("modify the fail , expression = <%s>", expression));
			printfQuietly(e);
		} finally {
			// Close console printer.
			close();
		}

		return "Deployment task finished!";
	}

	@ShellMethod(keys = "taskList", group = GROUP, help = "get task list")
	public String taskList(TaskListArgument argument) {
		// Open console printer.
		open();
		try {
			// Print to client
			int pageNum = StringUtils.isNotBlank(argument.getPageNum()) ? Integer.valueOf(argument.getPageNum()) : 1;
			int pageSize = StringUtils.isNotBlank(argument.getPageSize()) ? Integer.valueOf(argument.getPageSize()) : 10;
			PageHelper.startPage(pageNum, pageSize, true);
			List<Task> list = taskDao.list(null, null, null, null, null, null, null);
			String result = TableFormatters.build(list).setH('=').setV('!').getTableString();
			return result;
		} catch (Exception e) {
			printfQuietly(e);
			throw e;
		} finally {
			// Close console printer.
			close();
		}
	}

	/**
	 * Execution deployments
	 */
	@ShellMethod(keys = "deploy", group = GROUP, help = "Execute application deployment")
	public String deploy(BuildArgument argument) {

		// Open console printer.
		open();

		Lock lock = lockManager.getLock(LOCK_DEPENDENCY_BUILD, config.getJob().getJobTimeout(), TimeUnit.MINUTES);
		try {
			if (lock.tryLock()) {
				// Print to client

				// Create async task
				// TODO 修改后与原有逻辑有差异，必须多一个环节，选task
				pipelineCoreProcessor.startup(argument.getTaskId());

			} else {
				printfQuietly("One Task is running ,Please try again later");
			}

		} catch (Exception e) {
			printfQuietly(e);
		} finally {
			// Close console printer.
			close();
			lock.unlock();
		}

		return "Deployment task finished!";
	}

	/**
	 * Got application groups list.
	 */
	@ShellMethod(keys = "list", group = GROUP, help = "Get a list of application information")
	public String list(InstanceListArgument argument) {
		StringBuffer result = new StringBuffer();

		String appGroupName = argument.getAppGroupName();
		String envName = argument.getEnvName();
		String r = argument.getAnyInstants();
		Pattern pattern = Pattern.compile(r);
		if (isBlank(appGroupName)) {
			List<AppCluster> apps = appClusterDao.grouplist();
			for (AppCluster appCluster : apps) {
				appendApp(result, appCluster, r);
				result.append("\n");
			}
		} else {
			AppCluster app = appClusterDao.getAppGroupByName(appGroupName);
			if (null == app) {
				return "AppCluster not exist";
			}
			List<Environment> environments = appClusterDao.environmentlist(app.getId().toString());
			if (null == environments || environments.size() <= 0) {
				return "no one env";
			}
			if (isBlank(envName)) {
				for (Environment environment : environments) {
					appendEnv(result, environment, r);
				}
			} else {
				Integer envId = null;
				for (Environment environment : environments) {
					if (environment.getName().equals(envName)) {
						envId = environment.getId();
						break;
					}
				}
				if (null == envId) {
					return "env name is wrong";
				}
				AppInstance appInstance = new AppInstance();
				appInstance.setEnvId(envId.toString());
				List<AppInstance> instances = appClusterDao.instancelist(appInstance);
				if (null == instances || instances.size() < 1) {
					return "none";
				}
				result.append(" ----- <").append(envName).append("> -----\n");
				result.append("\t[ID]    [HostAndPort]          [description]\n");
				for (int i = 0; i < instances.size() && i < 50; i++) {
					if (StringUtils.isBlank(r) || StringUtils.isNotBlank(r)
							&& pattern.matcher(instances.get(i).getHostname() + ":" + instances.get(i).getEndpoint()).matches()) {
						appendInstance(result, instances.get(i));
						if (i == 49) {
							result.append("\t......");
						}
					}
				}
			}
		}
		return result.toString();
	}

	private void appendApp(StringBuffer result, AppCluster appCluster, String r) {
		List<Environment> environments = appClusterDao.environmentlist(appCluster.getId().toString());
		if (environments == null || environments.size() <= 0) {
			return;
		}
		result.append(" <").append(appCluster.getName()).append(">:\n");
		for (Environment environment : environments) {
			appendEnv(result, environment, r);
		}
	}

	private void appendEnv(StringBuffer result, Environment environment, String r) {
		AppInstance appInstance = new AppInstance();
		appInstance.setEnvId(environment.getId().toString());
		List<AppInstance> instances = appClusterDao.instancelist(appInstance);

		if (null == instances || instances.size() <= 0) {
			return;
		}
		result.append(" ----- <").append(environment.getName()).append("> -----\n");
		result.append("\t[ID]    [HostAndPort]          [description]\n");

		Pattern pattern = Pattern.compile(r);
		for (int i = 0; i < instances.size() && i < 50; i++) {
			if (StringUtils.isBlank(r) || StringUtils.isNotBlank(r)
					&& pattern.matcher(instances.get(i).getHostname() + ":" + instances.get(i).getEndpoint()).matches()) {
				appendInstance(result, instances.get(i));
				if (i == 49) {
					result.append("\t......");
				}
			}
		}
	}

	private void appendInstance(StringBuffer result, AppInstance instance) {
		result.append("\t").append(formatCell(instance.getId().toString(), 8))
				.append(formatCell((instance.getHostname() + ":" + instance.getEndpoint()), 23)).append(instance.getRemark())
				.append("\n");
	}

	private String formatCell(String text, int width) {
		if (text != null && text.length() < width) {
			StringBuilder space = new StringBuilder();
			for (int i = 0; i < width - text.length(); i++) {
				space.append(" ");
			}
			text = text + space.toString();
		}
		return text;
	}

}