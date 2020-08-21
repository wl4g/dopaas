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

import com.wl4g.components.core.web.BaseController;
import com.wl4g.devops.ci.common.hook.HookInfo;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.utils.HookCommandHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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

		//HookInfoGitlab a  = JacksonUtils.parseJSON(hook,HookInfoGitlab.class);
		log.info("Gitlab hook receive <= {}", hook);

		HookCommandHolder.HookCommand hookCommand = null;
		List<HookInfo.Commits> commits = hook.getCommits();
		for(HookInfo.Commits commit : commits){
			hookCommand = HookCommandHolder.parse(commit.getMessage());
			if(Objects.nonNull(hookCommand)){
				break;
			}
		}
		if(Objects.isNull(hookCommand)){
			return;
		}

		if(hookCommand instanceof HookCommandHolder.DeployCommand){
			HookCommandHolder.DeployCommand deployCommand = (HookCommandHolder.DeployCommand)hookCommand;
			System.out.println(deployCommand.getEnv());
		}

		//String branchName = hook.getBranchName();
		//String projectName = hook.getRepository().getName();
		pipeliner.hookPipeline(hookCommand);
	}

	/*@RequestMapping(value = "/run")
	public void create(Integer taskId) {
		PipelineModel pipelineModel = flowManager.buildPipeline(taskId);
		pipeliner.runPipeline(new NewParameter(taskId, "create by hook", null, null, null),pipelineModel);
	}*/



}