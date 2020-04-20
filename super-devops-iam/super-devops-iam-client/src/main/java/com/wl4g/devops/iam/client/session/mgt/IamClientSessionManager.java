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
package com.wl4g.devops.iam.client.session.mgt;

import static java.util.Objects.nonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.session.Session;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_TICKET_C;
import static com.wl4g.devops.iam.client.filter.AbstractAuthenticationFilter.SAVE_GRANT_TICKET;

import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.validation.IamValidator;
import com.wl4g.devops.iam.common.authc.model.SessionValidityAssertModel;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.session.mgt.AbstractIamSessionManager;
import com.wl4g.devops.support.redis.ScanCursor;

/**
 * IAM client session manager
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class IamClientSessionManager extends AbstractIamSessionManager<IamClientProperties> {

	/**
	 * Iteration batch size.
	 */
	final private static int DEFAULT_BATCH_SIZE = 100;

	/**
	 * Expire session validator
	 */
	final protected IamValidator<SessionValidityAssertModel, SessionValidityAssertModel> validator;

	public IamClientSessionManager(IamClientProperties config, IamCacheManager cacheManager,
			IamValidator<SessionValidityAssertModel, SessionValidityAssertModel> validator) {
		super(config, cacheManager, CACHE_TICKET_C);
		this.validator = validator;
	}

	@Override
	protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
		return super.getSessionId(request, response);
	}

	@Override
	public void validateSessions() {
		if (log.isInfoEnabled()) {
			log.info("Validating all active sessions...");
		}

		try {
			ScanCursor<IamSession> cursor = sessionDAO.getAccessSessions(DEFAULT_BATCH_SIZE);
			while (cursor.hasNext()) {
				List<IamSession> activeSessions = cursor.readValues();

				// Grant ticket of local sessions.
				Map<String, Session> localSessions = new HashMap<>(activeSessions.size());

				// Wrap to validation assertion
				SessionValidityAssertModel request = new SessionValidityAssertModel(config.getServiceName());
				for (IamSession session : activeSessions) {
					String grantTicket = (String) session.getAttribute(SAVE_GRANT_TICKET);
					request.getTickets().add(grantTicket);
					localSessions.put(grantTicket, session);
				}

				// Validation sessions.
				SessionValidityAssertModel assertion = validator.validate(request);
				for (String deadTicket : assertion.getTickets()) {
					Session session = localSessions.get(deadTicket);
					try {
						if (nonNull(session)) {
							sessionDAO.delete(session);
							if (log.isInfoEnabled()) {
								log.info("Cleaup expired session on: {}", session.getId());
							}
						}
					} catch (Exception e) {
						log.warn("Cleaup expired session failed. sessionId: {}, grantTicket: {}", session.getId(), deadTicket);
					}
				}
			}
		} catch (Exception e) {
			log.error("Validating expire sessions failed", e);
		}
	}

}