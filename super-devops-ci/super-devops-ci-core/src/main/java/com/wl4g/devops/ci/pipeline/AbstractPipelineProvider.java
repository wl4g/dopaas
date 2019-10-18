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
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskHisBuildCommandDao;
import com.wl4g.devops.dao.ci.TaskSignDao;
import com.wl4g.devops.support.lock.SimpleRedisLockManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.PROJECT_PATH;

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
	protected DependencyService dependencyService;

	@Autowired
	protected SimpleRedisLockManager lockManager;

	@Autowired
	protected ProjectDao projectDao;

	@Autowired
	protected TaskSignDao taskSignDao;

	@Autowired
	protected PipelineJobExecutor pipelineTaskRunner;

	@Autowired
	protected TaskHisBuildCommandDao taskHisBuildCommandDao;

	protected PipelineInfo pipelineInfo;

	/**
	 * sha
	 */
	protected String shaGit;

	/**
	 * md5
	 */
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


	/**
	 * Execute
	 */
	public abstract void execute() throws Exception;

	/**
	 * Exce command
	 */
	public String exceCommand(String targetHost, String userName, String command, String rsa) throws Exception {
		if (StringUtils.isBlank(command)) {
			return "command is blank";
		}

		String rsaKey = config.getTranform().getCipherKey();
		AES aes = new AES(rsaKey);
		char[] rsaReal = aes.decrypt(rsa).toCharArray();
		String result = command + "\n";
		result += SSHTool.execute(targetHost, userName, command, rsaReal);
		return result;
	}

	/**
	 * Mkdir
	 */
	public String mkdirs(String targetHost, String userName, String path, String rsa) throws Exception {
		String command = "mkdir -p " + path;
		return exceCommand(targetHost, userName, command, rsa);
	}

	protected String commandReplace(String command, String projectPath) {
		command.replaceAll("\\[","\\[");
		command = command.replaceAll(PROJECT_PATH, projectPath);// projectPath
		// TODO ......
		return command;
	}

}