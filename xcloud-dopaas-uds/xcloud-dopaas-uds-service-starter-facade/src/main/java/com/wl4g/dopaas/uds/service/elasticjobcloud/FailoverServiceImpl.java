/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.dopaas.uds.service.elasticjobcloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shardingsphere.elasticjob.infra.context.TaskContext.MetaInfo;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.wl4g.dopaas.uds.service.elasticjobcloud.FailoverService;
import com.wl4g.dopaas.uds.service.elasticjobcloud.FailoverTaskInfo;
import com.wl4g.dopaas.uds.service.elasticjobcloud.StateNode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Failover service.
 */
@Service
public final class FailoverServiceImpl implements FailoverService {

	private @Autowired CoordinatorRegistryCenter regCenter;

	/**
	 * Get all failover tasks.
	 *
	 * @return all failover tasks
	 */
	public Map<String, Collection<FailoverTaskInfo>> getAllFailoverTasks() {
		if (!regCenter.isExisted(FailoverNodeUtil.ROOT)) {
			return Collections.emptyMap();
		}
		List<String> jobNames = regCenter.getChildrenKeys(FailoverNodeUtil.ROOT);
		Map<String, Collection<FailoverTaskInfo>> result = new HashMap<>(jobNames.size(), 1);
		for (String each : jobNames) {
			Collection<FailoverTaskInfo> failoverTasks = getFailoverTasks(each);
			if (!failoverTasks.isEmpty()) {
				result.put(each, failoverTasks);
			}
		}
		return result;
	}

	/**
	 * Get failover tasks.
	 *
	 * @param jobName
	 *            job name
	 * @return collection of failover tasks
	 */
	private Collection<FailoverTaskInfo> getFailoverTasks(final String jobName) {
		List<String> failOverTasks = regCenter.getChildrenKeys(FailoverNodeUtil.getFailoverJobNodePath(jobName));
		List<FailoverTaskInfo> result = new ArrayList<>(failOverTasks.size());
		for (String each : failOverTasks) {
			String originalTaskId = regCenter.get(FailoverNodeUtil.getFailoverTaskNodePath(each));
			if (!Strings.isNullOrEmpty(originalTaskId)) {
				result.add(new FailoverTaskInfo(MetaInfo.from(each), originalTaskId));
			}
		}
		return result;
	}

	/**
	 * Failover node.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	static final class FailoverNodeUtil {

		static final String ROOT = StateNode.ROOT + "/failover";

		private static final String FAILOVER_JOB = ROOT + "/%s";

		private static final String FAILOVER_TASK = FAILOVER_JOB + "/%s";

		static String getFailoverJobNodePath(final String jobName) {
			return String.format(FAILOVER_JOB, jobName);
		}

		static String getFailoverTaskNodePath(final String taskMetaInfo) {
			return String.format(FAILOVER_TASK, MetaInfo.from(taskMetaInfo).getJobName(), taskMetaInfo);
		}
	}

}
