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
package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.cli.SSH2Utils.CommandResult;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskHisBuildCommandDao;
import com.wl4g.devops.dao.ci.TaskSignDao;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.cli.ProcessManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.PROJECT_PATH;
import static com.wl4g.devops.common.utils.cli.SSH2Utils.executeWithCommand;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

/**
 * Abstract based deploy provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
public abstract class AbstractPipelineProvider implements PipelineProvider {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected JedisLockManager lockManager;
	@Autowired
	protected PipelineJobExecutor jobExecutor;
	@Autowired
	protected ProcessManager processManager;

	@Autowired
	protected DependencyService dependencyService;
	@Autowired
	protected TaskHistoryService taskHistoryService;
	@Autowired
	protected TaskHisBuildCommandDao taskHisBuildCommandDao;
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected TaskSignDao taskSignDao;

	/** Pipeline information. */
	final protected PipelineInfo pipelineInfo;

	protected String shaGit;
	protected String shaLocal;

	public AbstractPipelineProvider(PipelineInfo info) {
		this.pipelineInfo = info;
		String[] a = info.getProject().getTarPath().split("/");
		this.pipelineInfo.setTarName(a[a.length - 1]);
	}

	public PipelineInfo getPipelineInfo() {
		return pipelineInfo;
	}

	@Override
	public String getShaGit() {
		return shaGit;
	}

	@Override
	public String getShaLocal() {
		return shaLocal;
	}

	public void setShaGit(String shaGit) {
		this.shaGit = shaGit;
	}

	public void setShaLocal(String shaLocal) {
		this.shaLocal = shaLocal;
	}

	public TaskHistoryService getTaskHistoryService() {
		return taskHistoryService;
	}

	/**
	 * Exce command
	 */
	public String exceCommand(String targetHost, String userName, String command, String rsa) throws Exception {
		if (StringUtils.isBlank(command)) {
			return "command is blank";
		}

		// Obtain text-plain privateKey(RSA)
		String rsaKey = config.getTranform().getCipherKey();
		char[] rsaReal = new AES(rsaKey).decrypt(rsa).toCharArray();

		StringBuffer result = new StringBuffer(command);
		result.append("\n");
		//
		CommandResult ret = executeWithCommand(targetHost, userName, rsaReal, command);
		if (!isBlank(ret.getMessage())) {
			result.append(ret.getMessage());
		}
		result.append("-----------------");
		if (!isBlank(ret.getErrmsg())) {
			result.append(ret.getErrmsg());
		}
		return result.toString();
	}

	/**
	 * Mkdir
	 */
	public String mkdirs(String targetHost, String userName, String path, String rsa) throws Exception {
		String command = "mkdir -p " + path;
		return exceCommand(targetHost, userName, command, rsa);
	}

	protected String commandReplace(String command, String projectPath) {
		command.replaceAll("\\[", "\\[");
		command = command.replaceAll(PROJECT_PATH, projectPath);// projectPath
		// TODO ......
		return command;
	}

	/**
	 * Get Package Name from path
	 */
	protected String subPackname(String path) {
		String[] a = path.split("/");
		return a[a.length - 1];
	}

	/**
	 * Get Packname WithOut Postfix from path
	 */
	protected String subPacknameWithOutPostfix(String path) {
		String a = subPackname(path);
		return a.substring(0, a.lastIndexOf("."));
	}

	/**
	 * Do startup job execution.
	 */
	protected void doStartJobsExecute0() {
		// Create jobs.
		List<Runnable> jobs = getPipelineInfo().getInstances().stream().map(instance -> newPipelineJob(instance))
				.collect(toList());

		// Submit jobs for complete.
		if (!isEmpty(jobs)) {
			jobExecutor.submitForComplete(jobs, config.getTranform().getWaitCompleteTimeout());
		}

	}

	/**
	 * Create pipeline job.
	 * 
	 * @param instance
	 * @return
	 */
	protected abstract Runnable newPipelineJob(AppInstance instance);

}