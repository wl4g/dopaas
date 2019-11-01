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

import java.io.Serializable;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SESSION;
import static org.springframework.util.Assert.isTrue;

import com.google.common.base.Charsets;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.support.cache.ScanCursor;

import redis.clients.jedis.ScanParams;

/**
 * Redis shiro session DAO.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月28日
 * @since
 */
public class JedisIamSessionDAO extends AbstractSessionDAO implements IamSessionDAO {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * IAM properties
	 */
	final private AbstractIamProperties<? extends ParamProperties> config;

	/**
	 * Jedis cache manager.
	 */
	final private JedisCacheManager cacheManager;

	public JedisIamSessionDAO(AbstractIamProperties<? extends ParamProperties> config, JedisCacheManager cacheManager) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(cacheManager, "'cacheManager' must not be null");
		this.config = config;
		this.cacheManager = cacheManager;
	}

	@Override
	public void update(final Session session) throws UnknownSessionException {
		if (session == null || session.getId() == null) {
			return;
		}
		// Get logged ID
		// PrincipalCollection pc = (PrincipalCollection)
		// session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
		// String principalId = pc != null ?
		// pc.getPrimaryPrincipal().toString() : "";

		if (log.isDebugEnabled()) {
			log.debug("update {}", session.getId());
		}

		/**
		 * Update session latest expiration time to timeout time
		 */
		this.cacheManager.getEnhancedCache(CACHE_SESSION).put(new EnhancedKey(session.getId(), session.getTimeout()), session);
	}

	@Override
	public void delete(final Session session) {
		if (session == null || session.getId() == null) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("delete {} ", session.getId());
		}
		this.cacheManager.getEnhancedCache(CACHE_SESSION).remove(new EnhancedKey(session.getId()));
	}

	@Override
	public ScanCursor<IamSession> getActiveSessions(final int batchSize) {
		return this.getActiveSessions(batchSize, null);
	}

	@Override
	public ScanCursor<IamSession> getActiveSessions(final int batchSize, @Deprecated final Object principal) {
		isTrue(batchSize > 0, "activeSessions batchSize must >0");

		byte[] match = (config.getCache().getPrefix() + CACHE_SESSION + "*").getBytes(Charsets.UTF_8);
		ScanParams params = new ScanParams().count(batchSize).match(match);
		return new ScanCursor<IamSession>(cacheManager.getJedisCluster(), params) {
		}.open();
	}

	@Override
	public void removeActiveSession(Object principal) {
		if (log.isDebugEnabled()) {
			log.debug("removeActiveSession principal: {} ", principal);
		}

		ScanCursor<IamSession> cursor = this.getActiveSessions(200, principal).open();
		while (cursor.hasNext()) {
			delete(cursor.next());
		}
	}

	@Override
	protected Serializable doCreate(Session session) {
		if (log.isDebugEnabled()) {
			log.debug("doCreate {}", session.getId());
		}
		Serializable sessionId = this.generateSessionId(session);
		this.assignSessionId(session, sessionId);
		this.update(session);
		return sessionId;
	}

	@Override
	protected Session doReadSession(final Serializable sessionId) {
		if (sessionId == null) {
			return null;
		}
		if (log.isDebugEnabled()) {
			log.debug("doReadSession {}", sessionId);
		}
		return (Session) this.cacheManager.getEnhancedCache(CACHE_SESSION).get(new EnhancedKey(sessionId, IamSession.class));
	}

	@Override
	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		if (log.isDebugEnabled()) {
			log.debug("readSession {}", sessionId);
		}
		try {
			return super.readSession(sessionId);
		} catch (UnknownSessionException e) {
			return null;
		}
	}

	@Override
	public void assignSessionId(Session session, Serializable sessionId) {
		((IamSession) session).setId((String) sessionId);
	}

}