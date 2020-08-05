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
package com.wl4g.devops.ci.pcm.jira;

import com.wl4g.components.core.bean.ci.Pcm;
import com.wl4g.components.core.bean.ci.PipeHistoryPcm;
import com.wl4g.components.core.web.model.SelectionModel;
import com.wl4g.devops.ci.pcm.AbstractPcmOperator;

import java.util.List;

/**
 * PCM API operator of jira.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月7日 v1.0.0
 * @see
 */
public class JiraPcmOperator extends AbstractPcmOperator {

	@Override
	public PcmKind kind() {
		return PcmKind.Jira;
	}

	@Override
	public List<SelectionModel> getProjects(Pcm pcm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SelectionModel> getUsers(Pcm pcm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SelectionModel> getTracker(Pcm pcm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SelectionModel> getPriorities(Pcm pcm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SelectionModel> getStatuses(Pcm pcm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SelectionModel> getIssues(Pcm pcm, String userId, String projectId, String searchSubject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createIssues(Pcm pcm, PipeHistoryPcm pipeHistoryPcm) {
		throw new UnsupportedOperationException();
	}

}