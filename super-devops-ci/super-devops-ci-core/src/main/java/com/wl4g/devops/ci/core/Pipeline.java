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
package com.wl4g.devops.ci.core;

import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.bean.share.Environment;
import com.wl4g.devops.common.utils.io.FileIOUtils;

import java.util.List;

/**
 * CICD pipeline core entry processor.
 * 
 * @author vjay
 * @author Wangl.sir <983708408@qq.com>
 * @date 2019-05-16 14:45:00
 */
public interface Pipeline {

	List<AppCluster> grouplist();

	List<Environment> environmentlist(String clusterId);

	List<AppInstance> instancelist(AppInstance appInstance);

	void hook(String projectName, String branchName, String url);

	void createTask(Integer taskId);

	void createRollbackTask(Integer taskId);

	FileIOUtils.ReadResult readLog(Integer taskHisId, Integer index, Integer size);

}