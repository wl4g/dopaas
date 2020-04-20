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
package com.wl4g.devops.iam.handler;

import com.wl4g.devops.common.bean.iam.ApplicationInfo;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.exception.iam.IllegalApplicationAccessException;
import com.wl4g.devops.common.exception.iam.IllegalCallbackDomainException;
import com.wl4g.devops.common.exception.iam.InvalidGrantTicketException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.authc.LogoutAuthenticationToken;
import com.wl4g.devops.iam.common.authc.model.LoggedModel;
import com.wl4g.devops.iam.common.authc.model.LogoutModel;
import com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel;
import com.wl4g.devops.iam.common.authc.model.SessionValidityAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidatedAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidateModel;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.common.session.GrantCredentialsInfo;
import com.wl4g.devops.iam.common.session.GrantCredentialsInfo.GrantApp;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.session.IamSession.RelationAttrKey;
import com.wl4g.devops.iam.common.session.mgt.IamSessionDAO;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.SimplePrincipalInfo;
import com.wl4g.devops.support.redis.ScanCursor;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel.Status.ExpiredAuthorized;
import static com.wl4g.devops.iam.common.utils.AuthenticatingUtils.*;
import static com.wl4g.devops.iam.sns.handler.SecondAuthcSnsHandler.SECOND_AUTHC_CACHE;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.tool.common.web.WebUtils2.isEqualWithDomain;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.shiro.web.util.WebUtils.toHttp;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Default authentication handler implements
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月22日
 * @since
 */
public class CentralAuthenticationHandler extends AbstractAuthenticationHandler {

	/**
	 * IAM session DAO.
	 */
	@Autowired
	protected IamSessionDAO sessionDAO;

	@Override
	public void checkAuthenticateRedirectValidity(String appName, String redirectUrl) throws IllegalCallbackDomainException {
		// Check redirect URL(When source application is not empty)
		if (!isBlank(appName)) {
			if (isBlank(redirectUrl)) {
				throw new IllegalCallbackDomainException("Parameters redirectUrl and application cannot be null");
			}

			// Get application.
			ApplicationInfo app = configurer.getApplicationInfo(appName);
			if (Objects.isNull(app)) {
				throw new IllegalCallbackDomainException("Illegal redirect application URL parameters.");
			}
			state(!isAnyBlank(app.getAppName(), app.getExtranetBaseUri()),
					String.format("Invalid redirection domain configure, application[%s]", appName));
			log.debug("Check authentication requests application [{}]", app);

			// Check redirect URL are legitimate callback URI?(As long as there
			// is a match)
			String host = URI.create(redirectUrl).getHost();
			if (!(equalsAny(host, PERMISSIVE_HOSTS) || isEqualWithDomain(redirectUrl, app.getExtranetBaseUri())
					|| isEqualWithDomain(redirectUrl, app.getIntranetBaseUri()))) {
				throw new IllegalCallbackDomainException(String.format("Illegal redirectUrl [%s]", redirectUrl));
			}
		}
	}

	@Override
	public void assertApplicationAccessAuthorized(String principal, String appName) throws IllegalApplicationAccessException {
		hasText(principal, "'principal' must not be empty");
		hasText(appName, "'appName' must not be empty");
		if (!configurer.isApplicationAccessAuthorized(principal, appName)) {
			throw new IllegalApplicationAccessException(
					bundle.getMessage("GentralAuthenticationHandler.unaccessible", principal, appName));
		}
	}

	@Override
	public TicketValidatedAssertModel<IamPrincipalInfo> validate(TicketValidateModel model) {
		TicketValidatedAssertModel<IamPrincipalInfo> assertion = new TicketValidatedAssertModel<>();
		String grantAppname = model.getApplication();
		hasTextOf(grantAppname, "grantAppname");

		// Get subject session of grantCredentials info.
		/*
		 * Synchronize with xx.xx.session.mgt.IamSessionManager#getSessionId
		 */
		Subject subject = getSubject();
		log.debug("Validating subject: {} by grantTicket: {}", subject, model.getTicket());

		// Assertion grantCredentials info.
		assertGrantingTicketValidity(subject, model);

		// Check access authorized from application.
		assertApplicationAccessAuthorized((String) subject.getPrincipal(), grantAppname);

		// Force clearance of last grant Ticket
		/*
		 * Synchronize with
		 * xx.xx.handler.impl.FastCasAuthenticationHandler#validate#loggedin
		 */
		cacheManager.getCache(CACHE_TICKET_S).remove(new CacheKey(model.getTicket()));
		log.debug("Clean older grantTicket: {}", model.getTicket());

		// --- Grant attributes setup. ---

		// Grant validated start date.
		long now = currentTimeMillis();
		assertion.setValidFromDate(new Date(now));

		/*
		 * x.client.realm.FastCasAuthorizingRealm#doGetAuthenticationInfo Grant
		 * term of validity(end date).
		 */
		assertion.setValidUntilDate(new Date(now + getSessionExpiredTime()));

		// Updating grantCredentials info
		/**
		 * Synchronize with: </br>
		 * x.handler.impl.FastCasAuthenticationHandler#logout() </br>
		 * x.session.mgt.IamSessionManager#getSessionId
		 */
		String newGrantTicket = generateGrantTicket();
		/**
		 * {@link com.wl4g.devops.iam.client.realm.FastCasAuthorizingRealm#doAuthenticationInfo(AuthenticationToken)}
		 */
		assertion.setPrincipalInfo(new SimplePrincipalInfo(getPrincipalInfo()).setStoredCredentials(newGrantTicket));
		log.info("New validated grantTicket: {}, sessionId: {}", newGrantTicket, getSessionId());

		// Grants roles and permissions attributes.
		Map<String, String> attributes = assertion.getPrincipalInfo().getAttributes();
		attributes.put(KEY_LANG_ATTRIBUTE_NAME, getBindValue(KEY_LANG_ATTRIBUTE_NAME));

		// Sets re-generate childDataCipherKey(grant application)
		String childDataCipherKey = null;
		if (config.getCipher().isEnableDataCipher()) {
			childDataCipherKey = generateDataCipherKey();
			attributes.put(KEY_DATA_CIPHER, childDataCipherKey);
		}
		// Sets re-generate childAccessToken(grant application)
		String childAccessTokenSignKey = null;
		if (config.getSession().isEnableAccessTokenValidity()) {
			String accessTokenSignKey = getBindValue(KEY_ACCESSTOKEN_SIGN);
			childAccessTokenSignKey = generateAccessTokenSignKey(model.getSessionId(), accessTokenSignKey);
			attributes.put(KEY_ACCESSTOKEN_SIGN, childAccessTokenSignKey);
		}

		// Storage grantCredentials info.
		GrantApp grant = new GrantApp(newGrantTicket).setDataCipher(childDataCipherKey)
				.setAccessTokenSignKey(childAccessTokenSignKey);
		putGrantCredentials(getSession(false), grantAppname, grant);

		return assertion;
	}

	@Override
	public LoggedModel loggedin(String grantAppname, Subject subject) {
		hasTextOf(grantAppname, "grantAppname");

		// Check authentication.
		if (nonNull(subject) && subject.isAuthenticated() && !isBlank((String) subject.getPrincipal())) {
			Session session = subject.getSession();

			// Generate granting ticket. Same: CAS/service-ticket
			String grantTicket = null;
			// If the ticket has been generated in the previous
			// moment.(currently?)
			GrantApp grant = getGrantCredentials(session).getGrantApp(grantAppname);
			if (!isNull(grant)) {
				grantTicket = grant.getGrantTicket();
			} else {
				// Init generate grantCredentials
				grantTicket = generateGrantTicket();
				log.info("New init grantTicket: {}, grantAppname: {}", grantTicket, grantAppname);
			}

			// Puts grantInfo session => applications
			putGrantCredentials(session, grantAppname, new GrantApp().setGrantTicket(grantTicket));

			return new LoggedModel(grantTicket);
		}
		throw new AuthenticationException("Unauthenticated");
	}

	@Override
	public LogoutModel logout(boolean forced, String appName, HttpServletRequest request, HttpServletResponse response) {
		log.debug("Logout from: {}, forced: {}, sessionId: {}", appName, forced, getSessionId());
		Subject subject = getSubject();

		// From client signout
		coprocessor.preLogout(new LogoutAuthenticationToken(getPrincipal(false), getHttpRemoteAddr(request)), toHttp(request),
				toHttp(response));

		// Represents all loggout Tags
		boolean logoutAll = true;
		// Get bind session grant information
		GrantCredentialsInfo info = getGrantCredentials(subject.getSession());
		log.debug("Got grantInfo: [{}] with sessionId: [{}]", info, subject.getSession().getId());

		if (!isNull(info) && info.hasEmpty()) {
			// Query applications by bind session names
			Set<String> appNames = info.getGrantApps().keySet();
			// Cleanup this(Solve the dead cycle).
			appNames.remove(config.getServiceName());

			List<ApplicationInfo> apps = configurer.findApplicationInfo(appNames.toArray(new String[] {}));
			if (!isEmpty(apps)) {
				// logout all
				logoutAll = handleLogoutSessionAll(subject, info, apps);
			} else
				log.debug("Not found logout appInfo. appNames: {}", appNames);
		}

		if (forced || logoutAll) {
			// Logout all sessions.
			try {
				/**
				 * That's the subject Refer to
				 * {@link com.wl4g.devops.iam.session.mgt.IamServerSessionManager#getSessionId())
				 * try/catch added for #SHIRO-298:
				 */
				log.debug("Logouting... sessionId: {}", getSessionId(subject));
				subject.logout(); // After that, session is null
			} catch (SessionException e) {
				log.warn("Encountered session exception during logout. This can generally safely be ignored.", e);
			}
		}

		return isNotBlank(appName) ? new LogoutModel(appName) : new LogoutModel();
	}

	@Override
	public SecondAuthcAssertModel secondaryValidate(String secondAuthCode, String appName) {
		CacheKey ekey = new CacheKey(secondAuthCode, SecondAuthcAssertModel.class);
		try {
			/*
			 * Save authorized info to cache. See:
			 * xx.iam.sns.handler.SecondAuthcSnsHandler#afterCallbackSet()
			 */
			SecondAuthcAssertModel assertion = (SecondAuthcAssertModel) cacheManager.getIamCache(SECOND_AUTHC_CACHE).get(ekey);
			// Check assertion expired
			if (assertion == null) {
				assertion = new SecondAuthcAssertModel(ExpiredAuthorized);
				assertion.setErrdesc("Authorization expires, please re-authorize.");
			}
			return assertion;
		} finally { // Release authentication code
			log.info("Remove release second authentication info. key[{}]", new String(ekey.getKey()));
			cacheManager.getIamCache(SECOND_AUTHC_CACHE).remove(ekey);
		}
	}

	@Override
	public SessionValidityAssertModel sessionValidate(SessionValidityAssertModel model) {
		hasTextOf(model.getApplication(), "grantAppname");

		ScanCursor<IamSession> cursor = sessionDAO.getAccessSessions(DEFAULT_SCAN_SIZE);
		while (cursor.hasNext()) {
			Session session = cursor.next();
			// GrantTicket of session.
			GrantCredentialsInfo info = getGrantCredentials(session);

			if (nonNull(info) && info.has(model.getApplication())) {
				String storedTicket = info.getGrantApps().get(model.getApplication()).getGrantTicket();
				// If exist grantTicket with application.
				if (!isBlank(storedTicket)) {
					model.getTickets().remove(storedTicket);
				}
			}
		}
		return model;
	}

	/**
	 * Assertion granting ticket validity </br>
	 *
	 * @param subject
	 * @param model
	 * @throws InvalidGrantTicketException
	 * @see {@link com.wl4g.devops.iam.handler.CentralAuthenticationHandler#loggedin}
	 */
	private void assertGrantingTicketValidity(Subject subject, TicketValidateModel model) throws InvalidGrantTicketException {
		if (isBlank(model.getTicket())) {
			log.warn("Invalid grantTicket: {}, application: {}, sessionId: {}", model.getTicket(), model.getApplication(),
					getSessionId(subject));
			throw new InvalidGrantTicketException("Invalid granting ticket and is required");
		}

		// Get grant information
		GrantCredentialsInfo info = getGrantCredentials(subject.getSession());
		log.debug("Got grantTicketInfo: {}, sessionId:{}", info, getSessionId());

		// No grant ticket created or expired?
		if (isNull(info) || !info.has(model.getApplication())) {
			throw new InvalidGrantTicketException("Invalid granting ticket application");
		}

		// Validate grantTicket and storedTicket?
		String storedTicket = info.getGrantApp(model.getApplication()).getGrantTicket();
		if (!(model.getTicket().equals(storedTicket) && subject.isAuthenticated() && nonNull(subject.getPrincipal()))) {
			log.warn("Invalid grantTicket: {}, appName: {}, sessionId: {}", model.getTicket(), model.getApplication(),
					subject.getSession().getId());
			throw new InvalidGrantTicketException("Invalid granting ticket");
		}

	}

	/**
	 * Handle logout all
	 *
	 * @param subject
	 * @param info
	 * @param apps
	 * @return
	 */
	private boolean handleLogoutSessionAll(Subject subject, GrantCredentialsInfo info, List<ApplicationInfo> apps) {
		boolean logoutAll = true; // Represents all logged-out Tags

		/*
		 * Notification all logged-in applications to logout
		 */
		for (ApplicationInfo app : apps) {
			hasText(app.getIntranetBaseUri(), "Application[%s] 'internalBaseUri' must not be empty", app.getAppName());
			// GrantTicket by application name
			String grantTicket = info.getGrantApps().get(app.getAppName()).getGrantTicket();
			// Logout URL
			String url = new StringBuffer(app.getIntranetBaseUri()).append(URI_C_BASE).append("/").append(URI_C_LOGOUT)
					.append("?").append(config.getParam().getGrantTicket()).append("=").append(grantTicket).toString();

			// Post remote client logout
			try {
				RespBase<LogoutModel> resp = restTemplate
						.exchange(url, HttpMethod.POST, null, new ParameterizedTypeReference<RespBase<LogoutModel>>() {
						}).getBody();
				if (RespBase.isSuccess(resp))
					log.info("Logout finished for principal:{}, application:{} url:{}", subject.getPrincipal(), app.getAppName(),
							url);
				else
					throw new IamException(resp != null ? resp.getMessage() : "No response");
			} catch (Exception e) {
				logoutAll = false;
				log.error(String.format("Remote client logout failure. principal[%s] application[%s] url[%s]",
						subject.getPrincipal(), app.getAppName(), url), e);
			}
		}

		return logoutAll;
	}

	/**
	 * Generate grantCredentials ticket.
	 *
	 * @return
	 */
	private String generateGrantTicket() {
		return "st" + randomAlphabetic(48, 64);
	}

	/**
	 * Puts grantCredentials to session. </br>
	 *
	 * @param session
	 *            Session
	 * @param grantAppname
	 *            granting application name
	 * @param grant
	 *            grant ticket
	 */
	private void putGrantCredentials(Session session, String grantAppname, GrantApp grant) {
		notNullOf(session, "session");
		hasTextOf(grantAppname, "grantAppname");
		notNullOf(grant, "grant");

		/**
		 * @See {@link CentralAuthenticationHandler#validate()}
		 */
		GrantCredentialsInfo info = getGrantCredentials(session);
		if (info.has(grantAppname)) {
			log.debug("Sets grantTicket of sessionId: {} grantAppname: {}", session.getId(), grantAppname);
		}
		// Updating grantTicket.
		session.setAttribute(new RelationAttrKey(KEY_GRANTCREDENTIALS), info.putGrant(grantAppname, grant));
		log.debug("Updated granting credentials to session. {}", info);

		// Sets grantTicket => sessionId.
		/**
		 * @see {@link com.wl4g.devops.iam.client.validation.FastCasTicketIamValidator#validate()}
		 * @see {@link com.wl4g.devops.iam.common.session.mgt.AbstractIamSessionManager#getSessionId()}
		 */
		long expireTime = getSessionExpiredTime(session); // Expiration time
		cacheManager.getIamCache(CACHE_TICKET_S).put(new CacheKey(grant.getGrantTicket(), expireTime), valueOf(session.getId()));
		log.debug("Sets grantTicket: '{}' of seesionId: '{}', expireTime: '{}'", grant, getSessionId(session), expireTime);
	}

	/**
	 * Gets bind session granting credentials.
	 *
	 * @param session
	 * @return
	 */
	public static GrantCredentialsInfo getGrantCredentials(Session session) {
		GrantCredentialsInfo info = (GrantCredentialsInfo) session
				.getAttribute(new RelationAttrKey(KEY_GRANTCREDENTIALS, GrantCredentialsInfo.class));
		return isNull(info) ? new GrantCredentialsInfo() : info;
	}

	/**
	 * Scan iteration batch size.
	 */
	final public static int DEFAULT_SCAN_SIZE = 100;

	/**
	 * Default gets {@link GrantCredentialsInfo} lock expirtion(ms)
	 */
	final public static long DEFAULT_LOCK_CREDENTIALS_EXPIRE = 5000L;

	/**
	 * IAM server grantTicket info of application.
	 */
	final public static String KEY_GRANTCREDENTIALS = "grantCredentials";

	/**
	 * Permissived whitelist hosts.
	 */
	final public static String[] PERMISSIVE_HOSTS = new String[] { "localhost", "127.0.0.1", "0:0:0:0:0:0:0:1" };

}