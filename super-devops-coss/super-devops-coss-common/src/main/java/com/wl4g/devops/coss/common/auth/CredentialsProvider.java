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
package com.wl4g.devops.coss.common.auth;

/**
 * Abstract credentials provider that maintains only one user credentials. Users
 * can switch to other valid credentials with
 * {@link OSS#switchCredentials(com.aliyun.oss.common.auth.Credentials)} Note
 * that <b>implementations of this interface must be thread-safe.</b>
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public interface CredentialsProvider {

	/**
	 * Gets credentials.
	 * 
	 * @return
	 */
	public Credentials getCredentials();

}