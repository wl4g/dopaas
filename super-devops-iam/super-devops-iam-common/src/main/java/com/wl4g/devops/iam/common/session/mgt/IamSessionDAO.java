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
package com.wl4g.devops.iam.common.session.mgt;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.support.redis.ScanCursor;
import com.wl4g.devops.support.redis.ScanCursor.CursorWrapper;

public interface IamSessionDAO extends SessionDAO {

	@Deprecated
	@Override
	default Collection<Session> getActiveSessions() {
		return null;
	}

	/**
	 * Gets access sessions
	 *
	 * @param limit
	 * @return
	 */
	ScanCursor<IamSession> getAccessSessions(final int limit);

	/**
	 * Gets access sessions
	 *
	 * @param cursorString
	 * @param limit
	 * @return
	 */
	ScanCursor<IamSession> getAccessSessions(final CursorWrapper cursor, final int limit);

	/**
	 * Gets access sessions by principal.
	 *
	 * @param principal
	 *            Getting active sessions based on logon objects
	 * @return
	 */
	default Set<IamSession> getAccessSessions(final Object principal) {
		return getAccessSessions(new CursorWrapper(), 200, principal);
	}

	/**
	 * Gets access sessions by principal.
	 *
	 * @param cursorString
	 * @param limit
	 * @param principal
	 *            Getting active sessions based on logon objects
	 * @return
	 */
	Set<IamSession> getAccessSessions(final CursorWrapper cursor, final int limit, final Object principal);

	/**
	 * Remove access current users
	 *
	 * @param principal
	 *            Removal of target users
	 */
	void removeAccessSession(Object principal);

}