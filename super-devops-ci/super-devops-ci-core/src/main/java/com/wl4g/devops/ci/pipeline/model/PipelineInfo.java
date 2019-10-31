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
package com.wl4g.devops.ci.pipeline.model;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

/**
 * Deploy information wrapper API.
 *
 * @author Wangl.sir
 * @version v1.0.0 2019-09-29
 * @since
 */
public interface PipelineInfo {

	Project getProject();

	void setProject(Project project);

	String getTarType();

	void setTarType(String tarType);

	String getPath();

	void setPath(String path);

	String getBranch();

	void setBranch(String branch);

	String getAlias();

	void setAlias(String alias);

	List<AppInstance> getInstances();

	void setInstances(List<AppInstance> instances);

	TaskHistory getTaskHistory();

	void setTaskHistory(TaskHistory taskHistory);

	TaskHistory getRefTaskHistory();

	void setRefTaskHistory(TaskHistory refTaskHistory);

	List<TaskHistoryDetail> getTaskHistoryDetails();

	void setTaskHistoryDetails(List<TaskHistoryDetail> taskHistoryDetails);

	String getTarName();

	void setTarName(String tarName);
}