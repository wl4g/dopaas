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
import com.wl4g.devops.ci.common.hook.GitlabHookInfo;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.utils.HookCommandHolder;
import com.wl4g.devops.ci.utils.HookCommandHolder.DeployCommand;
import com.wl4g.devops.ci.utils.HookCommandHolder.HookCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;

/**
 * GIT hook API controller.
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-05-05 15:37:00
 */
@RestController
@RequestMapping("/hook")
public class VcsHookController extends BaseController {

	@Autowired
	private PipelineManager pipeliner;

	/**
	 * Receive GITLAB hook.
	 *
	 * @param hook
	 * @throws Exception
	 */
	@RequestMapping("gitlab")
	public void receiveGitlabHook(@RequestBody GitlabHookInfo hook) throws Exception {
		log.info("Gitlab hook receive <= {}", toJSONString(hook));

		HookCommand hookCommand = null;
		List<GitlabHookInfo.Commits> commits = hook.getCommits();
		for (GitlabHookInfo.Commits commit : commits) {
			hookCommand = HookCommandHolder.parse(commit.getMessage());
			if (nonNull(hookCommand)) {
				break;
			}
		}
		if (isNull(hookCommand)) {
			return;
		}
		if (hookCommand instanceof DeployCommand) {
			DeployCommand deployCommand = (DeployCommand) hookCommand;
			System.out.println(deployCommand.getEnv());
		}

		pipeliner.hookPipeline(hookCommand);
	}

}