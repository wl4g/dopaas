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

import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppInstance;

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
		public List<PipelineHistoryInstance> getPipelineHistoryInstances() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PipelineModel getPipelineModel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PipeStepInstanceCommand getPipeStepInstanceCommand() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Pipeline getPipeline() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PipeStepNotification getPipeStepNotification() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PipeStepBuilding getPipeStepBuilding() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PipelineHistory getPipelineHistory() {
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

	/**
	 * Get current pipeline build project.
	 * 
	 * @return
	 */
	Project getProject();

	/**
	 * Get current pipeline build project.
	 * 
	 * @return
	 */
	String getProjectSourceDir();

	/**
	 * Get current pipeline build application cluster.
	 * 
	 * @return
	 */
	AppCluster getAppCluster();

	/**
	 * Get current pipeline build to remote instances.
	 * 
	 * @return
	 */
	List<AppInstance> getInstances();

	/**
	 * Get current pipeline build task history.
	 * 
	 * @return
	 */
	PipelineHistory getPipelineHistory();

	/**
	 * Get current pipeline task record instance.
	 * 
	 * @return
	 */
	List<PipelineHistoryInstance> getPipelineHistoryInstances();

	PipelineModel getPipelineModel();

	PipeStepInstanceCommand getPipeStepInstanceCommand();

	Pipeline getPipeline();

	PipeStepNotification getPipeStepNotification();

	PipeStepBuilding getPipeStepBuilding();

}