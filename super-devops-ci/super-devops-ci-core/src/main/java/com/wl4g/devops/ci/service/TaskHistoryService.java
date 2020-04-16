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
package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskBuildCommand;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.page.PageModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:43:00
 */
@Component
public interface TaskHistoryService {

	PageModel list(PageModel pm, String groupName, String projectName, String branchName, String startDate, String endDate,
			String envType);

	List<TaskHistoryInstance> getDetailByTaskId(Integer id);

	TaskHistory getById(Integer id);

	TaskHistory createTaskHistory(Project project, List<AppInstance> instances, int type, int status, String branchName,
			String sha, Integer parentId, String buildCommand, String preCommand, String postCommand, String tarType, String branchType,
			Integer contactGroupId, List<TaskBuildCommand> taskBuildCommands, String trackId, Integer trackType, String remark,
			String envType, String annex,String parentAppHome,String assetsPath);

	void updateStatus(int taskId, int status);

	void updateStatusAndResult(int taskId, int status, String result);

	void updateDetailStatus(int taskDetailId, int status);

	void updateStatusAndResultAndSha(int taskId, int status, String result, String sha, String md5);

	void stopByTaskHisId(Integer taskHisId);

	void updateCostTime(int taskId, long costTime);

}