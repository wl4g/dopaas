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
package com.wl4g.devops.ci.web;

import com.wl4g.components.core.bean.ci.model.HookInfo;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.flow.FlowManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GIT hook API controller.
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-05-05 15:37:00
 */
@RestController
@RequestMapping("/hook")
public class GitHookController extends BaseController {

	@Autowired
	private PipelineManager pipeliner;

	@Autowired
	private FlowManager flowManager;

	/**
	 * Receive GITLAB hook.
	 * 
	 * @param hook
	 * @throws Exception
	 */
	@RequestMapping("gitlab")
	public void gitlabHook(@RequestBody HookInfo hook) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Gitlab hook receive <= {}", hook);
		}
		String branchName = hook.getBranchName();
		// String url = hook.getRepository().getGitHttpUrl();
		String projectName = hook.getRepository().getName();
		pipeliner.hookPipeline(new HookParameter(projectName, branchName));
	}

	@RequestMapping(value = "/run")
	public void create(Integer taskId) {
		PipelineModel pipelineModel = flowManager.buildPipeline(taskId);
		pipeliner.runPipeline(new NewParameter(taskId, "create by hook", null, null, null),pipelineModel);
	}



}