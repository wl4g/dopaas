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
package com.wl4g.devops.iam.common.authz.permission;

public class GenericWildcardPermissionTests {

	public static void main(String[] args) {
		EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci:task,ci:schedule");
		EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci,ci:task,iam,iam:online,umc,umc:alarm");
		System.out.println(define1.implies(own1));
	}
}
