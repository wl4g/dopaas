/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.ci.deploy.provider;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

/**
 * Docker and k8s deploy provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class DockerK8sDeployProvider extends AbstractDeployProvider {

	public DockerK8sDeployProvider(Project project, String path, String branch, String alias, List<AppInstance> instances,
			TaskHistory taskHistory, TaskHistory refTaskHistory, List<TaskHistoryDetail> taskHistoryDetails) {
		super(project, path, branch, alias, instances, taskHistory, refTaskHistory, taskHistoryDetails);
	}

	@Override
	public void execute() throws Exception {

	}

	@Override
	public void rollback() throws Exception {

	}

}