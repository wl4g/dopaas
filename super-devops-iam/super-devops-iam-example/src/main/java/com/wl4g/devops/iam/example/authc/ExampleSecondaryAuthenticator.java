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
package com.wl4g.devops.iam.example.authc;

import org.springframework.stereotype.Component;

import com.wl4g.devops.iam.client.authc.secondary.SecondaryAuthenticator;

@Component
public class ExampleSecondaryAuthenticator implements SecondaryAuthenticator {

	@Override
	public String[] doGetAuthorizers(String funcId) {
		if (funcId.equals("funSensitiveApi")) {
			return new String[] { "bl001", "bl002", "admin" };
		}
		throw new IllegalStateException(String.format("Illegal function id [%s]", funcId));
	}

}