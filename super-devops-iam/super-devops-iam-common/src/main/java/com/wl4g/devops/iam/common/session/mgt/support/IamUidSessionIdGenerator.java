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
package com.wl4g.devops.iam.common.session.mgt.support;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import static java.lang.String.format;
import static java.util.UUID.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;

/**
 * {@link SessionIdGenerator} that generates String values of JDK
 * {@link java.util.UUID}'s as the session IDs.
 *
 * @since 1.0
 */
public class IamUidSessionIdGenerator implements SessionIdGenerator {

	/**
	 * Default generation id-suffix from application length.
	 */
	final private static int DEFAULT_SUFFIX_LEN = 3;

	/**
	 * Application name.
	 */
	final private String appName;

	public IamUidSessionIdGenerator(String appName) {
		notNull(appName, "appName");
		this.appName = appName;
	}

	/**
	 * Ignores the method argument and simply returns
	 * {@code UUID}.{@link java.util.UUID#randomUUID()
	 * randomUUID()}.{@code toString()}.
	 *
	 * @param session
	 *            the {@link Session} instance to which the ID will be applied.
	 * @return the String value of the JDK's next {@link UUID#randomUUID()
	 *         randomUUID()}.
	 */
	public Serializable generateId(Session session) {
		String appPrefix = (appName.length() > DEFAULT_SUFFIX_LEN) ? appName.substring(0, DEFAULT_SUFFIX_LEN) : appName;
		StringBuffer idSuffix = new StringBuffer(appPrefix.substring(0, 1));
		for (char ch : appPrefix.substring(1).toCharArray()) {
			idSuffix.append((int) ch);
		}
		return format("sid%s%s", randomUUID().toString().replaceAll("-", EMPTY), idSuffix);
	}

}