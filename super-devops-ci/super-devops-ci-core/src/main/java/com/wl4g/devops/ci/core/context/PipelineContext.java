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
package com.wl4g.devops.ci.core.context;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

/**
 * Pipeline context with composite information wrapper.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-09-29
 * @since
 */
public abstract interface PipelineContext {

	/**
	 * The is empty for {@link PipelineContext} instance.
	 */
	final public static PipelineContext EMPTY = new PipelineContext() {

		@Override
		public List<TaskHistoryDetail> getTaskHistoryDetails() {
			throw new UnsupportedOperationException();
		}

		@Override
		public TaskHistory getTaskHistory() {
			throw new UnsupportedOperationException();
		}

		@Override
		public TaskHistory getRefTaskHistory() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getProjectSourceDir() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Project getProject() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<AppInstance> getInstances() {
			throw new UnsupportedOperationException();
		}

		@Override
		public AppCluster getAppCluster() {
			throw new UnsupportedOperationException();
		}
	};

	Project getProject();

	String getProjectSourceDir();

	AppCluster getAppCluster();

	List<AppInstance> getInstances();

	TaskHistory getTaskHistory();

	TaskHistory getRefTaskHistory();

	List<TaskHistoryDetail> getTaskHistoryDetails();

}