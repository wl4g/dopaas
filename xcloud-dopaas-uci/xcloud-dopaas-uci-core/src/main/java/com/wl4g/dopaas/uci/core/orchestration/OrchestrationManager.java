/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.core.orchestration;

import com.wl4g.dopaas.common.bean.uci.Orchestration;
import com.wl4g.dopaas.common.bean.uci.OrchestrationHistory;
import com.wl4g.dopaas.common.bean.uci.OrchestrationPipeline;
import com.wl4g.dopaas.common.bean.uci.model.PipelineModel;
import com.wl4g.dopaas.common.bean.uci.model.RunModel;
import java.util.*;

/**
 * Flow pipelines manager.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2020-03-22
 * @sine v1.0
 * @see
 */
public interface OrchestrationManager {

	/**
	 * Start to run orchestration
	 *
	 * @param orchestration
	 */
	public void runOrchestration(Orchestration orchestration, String remark, String taskTraceId, String taskTraceType,
			String annex);

	/**
	 * Step 1 : Sort By Priority
	 */
	public List<List<OrchestrationPipeline>> sortByPriority(List<OrchestrationPipeline> orchestrationPipelines);

	/**
	 * Step 2 : build flow and it's childs(pipelines)
	 *
	 * @param orchestrationId
	 * @param list
	 * @return
	 */
	public List<List<PipelineModel>> buildFlow(Long orchestrationId, List<List<OrchestrationPipeline>> list, RunModel runModel);

	/**
	 * Step 3 : Hand out
	 *
	 * @param lists
	 */
	public void handOut(OrchestrationHistory orchestrationHistory, List<List<PipelineModel>> pipelineModelSort, RunModel runModel,
			String remark, String taskTraceId, String taskTraceType, String annex) throws Exception;

	public void master2slave(OrchestrationHistory orchestrationHistory, PipelineModel pipelineModel, String remark,
			String taskTraceId, String taskTraceType, String annex) throws Exception;

	/**
	 * for single pipeline
	 *
	 * @param pipelineId
	 * @return
	 */
	// TODO 这里需要添加redis锁（）jedisService.setMap()
	public PipelineModel buildPipeline(Long pipelineId);

	/**
	 * when pipeline state change , call this method
	 */
	public void pipelineStateChange(PipelineModel pipelineModel);

	/**
	 * when pipeline finish, del the pipeline from runModel
	 */
	public void pipelineComplete(String runId);

	public void pipelineCompleteFocus(String runId);

	/**
	 * whebn flow is complete,
	 *
	 * @param runId
	 */
	public void flowComplete(RunModel runModel, boolean isAllSuccess);

	public static enum FlowStatus {
		WAITING, RUNNING, RUNNING_BUILD, RUNNING_DEPLOY, FAILED, SUCCESS;

		/**
		 * Converter string to {@link FlowStatus}
		 *
		 * @param stauts
		 * @return
		 */
		public static FlowStatus of(String stauts) {
			FlowStatus wh = safeOf(stauts);
			if (wh == null) {
				throw new IllegalArgumentException(String.format("Illegal action '%s'", stauts));
			}
			return wh;
		}

		/**
		 * Safe converter string to {@link FlowStatus}
		 *
		 * @param stauts
		 * @return
		 */
		public static FlowStatus safeOf(String stauts) {
			for (FlowStatus t : values()) {
				if (String.valueOf(stauts).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}
	}

}