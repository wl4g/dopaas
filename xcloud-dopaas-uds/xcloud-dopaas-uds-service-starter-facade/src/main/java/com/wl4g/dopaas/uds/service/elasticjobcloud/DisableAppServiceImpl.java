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

import com.wl4g.dopaas.uds.service.elasticjobcloud.DisableAppService;
import com.wl4g.dopaas.uds.service.elasticjobcloud.StateNode;
import com.wl4g.dopaas.uds.service.elasticjobcloud.config.JobStateConfiguration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Disable app service.
 */
@Slf4j
@Service
public class DisableAppServiceImpl implements DisableAppService {

	@Autowired
	private CoordinatorRegistryCenter regCenter;

	@Autowired
	private JobStateConfiguration jobStateConfiguration;

	/**
	 * Add application name to disable queue.
	 *
	 * @param appName
	 *            application name
	 */
	public void add(final String appName) {
		if (regCenter.getNumChildren(DisableAppNode.ROOT) > jobStateConfiguration.getQueueSize()) {
			log.warn("Cannot add disable app, caused by read state queue size is larger than {}.",
					jobStateConfiguration.getQueueSize());
			return;
		}
		String disableAppNodePath = DisableAppNode.getDisableAppNodePath(appName);
		if (!regCenter.isExisted(disableAppNodePath)) {
			regCenter.persist(disableAppNodePath, appName);
		}
	}

	/**
	 * Remove application name from disable queue.
	 *
	 * @param appName
	 *            application name
	 */
	public void remove(final String appName) {
		regCenter.remove(DisableAppNode.getDisableAppNodePath(appName));
	}

	/**
	 * Check whether the application name is disabled or not.
	 *
	 * @param appName
	 *            application name
	 * @return true is in the disable queue, otherwise not
	 */
	public boolean isDisabled(final String appName) {
		return regCenter.isExisted(DisableAppNode.getDisableAppNodePath(appName));
	}

	/**
	 * Disable app node.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	static final class DisableAppNode {

		static final String ROOT = StateNode.ROOT + "/disable/app";

		private static final String DISABLE_APP = ROOT + "/%s";

		static String getDisableAppNodePath(final String appName) {
			return String.format(DISABLE_APP, appName);
		}
	}

}
