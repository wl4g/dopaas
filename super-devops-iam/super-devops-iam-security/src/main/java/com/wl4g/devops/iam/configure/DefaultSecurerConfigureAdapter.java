/*
 * Copyright 2015 the original author or authors.
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

public class DefaultSecurerConfigureAdapter implements SecurerConfigureAdapter {

	@Override
	public SecurerConfig configure() {
		return new SecurerConfig(new String[] { "MD5", "SHA-256", "SHA-384", "SHA-512" }, "IAM", 5, 2 * 60 * 60 * 1000L,
				3 * 60 * 1000L);
	}

}