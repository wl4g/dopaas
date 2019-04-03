package com.wl4g.devops.iam.common.session.mgt;

import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.session.mgt.support.ScanCursor;

public interface IamSessionDAO extends SessionDAO {

	@Deprecated
	@Override
	default Collection<Session> getActiveSessions() {
		return null;
	}

	/**
	 * Get active sessions
	 * 
	 * @param cursor
	 * @param size
	 * @return
	 */
	public ScanCursor<IamSession> getActiveSessions(final int batchSize);

	/**
	 * Get active sessions
	 * 
	 * @param cursor
	 * @param size
	 * @param principal
	 *            Getting active sessions based on logon objects
	 * @return
	 */
	public ScanCursor<IamSession> getActiveSessions(final int batchSize, final Object principal);

	/**
	 * Remove active current users
	 * 
	 * @param principal
	 *            Removal of target users
	 */
	public void removeActiveSession(Object principal);

}
