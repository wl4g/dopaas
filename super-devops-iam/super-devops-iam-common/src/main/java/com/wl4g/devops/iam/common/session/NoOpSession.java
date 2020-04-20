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
package com.wl4g.devops.iam.common.session;

import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;

/**
 * No operation session. {@link NoOpSession}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月25日
 * @since
 */
public class NoOpSession implements Session {

	/**
	 * Default no operation session instance. {@link NoOpSession}
	 */
	final public static Session DefaultNoOpSession = new NoOpSession();

	@Override
	public Serializable getId() {
		// Ignored no operation
		return null;
	}

	@Override
	public Date getStartTimestamp() {
		// Ignored no operation
		return null;
	}

	@Override
	public Date getLastAccessTime() {
		// Ignored no operation
		return null;
	}

	@Override
	public long getTimeout() throws InvalidSessionException {
		// Ignored no operation
		return 0;
	}

	@Override
	public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
		// Ignored no operation

	}

	@Override
	public String getHost() {
		// Ignored no operation
		return null;
	}

	@Override
	public void touch() throws InvalidSessionException {
		// Ignored no operation

	}

	@Override
	public void stop() throws InvalidSessionException {
		// Ignored no operation

	}

	@Override
	public Collection<Object> getAttributeKeys() throws InvalidSessionException {
		// Ignored no operation
		return emptyList();
	}

	@Override
	public Object getAttribute(Object key) throws InvalidSessionException {
		// Ignored no operation
		return null;
	}

	@Override
	public void setAttribute(Object key, Object value) throws InvalidSessionException {
		// Ignored no operation

	}

	@Override
	public Object removeAttribute(Object key) throws InvalidSessionException {
		// Ignored no operation
		return null;
	}

}
