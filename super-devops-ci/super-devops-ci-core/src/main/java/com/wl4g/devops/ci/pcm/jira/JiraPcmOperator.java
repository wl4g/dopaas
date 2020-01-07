package com.wl4g.devops.ci.pcm.jira;

import java.util.List;

import com.wl4g.devops.ci.pcm.AbstractPcmOperator;
import com.wl4g.devops.common.web.model.SelectionModel;

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
	public List<SelectionModel> getProjects(Integer trackId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SelectionModel> getUsers(Integer trackId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SelectionModel> getIssues(Integer trackId, String userId, String projectId, String searchSubject) {
		throw new UnsupportedOperationException();
	}

}
