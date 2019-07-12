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

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

import com.wl4g.devops.iam.common.session.IamSession;

/**
 * {@code SessionFactory} implementation that generates {@link SimpleSession}
 * instances.
 *
 * @since 1.0
 */
public class IamSessionFactory implements SessionFactory {

	/**
	 * Creates a new {@link SimpleSession SimpleSession} instance retaining the
	 * context's {@link SessionContext#getHost() host} if one can be found.
	 *
	 * @param initData
	 *            the initialization data to be used during {@link Session}
	 *            creation.
	 * @return a new {@link SimpleSession SimpleSession} instance
	 */
	@Override
	public Session createSession(SessionContext initData) {
		if (initData != null) {
			String host = initData.getHost();
			if (host != null) {
				return new IamSession(host);
			}
		}
		return new IamSession();
	}

}