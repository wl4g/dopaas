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
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SESSION;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.base.Charsets;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.common.cache.JedisIamCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.redis.ScanCursor;
import com.wl4g.devops.support.redis.ScanCursor.CursorWrapper;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanParams;

/**
 * Redis shiro session DAO.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月28日
 * @since
 */
public class JedisIamSessionDAO extends RelationAttributesIamSessionDAO {

	/**
	 * Distributed locks.
	 */
	@Autowired
	protected JedisLockManager lockManager;

	public JedisIamSessionDAO(AbstractIamProperties<? extends ParamProperties> config, IamCacheManager cacheManager) {
		super(config, cacheManager);
	}

	@Override
	public ScanCursor<IamSession> getAccessSessions(final int limit) {
		return getAccessSessions(new CursorWrapper(), 100);
	}

	@Override
	public ScanCursor<IamSession> getAccessSessions(final CursorWrapper cursor, int limit) {
		isTrue(limit > 0, "accessSessions batchSize must >0");
		byte[] match = (config.getCache().getPrefix() + CACHE_SESSION + "*").getBytes(Charsets.UTF_8);
		ScanParams params = new ScanParams().count(limit).match(match);
		JedisCluster jedisCluster = ((JedisIamCacheManager) cacheManager).getJedisCluster();
		return new ScanCursor<IamSession>(jedisCluster, cursor, IamSession.class, params) {
			@Override
			public synchronized IamSession next() {
				IamSession s = super.next();
				awareRelationCache(s);
				return s;
			}
		}.open();
	}

	@Override
	public Set<IamSession> getAccessSessions(final CursorWrapper cursor, final int limit, final Object principal) {
		Set<IamSession> principalSessions = new HashSet<>(4);
		ScanCursor<IamSession> sc = getAccessSessions(cursor, limit);
		while (sc.hasNext()) {
			IamSession s = sc.next();
			if (nonNull(s)) {
				awareRelationCache(s);
				Object primaryPrincipal = s.getPrimaryPrincipal();
				if (nonNull(primaryPrincipal) && primaryPrincipal.equals(principal)) {
					principalSessions.add(s);
				}
			}
		}
		return principalSessions;
	}

	@Override
	public void removeAccessSession(Object principal) {
		log.debug("removeActiveSession principal: {} ", principal);

		Set<IamSession> sessions = getAccessSessions(principal);
		if (!isEmpty(sessions)) {
			for (IamSession s : sessions) {
				delete(s);
				log.debug("Removed iam session for principal: {}, session: {}", principal, s);
			}
		}
	}

	@Override
	protected Serializable doCreate(Session session) {
		log.debug("doCreate {}", session.getId());
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		update(session);
		return sessionId;
	}

	@Override
	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		log.debug("readSession {}", sessionId);
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

	@Override
	protected void doPutIamSession(Session session) {
		// Update session latest expiration time to timeout.
		cacheManager.getIamCache(CACHE_SESSION).put(new CacheKey(session.getId(), session.getTimeout()), session);
	}

	@Override
	protected void doDeleteIamSession(Session session) {
		cacheManager.getIamCache(CACHE_SESSION).remove(new CacheKey(session.getId()));
	}

	@Override
	protected Session doReadIamSession(Serializable sessionId) {
		return (Session) cacheManager.getIamCache(CACHE_SESSION).get(new CacheKey(sessionId, IamSession.class));
	}

}