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
package com.wl4g.devops.iam.context;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import com.wl4g.devops.common.bean.iam.ApplicationInfo;
import com.wl4g.devops.common.bean.iam.IamAccountInfo;
import com.wl4g.devops.common.bean.iam.SocialConnectInfo;
import com.wl4g.devops.common.bean.iam.IamAccountInfo.Parameter;
import com.wl4g.devops.iam.context.ServerSecurityContext;

/**
 * Standard IAM Security context handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月29日
 * @since
 */
@Service
public class StandardSecurityHandlerContext implements ServerSecurityContext {

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
		// For devops-iam-example.
		ApplicationInfo appInfo = new ApplicationInfo(appName, "localhost");
		appInfo.setIntranetBaseUri("http://localhost:14041/iam-example");
		return appInfo;
	}

	@Override
	public List<ApplicationInfo> findApplicationInfo(String... application) {
		List<ApplicationInfo> appInfoList = new ArrayList<>();
		// For test.
		ApplicationInfo appInfo = new ApplicationInfo("devops-iam-example", "localhost");
		appInfo.setIntranetBaseUri("http://localhost:14041/devops-iam-example");
		appInfoList.add(appInfo);
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