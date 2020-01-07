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
