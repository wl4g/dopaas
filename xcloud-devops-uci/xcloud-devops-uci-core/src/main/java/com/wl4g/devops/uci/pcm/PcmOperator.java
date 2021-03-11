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
package com.wl4g.devops.uci.pcm;

import com.google.common.annotations.Beta;
import com.wl4g.component.core.framework.operator.Operator;
import com.wl4g.component.core.bean.model.SelectionModel;
import com.wl4g.devops.common.bean.uci.Pcm;
import com.wl4g.devops.common.bean.uci.PipeHistoryPcm;

import java.util.List;

import static com.wl4g.devops.uci.pcm.PcmOperator.PcmKind;

/**
 * Project collaboration management operator adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2020-01-03 14:10:00
 */
@Beta
public interface PcmOperator extends Operator<PcmKind> {

	/**
	 * Get PCM project info list.
	 * 
	 * @param trackId
	 * @return
	 */
	List<SelectionModel> getProjects(Pcm pcm);

	/**
	 * Get PCM user list.
	 * 
	 * @param trackId
	 * @return
	 */
	List<SelectionModel> getUsers(Pcm pcm);

	/**
	 * Get Pcm Tracker
	 * 
	 * @param pcm
	 * @return
	 */
	List<SelectionModel> getTracker(Pcm pcm);

	/**
	 * Get Pcm priorities
	 * 
	 * @param pcm
	 * @return
	 */
	List<SelectionModel> getPriorities(Pcm pcm);

	/**
	 * Get Pcm Statuses
	 * 
	 * @param pcm
	 * @return
	 */
	List<SelectionModel> getStatuses(Pcm pcm);

	/**
	 * 
	 * Get PCM issue list.
	 * 
	 * @param trackId
	 * @param userId
	 * @param projectId
	 * @param searchSubject
	 * @return
	 */
	List<SelectionModel> getIssues(Pcm pcm, String userId, String projectId, String searchSubject);

	void createIssues(Pcm pcm, PipeHistoryPcm pipeStepPcm);

	/**
	 * Project collaboration management operator kind.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @author vjay
	 * @date 2020-01-03 14:41:00
	 */
	public static enum PcmKind {

		Redmine, // Apache redmine

		Jira;

		public static PcmKind of(String s) {
			PcmKind wh = safeOf(s);
			if (wh == null) {
				throw new IllegalArgumentException(String.format("Illegal PlatformType '%s'", s));
			}
			return wh;
		}

		public static PcmKind safeOf(String s) {
			for (PcmKind t : values()) {
				if (t.name().equalsIgnoreCase(s)) {
					return t;
				}
			}
			return null;
		}

	}

}