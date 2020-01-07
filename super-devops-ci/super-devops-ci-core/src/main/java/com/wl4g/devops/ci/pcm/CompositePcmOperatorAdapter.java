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
package com.wl4g.devops.ci.pcm;

import com.wl4g.devops.ci.pcm.PcmOperator.PcmKind;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.common.web.model.SelectionModel;

import java.util.List;

/**
 * Composite project collaboration management provider operator. (e.g.
 * redmine/jira etc.)
 * 
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @author vjay
 * @version 2020年1月6日 v1.0.0
 * @date 2020-01-03 14:47:00
 * @see
 */
public class CompositePcmOperatorAdapter extends GenericOperatorAdapter<PcmKind, PcmOperator> implements PcmOperator {

	public CompositePcmOperatorAdapter(List<PcmOperator> operators) {
		super(operators);
	}

	@Override
	public List<SelectionModel> getProjects(Integer trackId) {
		return getAdapted().getProjects(trackId);
	}

	@Override
	public List<SelectionModel> getUsers(Integer trackId) {
		return getAdapted().getUsers(trackId);
	}

	@Override
	public List<SelectionModel> getIssues(Integer trackId, String userId, String projectId, String search) {
		return getAdapted().getIssues(trackId, userId, projectId, search);
	}

}