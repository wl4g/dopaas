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
package com.wl4g.devops.iam.authc;

import static com.wl4g.devops.tool.common.lang.Assert2.*;

import com.wl4g.devops.iam.crypto.SecureCryptService.SecureAlgKind;

/**
 * SMS authentication token
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public class SmsAuthenticationToken extends ClientSecretIamAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	/**
	 * SMS authenticating action.
	 */
	final private Action action;

	/**
	 * Principal(e.g. user-name or mobile number etc)
	 */
	final private String principal;

	/**
	 * Dynamic verification code
	 */
	final private String smsCode;

	public SmsAuthenticationToken(final SecureAlgKind secureAlgKind, final String clientSecretKey, final String remoteHost,
			final String action, final String principal, final String smsCode) {
		super(secureAlgKind, clientSecretKey, remoteHost);
		hasTextOf(action, "action");
		hasTextOf(principal, "principal");
		hasTextOf(smsCode, "smsCode");
		this.action = Action.of(action);
		this.principal = principal;
		this.smsCode = smsCode;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public Object getCredentials() {
		return smsCode;
	}

	public Action getAction() {
		return action;
	}

	/**
	 * SMS authentication action
	 *
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年4月19日
	 * @since
	 */
	public static enum Action {

		/**
		 * SMS login action type.
		 */
		LOGIN,

		/**
		 * SMS bind action type.
		 */
		BIND,

		/**
		 * SMS unbind action type.
		 */
		UNBIND;

		/**
		 * Converter string to {@link Action}
		 *
		 * @param action
		 * @return
		 */
		public static Action of(String action) {
			Action wh = safeOf(action);
			if (wh == null) {
				throw new IllegalArgumentException(String.format("Illegal action '%s'", action));
			}
			return wh;
		}

		/**
		 * Safe converter string to {@link Action}
		 *
		 * @param action
		 * @return
		 */
		public static Action safeOf(String action) {
			for (Action t : values()) {
				if (String.valueOf(action).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}

	}

}