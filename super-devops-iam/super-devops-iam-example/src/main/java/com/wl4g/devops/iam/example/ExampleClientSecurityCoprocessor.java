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
package com.wl4g.devops.iam.example;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import com.wl4g.devops.common.exception.iam.AfterAuthenticatSuccessException;
import com.wl4g.devops.iam.client.configure.ClientSecurityCoprocessor;

@Service
public class ExampleClientSecurityCoprocessor implements ClientSecurityCoprocessor {

	final public static String KEY_EXAMPLE_STORE_IN_SESSION = "exampleKey1";

	@Override
	public void postAuthenticatingSuccess(AuthenticationToken token, Subject subject, HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> respParams) throws AfterAuthenticatSuccessException {
		// TODO Auto-generated method stub
		subject.getSession().setAttribute(KEY_EXAMPLE_STORE_IN_SESSION, "12345");
	}

}