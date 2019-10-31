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
package com.wl4g.devops.iam.configure;

import com.wl4g.devops.common.bean.iam.ApplicationInfo;
import com.wl4g.devops.common.bean.iam.IamAccountInfo;
import com.wl4g.devops.common.bean.iam.IamAccountInfo.*;
import com.wl4g.devops.common.bean.iam.SocialConnectInfo;
import com.wl4g.devops.common.bean.iam.User;
import com.wl4g.devops.dao.iam.UserDao;
import com.wl4g.devops.dao.share.ApplicationDao;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.common.utils.lang.Collections2.isEmptyArray;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.equalsAny;
import static org.apache.shiro.util.Assert.hasText;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Standard IAM Security context handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月29日
 * @since
 */
@Service
public class StandardSecurityConfigurer implements ServerSecurityConfigurer {

	/**
	 * Because ProtoStuff can't serialize XXXDao created by MyBatis, it will
	 * throw a serialization exception. This field must be ignored. </br>
	 * The problem is method: {@link StandardSecurityConfigurer#getIamAccount()}
	 */
	@Autowired
	private transient ApplicationDao applicationDao;

	@Autowired
	private transient UserDao userDao;

	@Override
	public String determineLoginSuccessUrl(String successUrl, AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) {
		return successUrl;
	}

	@Override
	public String determineLoginFailureUrl(String loginUrl, AuthenticationToken token, AuthenticationException ae,
			ServletRequest request, ServletResponse response) {
		return loginUrl;
	}

	@Override
	public ApplicationInfo getApplicationInfo(String appName) {
		List<ApplicationInfo> apps = safeList(findApplicationInfo(appName));
		return !isEmpty(apps) ? apps.get(0) : null;
	}

	@Override
	public List<ApplicationInfo> findApplicationInfo(String... appNames) {
		List<ApplicationInfo> appInfoList = new ArrayList<>();
		if (isEmptyArray(appNames)) {
			return emptyList();
		}
		// Is IAM example demo.
		if (equalsAny("iam-example", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("iam-example", "http://localhost:14041");
			appInfo.setIntranetBaseUri("http://localhost:14041/iam-example");
			appInfoList.add(appInfo);
		} else { // Formal environment.
			List<ApplicationInfo> applications = applicationDao.getByAppNames(appNames);
			appInfoList.addAll(applications);
		}

		//// --- For testing. ---
		//
		// if (equalsAny("scm-server", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("scm-server",
		// "http://localhost:14043");
		// appInfo.setIntranetBaseUri("http://localhost:14043/scm-server");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("ci-server", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("ci-server",
		// "http://localhost:14046");
		// appInfo.setIntranetBaseUri("http://localhost:14046/ci-server");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("umc-admin", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("umc-admin",
		// "http://localhost:14048");
		// appInfo.setIntranetBaseUri("http://localhost:14048/umc-admin");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("share-admin", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("share-admin",
		// "http://localhost:14051");
		// appInfo.setIntranetBaseUri("http://localhost:14051/share-admin");
		// appInfoList.add(appInfo);
		// }
		// if (equalsAny("srm-admin", appNames)) {
		// ApplicationInfo appInfo = new ApplicationInfo("srm-admin",
		// "http://localhost:15050");
		// appInfo.setIntranetBaseUri("http://localhost:15050/srm-admin");
		// appInfoList.add(appInfo);
		// }
		//
		// http://localhost:14041 # iam-example
		// http://localhost:14043 # scm-server
		// http://localhost:14046 # ci-server
		// http://localhost:14048 # umc-manager
		// http://localhost:14050 # srm-manager
		// http://localhost:14051 # share-manager
		//

		return appInfoList;
	}

	@Override
	public IamAccountInfo getIamAccount(Parameter parameter) {
		User user = null;

		// By SNS authorizing
		if (parameter instanceof SnsParameter) {
			SnsParameter snsParameter = (SnsParameter) parameter;
			user = userDao.selectByUnionIdOrOpenId(snsParameter.getUnionId(), snsParameter.getOpenId());
		}
		// By general account
		else if (parameter instanceof SimpleParameter) {
			SimpleParameter simpleParameter = (SimpleParameter) parameter;
			user = userDao.selectByUserName(simpleParameter.getPrincipal());
		}
		if (nonNull(user)) {
			return new StandardIamAccountInfo(user.getUserName(), user.getPassword());
		}
		return null;
	}

	@Override
	public boolean isApplicationAccessAuthorized(String principal, String application) {
		return true;
	}

	@Override
	public String findRoles(String principal, String application) {
		return "sc_sys_mgt,sc_general_mgt,sc_general_operator,sc_user_jack";
	}

	@Override
	public String findPermissions(String principal, String application) {
		return "sys:user:view,sys:user:edit,goods:order:view,goods:order:edit";
	}

	@Override
	public void bindSocialConnection(SocialConnectInfo social) {

	}

	@Override
	public List<SocialConnectInfo> findSocialConnections(String principal, String provider) {

		return null;
	}

	@Override
	public void unbindSocialConnection(SocialConnectInfo social) {

	}

	/**
	 * Standard IAM account info implements.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月31日
	 * @since
	 */
	public static class StandardIamAccountInfo implements IamAccountInfo {
		private static final long serialVersionUID = 1L;

		/** Authenticating principal name. */
		final private String principal;

		/** Authenticating principal DB stored credenticals. */
		final private String storedCredentials;

		public StandardIamAccountInfo(String principal, String storedCredentials) {
			hasText(principal, "Principal must not be empty.");
			hasText(storedCredentials, "StoredCredentials must not be empty.");
			this.principal = principal;
			this.storedCredentials = storedCredentials;
		}

		@Override
		public String getPrincipal() {
			return principal;
		}

		@Override
		public String getStoredCredentials() {
			return storedCredentials;
		}

	}

}