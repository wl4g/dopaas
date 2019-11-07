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
package com.wl4g.devops.iam.authc.pam;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhanced modular authentication processor that throws out exception handling
 * information
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月26日
 * @since
 */
public class ExceptionModularRealmAuthenticator extends ModularRealmAuthenticator {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
		AuthenticationStrategy strategy = getAuthenticationStrategy();
		AuthenticationInfo aggregate = strategy.beforeAllAttempts(realms, token);
		if (log.isTraceEnabled()) {
			log.trace("Iterating through {} realms for PAM authentication", realms.size());
		}

		for (Realm realm : realms) {
			aggregate = strategy.beforeAttempt(realm, token, aggregate);
			if (realm.supports(token)) {
				if (log.isTraceEnabled()) {
					log.trace("Attempting to authenticate token [{}] using realm [{}]", token, realm);
				}

				AuthenticationInfo info = null;
				Throwable t = null;
				try {
					info = realm.getAuthenticationInfo(token);
				} catch (Throwable throwable) {
					t = throwable;
					throw new AuthenticationException(t);
				} finally {
					aggregate = strategy.afterAttempt(realm, token, info, aggregate, t);
				}
			} else if (log.isDebugEnabled()) {
				log.debug("Realm [{}] does not support token {}.  Skipping realm.", realm, token);
			}
		}

		return strategy.afterAllAttempts(token, aggregate);
	}

}