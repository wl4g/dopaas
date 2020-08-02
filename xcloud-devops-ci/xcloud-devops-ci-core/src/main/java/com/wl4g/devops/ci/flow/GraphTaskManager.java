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
package com.wl4g.devops.ci.flow;

import com.wl4g.devops.ci.bean.GraphTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.wl4g.devops.ci.bean.GraphTask.Project;

/**
 * @author vjay
 * @date 2020-05-15 10:37:00
 */
public class GraphTaskManager {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	public void add(Project project) {

	}

	/**
	 * Get All Graph Task
	 * 
	 * @return
	 */
	public List<GraphTask> getGraphTasks() {
		// TODO get from redis
		return null;
	}

	/**
	 * For Step Build
	 */
	public static enum ProjectStatus {

		WAITING, BUILDING, SUCCESS, FAILED;

		/**
		 * Converter string to {@link FlowManager.FlowStatus}
		 *
		 * @param stauts
		 * @return
		 */
		public static ProjectStatus of(String stauts) {
			ProjectStatus wh = safeOf(stauts);
			if (wh == null) {
				throw new IllegalArgumentException(String.format("Illegal action '%s'", stauts));
			}
			return wh;
		}

		/**
		 * Safe converter string to {@link FlowManager.FlowStatus}
		 *
		 * @param stauts
		 * @return
		 */
		public static ProjectStatus safeOf(String stauts) {
			for (ProjectStatus t : values()) {
				if (String.valueOf(stauts).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}

	}
}