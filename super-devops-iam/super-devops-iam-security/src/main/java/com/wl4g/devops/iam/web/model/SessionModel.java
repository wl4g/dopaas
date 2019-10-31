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
package com.wl4g.devops.iam.web.model;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

import com.wl4g.devops.iam.common.session.IamSession;

/**
 * Sessions query model.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-31
 * @since
 */
public class SessionModel extends IamSession {
	private static final long serialVersionUID = 1990530522326712114L;

	/**
	 * Active sessions response key-name.
	 */
	final public static String KEY_SESSIONS = "sessions";

	public SessionModel() {
	}

	public SessionModel(IamSession session) {
		setId(session.getId());
		setStartTimestamp(session.getStartTimestamp());
		setStopTimestamp(session.getStopTimestamp());
		setLastAccessTime(session.getLastAccessTime());
		setTimeout(session.getTimeout());
		setExpired(session.isExpired());
		setHost(session.getHost());
		setAttributes(session.getAttributes());
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}
