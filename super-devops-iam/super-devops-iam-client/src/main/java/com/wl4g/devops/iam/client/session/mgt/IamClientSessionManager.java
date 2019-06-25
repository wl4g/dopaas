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
package com.wl4g.devops.iam.client.session.mgt;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.session.Session;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_TICKET_C;
import static com.wl4g.devops.iam.client.filter.AbstractAuthenticationFilter.SAVE_GRANT_TICKET;

import com.wl4g.devops.common.bean.iam.model.SessionValidationAssertion;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.validation.IamValidator;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.session.mgt.AbstractIamSessionManager;
import com.wl4g.devops.iam.common.utils.Sessions;
import com.wl4g.devops.support.cache.ScanCursor;

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
	final private static int DEFAULT_BATCH_SIZE = 1000;

	/**
	 * Expire session validator
	 */
	final protected IamValidator<SessionValidationAssertion, SessionValidationAssertion> validator;

	public IamClientSessionManager(IamClientProperties config,
			IamValidator<SessionValidationAssertion, SessionValidationAssertion> validator) {
		super(config, CACHE_TICKET_C);
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
			ScanCursor<IamSession> cursor = this.sessionDAO.getActiveSessions(DEFAULT_BATCH_SIZE);
			while (cursor.hasNext()) {
				List<IamSession> activeSessions = cursor.readItem();

				// GrantTicket and session
				Map<String, Session> tmp = new HashMap<>(activeSessions.size());

				// Wrap to validation assertion
				SessionValidationAssertion request = new SessionValidationAssertion(config.getServiceName());
				for (IamSession session : activeSessions) {
					String grantTicket = (String) session.getAttribute(SAVE_GRANT_TICKET);
					request.getTickets().add(grantTicket);
					tmp.put(grantTicket, session);
				}

				// Validation execution
				SessionValidationAssertion assertion = this.validator.validate(request);
				for (String expiredTicket : assertion.getTickets()) {
					Session session = tmp.get(expiredTicket);
					try {
						this.sessionDAO.delete(session);
					} catch (Exception e) {
						log.warn("Cleaup expired session failed. sessionId: {}, grantTicket: {}", Sessions.getSessionId(session),
								expiredTicket);
					}
					if (log.isInfoEnabled()) {
						log.info("Cleaup expired session on: {}", Sessions.getSessionId(session));
					}
				}
			}

		} catch (Exception e) {
			log.error("Validating expire sessions failed", e);
		}

	}

}