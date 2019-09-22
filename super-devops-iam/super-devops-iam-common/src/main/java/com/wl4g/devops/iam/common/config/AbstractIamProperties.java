/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.common.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

/**
 * IAM abstract properties.
 * 
 * @author Wangl.sir
 * @version v1.0.0 2018-09-22
 * @since
 */
public abstract class AbstractIamProperties<P extends ParamProperties> implements InitializingBean, Serializable {
	private static final long serialVersionUID = -5858422822181237865L;

	/**
	 * Default view access base URI
	 */
	final public static String DEFAULT_VIEW_BASE_URI = "/view";

	/**
	 * Default view index URI.
	 */
	final public static String DEFAULT_VIEW_INDEX_URI = DEFAULT_VIEW_BASE_URI + "/index.html";

	/**
	 * Default view 403 URI.
	 */
	final public static String DEFAULT_VIEW_403_URI = DEFAULT_VIEW_BASE_URI + "/403.html";

	/**
	 * Spring boot environment.
	 */
	@Autowired
	protected Environment environment;

	/**
	 * External custom filter chain pattern matching
	 */
	protected Map<String, String> filterChain = new LinkedHashMap<>();

	/**
	 * Session cache configuration properties.
	 */
	protected CacheProperties cache = new CacheProperties();

	/**
	 * Cookie configuration properties.
	 */
	protected CookieProperties cookie = new CookieProperties();

	/**
	 * Session configuration properties.
	 */
	protected SessionProperties session = new SessionProperties();

	/**
	 * Redirect to login URI.</br>
	 * e.g. </br>
	 * In IAM-Client: {iam-server-uri}/authenticator </br>
	 * In IAM-Server: {iam-server-uri}/view/login.html
	 * 
	 * @return
	 */
	protected abstract String getLoginUri();

	/**
	 * Success URI.
	 * 
	 * @return
	 */
	protected abstract String getSuccessUri();

	/**
	 * Unauthorized(403) URI.
	 * 
	 * @return
	 */
	protected abstract String getUnauthorizedUri();

	public Map<String, String> getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(Map<String, String> filterChain) {
		this.filterChain = filterChain;
	}

	public CacheProperties getCache() {
		return cache;
	}

	public void setCache(CacheProperties cache) {
		this.cache = cache;
	}

	public CookieProperties getCookie() {
		return cookie;
	}

	public void setCookie(CookieProperties cookie) {
		this.cookie = cookie;
	}

	public SessionProperties getSession() {
		return session;
	}

	public void setSession(SessionProperties session) {
		this.session = session;
	}

	/**
	 * IAM parameters configuration properties.
	 * 
	 * @param param
	 */
	public abstract P getParam();

	public abstract void setParam(P param);

	/**
	 * Apply default properties if necessary.
	 */
	protected abstract void applyDefaultIfNecessary();

	/**
	 * Validation.
	 */
	protected void validation() {
		Assert.hasText(getLoginUri(), "'loginUri' must be empty.");
		Assert.hasText(getSuccessUri(), "'successUri' must be empty.");
		Assert.hasText(getUnauthorizedUri(), "'unauthorizedUri' must be empty.");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Apply default properties if necessary.
		applyDefaultIfNecessary();
		// Validate attributes.
		validation();
	}

	/**
	 * Session cache configuration properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月29日
	 * @since
	 */
	public class CacheProperties implements Serializable {
		private static final long serialVersionUID = 5246530494860631770L;

		/**
		 * IAM cache prefix.
		 */
		private String prefix;

		public String getPrefix() {
			if (isBlank(prefix)) {
				setPrefix(environment.getProperty("spring.application.name") + "_iam_");
			}
			Assert.hasText(prefix, "Cache prefix must not be empty.");
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

	}

	/**
	 * Cookie configuration properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月29日
	 * @since
	 */
	public class CookieProperties extends SimpleCookie implements Serializable {
		private static final long serialVersionUID = 918554077474485700L;

		@Override
		public String getName() {
			if (isBlank(super.getName())) {
				setName("IAMSID_" + environment.getProperty("spring.application.name"));
			}
			Assert.hasText(super.getName(), "Cookie name must not be empty.");
			return super.getName();
		}

		/**
		 * Specification capitalizes cookie names
		 */
		public void setName(String name) {
			super.setName(name.toUpperCase(Locale.US));
		}

	}

	/**
	 * Session configuration properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月29日
	 * @since
	 */
	public class SessionProperties implements Serializable {
		private static final long serialVersionUID = -2694422471812860689L;

		/**
		 * Session timeout in milliseconds
		 */
		private Long globalSessionTimeout = 1800_000L;

		/**
		 * Clean up invalid sessions on a regular basis, and clean up isolated
		 * sessions caused by users closing browsers directly
		 */
		private Long sessionValidationInterval = 360_000L;

		/**
		 * {@link org.apache.shiro.web.session.mgt.DefaultWebSessionManager#setSessionIdUrlRewritingEnabled}
		 * EG:http://domain/project/index;JSESSIONID=e5cdc1582aa849a8b36aa4d161e5cd97
		 */
		private boolean urlRewriting = false;

		/**
		 * When request remember is enabled, it indicates that the address
		 * previously remembered will be jumped after successful login, rather
		 * than the default home page, except for cas-client requests (which is
		 * the application callback address at this time).
		 */
		private boolean enableRequestRemember = true;

		public Long getGlobalSessionTimeout() {
			return globalSessionTimeout;
		}

		public void setGlobalSessionTimeout(Long globalSessionTimeout) {
			this.globalSessionTimeout = globalSessionTimeout;
		}

		public Long getSessionValidationInterval() {
			return sessionValidationInterval;
		}

		public void setSessionValidationInterval(Long sessionValidationInterval) {
			this.sessionValidationInterval = sessionValidationInterval;
		}

		public boolean isUrlRewriting() {
			return urlRewriting;
		}

		public void setUrlRewriting(boolean urlRewriting) {
			this.urlRewriting = urlRewriting;
		}

		public boolean isEnableRequestRemember() {
			return enableRequestRemember;
		}

		public void setEnableRequestRemember(boolean enableRequestRemember) {
			this.enableRequestRemember = enableRequestRemember;
		}

	}

	/**
	 * IAM parameters configuration properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月29日
	 * @since
	 */
	public abstract static class ParamProperties implements Serializable {
		private static final long serialVersionUID = 3258460473777285504L;

		/**
		 * This SID session is used if the parameter contains the "SID"
		 * parameter name.
		 */
		private String sid = "__sid";

		/**
		 * Save SID to cookie, use this parameter name in browser mode.
		 */
		private String sidSaveCookie = "__cookie";

		/**
		 * Authentication parameter application name
		 */
		private String logoutForced = "forced";

		/**
		 * Authentication parameter application name
		 */
		private String application = "application";

		/**
		 * Authentication parameter grant ticket name
		 */
		private String grantTicket = "gt";

		/**
		 * Authentication center sets the parameter name of authentication
		 * response type.
		 */
		private String responseType = "response_type";

		/**
		 * Redirected URL parameter name for request authentication callback
		 */
		private String redirectUrl = "redirect_url";

		/**
		 * Name of 'which' parameter of SNS OAuth authentication API
		 */
		private String which = "which";

		/**
		 * Name of 'state' parameter of SNS OAuth authentication API
		 */
		private String state = "state";

		/**
		 * SNS callback redirection refresh URL parameter name
		 */
		private String refreshUrl = "refresh_url";

		/**
		 * SNS callback redirection, whether to use the parameter name of the
		 * agent page
		 */
		private String agent = "agent";

		/**
		 * Number name of resource owner 'authorizers' for SNS OAuth secondary
		 * authentication API
		 */
		private String authorizers = "authorizers";

		/**
		 * Number name of resource owner 'secondAuthCode' for SNS OAuth
		 * secondary authentication API
		 */
		private String secondAuthCode = "secondAuthCode";

		/**
		 * Number name of resource owner 'funcId' for SNS OAuth secondary
		 * authentication API
		 */
		private String funcId = "function";

		/**
		 * Internationalized language parameter name
		 */
		private String i18nLang = "lang";

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getSidSaveCookie() {
			return sidSaveCookie;
		}

		public void setSidSaveCookie(String sidSaveCookie) {
			this.sidSaveCookie = sidSaveCookie;
		}

		public String getLogoutForced() {
			return logoutForced;
		}

		public void setLogoutForced(String logoutForced) {
			this.logoutForced = logoutForced;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getGrantTicket() {
			return grantTicket;
		}

		public void setGrantTicket(String grantTicket) {
			this.grantTicket = grantTicket;
		}

		public String getResponseType() {
			return responseType;
		}

		public void setResponseType(String responseType) {
			this.responseType = responseType;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

		public String getWhich() {
			return which;
		}

		public void setWhich(String which) {
			this.which = which;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getRefreshUrl() {
			return refreshUrl;
		}

		public void setRefreshUrl(String refreshUrl) {
			this.refreshUrl = refreshUrl;
		}

		public String getAgent() {
			return agent;
		}

		public void setAgent(String agent) {
			this.agent = agent;
		}

		public String getAuthorizers() {
			return authorizers;
		}

		public void setAuthorizers(String authorizers) {
			this.authorizers = authorizers;
		}

		public String getSecondAuthCode() {
			return secondAuthCode;
		}

		public void setSecondAuthCode(String secondAuthCode) {
			this.secondAuthCode = secondAuthCode;
		}

		public String getFuncId() {
			return funcId;
		}

		public void setFuncId(String funcId) {
			this.funcId = funcId;
		}

		public String getI18nLang() {
			return i18nLang;
		}

		public void setI18nLang(String locale) {
			this.i18nLang = locale;
		}

	}

	/**
	 * When a which request connects to a social network (requesting oauth2
	 * authorization), the type of destination operation (e.g. login,
	 * registration binding)
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2019年1月7日
	 * @since
	 */
	public static enum Which {

		/**
		 * Used when authorizing login using social accounts.(<font color=red>It
		 * operates on the PC side)</font>
		 */
		LOGIN,

		/**
		 * Used for binding social accounts.(<font color=red>It operates on the
		 * PC side)</font>
		 */
		BIND,

		/**
		 * Used for UnBinding social accounts.(<font color=red>It operates on
		 * the PC side)</font>
		 */
		UNBIND,

		/**
		 * Used when authorizing using social service provider client, for
		 * example, authorized login on the client of public platform such as
		 * WeChat, QQ, Facebook.(<font color=red>It operates on the Mobile
		 * side</font>)
		 */
		CLIENT_AUTH,

		/**
		 * Used for secondary authentication(SNS mode validation)
		 */
		SECOND_AUTH;

		/**
		 * Converter string to {@link Which}
		 * 
		 * @param which
		 * @return
		 */
		public static Which of(String which) {
			Which wh = safeOf(which);
			if (wh == null) {
				throw new IllegalArgumentException(String.format("Illegal which '%s'", which));
			}
			return wh;
		}

		/**
		 * Safe converter string to {@link Which}
		 * 
		 * @param which
		 * @return
		 */
		public static Which safeOf(String which) {
			for (Which t : values()) {
				if (String.valueOf(which).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}

	}

}