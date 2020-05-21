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
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import static com.wl4g.devops.iam.common.utils.IamAuthenticatingUtils.*;

import static java.lang.String.format;
import static java.util.UUID.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.Serializable;

/**
 * {@link SessionIdGenerator} that generates String values of JDK
 * {@link java.util.UUID}'s as the session IDs.
 *
 * @since 1.0
 */
public class IamUidSessionIdGenerator implements SessionIdGenerator {

	/**
	 * Application name.
	 */
	@Autowired
	protected AbstractIamProperties<? extends ParamProperties> config;

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
	@Override
	public Serializable generateId(Session session) {
		String idSuffix = generateDefaultTokenSuffix(config.getSpringApplicationName());
		return format("sid%s%s", randomUUID().toString().replaceAll("-", EMPTY), idSuffix);
	}

}