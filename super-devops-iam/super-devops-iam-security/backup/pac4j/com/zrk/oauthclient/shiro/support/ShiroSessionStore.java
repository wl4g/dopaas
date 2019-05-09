/*
 * Copyright 2015 the original author or authors.
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
package com.zrk.oauthclient.shiro.support;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specific session store.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class ShiroSessionStore implements SessionStore {

	private final static Logger log = LoggerFactory.getLogger(ShiroSessionStore.class);

	@Override
	public String getOrCreateSessionId(WebContext context) {
		return SecurityUtils.getSubject().getSession().getId().toString();
	}

	@Override
	public Object get(WebContext context, String key) {
		return SecurityUtils.getSubject().getSession().getAttribute(key);
	}

	@Override
	public void set(WebContext context, String key, Object value) {
		try {
			SecurityUtils.getSubject().getSession().setAttribute(key, value);
		} catch (final UnavailableSecurityManagerException e) {
			log.warn("Should happen just once at startup in some specific case of Shiro Spring configuration", e);
		}
	}
}