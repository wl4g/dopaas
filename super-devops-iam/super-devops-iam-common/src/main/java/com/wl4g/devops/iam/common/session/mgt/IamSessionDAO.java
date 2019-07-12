/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.support.cache.ScanCursor;

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