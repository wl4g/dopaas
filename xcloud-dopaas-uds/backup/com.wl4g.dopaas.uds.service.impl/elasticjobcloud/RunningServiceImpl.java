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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.shardingsphere.elasticjob.infra.context.TaskContext;
import org.apache.shardingsphere.elasticjob.infra.context.TaskContext.MetaInfo;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Running service.
 */
@Service
public class RunningServiceImpl implements RunningService {

	private @Autowired CoordinatorRegistryCenter regCenter;
	private @Autowired CloudJobConfigService cloudJobConfigService;

	/**
	 * Get all running tasks.
	 *
	 * @return collection of all the running tasks
	 */
	public Map<String, Set<TaskContext>> getAllRunningTasks() {
		Map<String, Set<TaskContext>> result = new HashMap<>();
		List<String> jobKeys = regCenter.getChildrenKeys(RunningNode.ROOT);
		for (String each : jobKeys) {
			if (!cloudJobConfigService.load(each).isPresent()) {
				continue;
			}
			result.put(each,
					Sets.newCopyOnWriteArraySet(regCenter.getChildrenKeys(RunningNode.getRunningJobNodePath(each)).stream()
							.map(input -> TaskContext
									.from(regCenter.get(RunningNode.getRunningTaskNodePath(MetaInfo.from(input).toString()))))
							.collect(Collectors.toList())));
		}
		return result;
	}

	/**
	 * Running node.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	static final class RunningNode {

		static final String ROOT = StateNode.ROOT + "/running";

		private static final String RUNNING_JOB = ROOT + "/%s";

		private static final String RUNNING_TASK = RUNNING_JOB + "/%s";

		static String getRunningJobNodePath(final String jobName) {
			return String.format(RUNNING_JOB, jobName);
		}

		static String getRunningTaskNodePath(final String taskMetaInfo) {
			return String.format(RUNNING_TASK, MetaInfo.from(taskMetaInfo).getJobName(), taskMetaInfo);
		}
	}

}
