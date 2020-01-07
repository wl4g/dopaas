package com.wl4g.devops.ci.pcm;

import java.util.List;

import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.common.web.model.SelectionModel;

import static com.wl4g.devops.ci.pcm.PcmOperator.PcmKind;

/**
 * Project collaboration management operator adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2020-01-03 14:10:00
 */
public interface PcmOperator extends Operator<PcmKind> {

	/**
	 * Get PCM project info list.
	 * 
	 * @param trackId
	 * @return
	 */
	List<SelectionModel> getProjects(Integer trackId);

	/**
	 * Get PCM user list.
	 * 
	 * @param trackId
	 * @return
	 */
	List<SelectionModel> getUsers(Integer trackId);

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
	List<SelectionModel> getIssues(Integer trackId, String userId, String projectId, String searchSubject);

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
