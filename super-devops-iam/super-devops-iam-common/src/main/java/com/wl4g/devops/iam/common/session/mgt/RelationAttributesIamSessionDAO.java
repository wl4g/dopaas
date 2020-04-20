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

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import com.wl4g.devops.iam.common.cache.IamCache;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Relational iam session DAO.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月16日
 * @since
 */
public abstract class RelationAttributesIamSessionDAO extends AbstractSessionDAO implements IamSessionDAO {
	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Iam config properties
	 */
	final protected AbstractIamProperties<? extends ParamProperties> config;

	/**
	 * {@link IamCacheManager}
	 */
	final protected IamCacheManager cacheManager;

	/**
	 * Relations attributes {@link IamCache}
	 */
	final protected IamCache relationAttrsCache;

	public RelationAttributesIamSessionDAO(AbstractIamProperties<? extends ParamProperties> config,
			IamCacheManager cacheManager) {
		notNullOf(config, "config");
		notNullOf(cacheManager, "cacheManager");
		this.config = config;
		this.cacheManager = cacheManager;
		this.relationAttrsCache = cacheManager.getIamCache(CACHE_RELATION_ATTRS);
	}

	@Override
	public Serializable create(Session session) {
		try {
			return super.create(session);
		} finally {
			// Sets relation cache.
			((IamSession) session).setRelationAttrsCache(relationAttrsCache);
		}
	}

	@Override
	public void update(final Session session) throws UnknownSessionException {
		if (isNull(session) || isNull(session.getId()))
			return;
		log.debug("Updating {}", session.getId());

		// Gets logged ID.
		// PrincipalCollection pc = (PrincipalCollection)
		// session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
		// String principalId = pc != null ?
		// pc.getPrimaryPrincipal().toString() : "";

		// Update session latest expiration time to timeout.
		doPutIamSession(session);
		awareRelationCache(session);
	}

	@Override
	public void delete(final Session session) {
		if (isNull(session) || isNull(session.getId()))
			return;

		log.debug("Deletion {} ", session.getId());
		doDeleteIamSession(session);
		awareRelationCache(session);

		// Remove all relation attributes.
		relationAttrsCache.mapRemoveAll();
	}

	@Override
	protected Session doReadSession(final Serializable sessionId) {
		if (isNull(sessionId))
			return null;

		log.debug("doReadSession {}", sessionId);
		Session session = doReadIamSession(sessionId);
		awareRelationCache(session);
		return session;
	}

	/**
	 * Aware sets relation cache
	 * 
	 * @param session
	 * @return
	 */
	protected Session awareRelationCache(final Session session) {
		// Sets relation cache.
		if (!isNull(session) && (session instanceof IamSession)) {
			((IamSession) session).setRelationAttrsCache(relationAttrsCache);
		}
		return session;
	}

	/**
	 * doPutIamSession
	 * 
	 * @param session
	 */
	protected abstract void doPutIamSession(final Session session);

	/**
	 * doDeleteIamSession
	 * 
	 * @param session
	 */
	protected abstract void doDeleteIamSession(final Session session);

	/**
	 * doReadIamSession
	 * 
	 * @param sessionId
	 * @return
	 */
	protected abstract Session doReadIamSession(final Serializable sessionId);

}