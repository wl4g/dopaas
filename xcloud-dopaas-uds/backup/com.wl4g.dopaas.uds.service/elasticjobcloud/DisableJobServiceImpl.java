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

import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.dopaas.uds.service.elasticjobcloud.config.JobStateProperties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Disable job service.
 */
@Slf4j
@Service
public class DisableJobServiceImpl implements DisableJobService {

	private @Autowired CoordinatorRegistryCenter regCenter;
	private @Autowired JobStateProperties jobStateProperties;

	/**
	 * Add job to the disable queue.
	 *
	 * @param jobName
	 *            job name
	 */
	public void add(final String jobName) {
		if (regCenter.getNumChildren(DisableJobNode.ROOT) > jobStateProperties.getQueueSize()) {
			log.warn("Cannot add disable job, caused by read state queue size is larger than {}.",
					jobStateProperties.getQueueSize());
			return;
		}
		String disableJobNodePath = DisableJobNode.getDisableJobNodePath(jobName);
		if (!regCenter.isExisted(disableJobNodePath)) {
			regCenter.persist(disableJobNodePath, jobName);
		}
	}

	/**
	 * Remove the job from the disable queue.
	 *
	 * @param jobName
	 *            job name
	 */
	public void remove(final String jobName) {
		regCenter.remove(DisableJobNode.getDisableJobNodePath(jobName));
	}

	/**
	 * Determine whether the job is in the disable queue or not.
	 *
	 * @param jobName
	 *            job name
	 * @return true is in the disable queue, otherwise not
	 */
	public boolean isDisabled(final String jobName) {
		return regCenter.isExisted(DisableJobNode.getDisableJobNodePath(jobName));
	}

	/**
	 * Disable job node.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	static final class DisableJobNode {

		static final String ROOT = StateNode.ROOT + "/disable/job";

		private static final String DISABLE_JOB = ROOT + "/%s";

		static String getDisableJobNodePath(final String jobName) {
			return String.format(DISABLE_JOB, jobName);
		}
	}

}
