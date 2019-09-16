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
package com.wl4g.devops.iam.configure;

import com.wl4g.devops.common.bean.iam.ApplicationInfo;
import com.wl4g.devops.common.bean.iam.IamAccountInfo;
import com.wl4g.devops.common.bean.iam.IamAccountInfo.Parameter;
import com.wl4g.devops.common.bean.iam.SocialConnectInfo;
import com.wl4g.devops.common.bean.share.Application;
import com.wl4g.devops.common.utils.lang.Collections2;
import com.wl4g.devops.dao.share.ApplicationDao;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	@Autowired
	private ApplicationDao applicationDao;

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
		// TODO(Using DB) For testing::
		List<ApplicationInfo> apps = findApplicationInfo(appName);
		return !apps.isEmpty() ? apps.get(0) : null;
	}

	@Override
	public List<ApplicationInfo> findApplicationInfo(String... appNames) {
		List<ApplicationInfo> appInfoList = new ArrayList<>();

		if(Collections2.isEmptyArray(appNames)){
			return Collections.emptyList();
		}

		//TODO (Using DB)
		List<Application> applications = applicationDao.getByAppNames(appNames);
		for(Application application : applications){
			ApplicationInfo appInfo = new ApplicationInfo(application.getAppName(), application.getExtranetBaseuri());
			appInfo.setIntranetBaseUri(application.getIntranetBaseuri());
			appInfoList.add(appInfo);
		}

		/*if (equalsAny("iam-example", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("iam-example", "http://localhost:14041");
			appInfo.setIntranetBaseUri("http://localhost:14041/iam-example");
			appInfoList.add(appInfo);
		}
		if (equalsAny("scm-server", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("scm-server", "http://localhost:14043");
			appInfo.setIntranetBaseUri("http://localhost:14043/scm-server");
			appInfoList.add(appInfo);
		}
		if (equalsAny("ci-server", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("ci-server", "http://localhost:14046");
			appInfo.setIntranetBaseUri("http://localhost:14046/ci-server");
			appInfoList.add(appInfo);
		}
		if (equalsAny("umc-admin", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("umc-admin", "http://localhost:14048");
			appInfo.setIntranetBaseUri("http://localhost:14048/umc-admin");
			appInfoList.add(appInfo);
		}
		if (equalsAny("share-admin", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("share-admin", "http://localhost:14051");
			appInfo.setIntranetBaseUri("http://localhost:14051/share-admin");
			appInfoList.add(appInfo);
		}
		if (equalsAny("srm-admin", appNames)) {
			ApplicationInfo appInfo = new ApplicationInfo("srm-admin", "http://localhost:15050");
			appInfo.setIntranetBaseUri("http://localhost:15050/srm-admin");
			appInfoList.add(appInfo);
		}*/

		//
		// http://localhost:14041 # iam-example
		// http://localhost:14043 # scm-server
		// http://localhost:14046 # ci-server
		// http://localhost:14048 # umc-admin
		// http://localhost:14051 # share-admin
		// http://localhost:15050 # srm-admin
		//

		return appInfoList;
	}

	@Override
	public IamAccountInfo getIamAccount(Parameter parameter) {
		return new IamAccountInfo() {
			private static final long serialVersionUID = 7697544753707057845L;

			@Override
			public String getPrincipal() {
				return "admin";
			}

			@Override
			public String getStoredCredentials() {
				if (parameter instanceof SnsParameter) {
					SnsParameter snsParam = (SnsParameter) parameter;
					System.out.println(snsParam);
				} else {
					SimpleParameter simpleParam = (SimpleParameter) parameter;
					System.out.println(simpleParam);
					// for test
					return "5543f9567dcd41eca580a1724686a3a7"; // 123456
				}
				return null;
			}

		};
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

}