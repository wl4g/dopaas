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
package com.wl4g.devops.scm.example.service;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wl4g.components.common.log.SmartLogger;

public class ExampleService {
	final protected SmartLogger log = getLogger(getClass());

	@Value("#{'${example.firstName:unname}'.toUpperCase()}-auto-${random.int(1000)}")
	private String firstName;

	@Autowired
	private LastNameBean lastName;

	public String getFirstName() {
		return firstName;
	}

	public LastNameBean getLastName() {
		return lastName;
	}

	public static class LastNameBean {

		private String lastName;

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}

}