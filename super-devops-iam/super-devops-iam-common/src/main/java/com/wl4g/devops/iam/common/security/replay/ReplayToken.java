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
package com.wl4g.devops.iam.common.security.replay;

import java.io.Serializable;

/**
 * Replay attacks protection token.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月7日
 * @since
 * @see <a href=
 *      "https://help.aliyun.com/knowledge_detail/50041.html?spm=5176.13910061.0.0.4e9133d5y96BZW#h2-u4F7Fu7528u7B7Eu540Du9632u6B62u91CDu653Eu653Bu51FB2">API
 *      Replay Attacks</a>
 */
public interface ReplayToken extends Serializable {

	/**
	 * Unrepeatable request timestamp
	 * 
	 * @return
	 */
	Long getTimestamp();

	/**
	 * Unrepeatable request nonce
	 * 
	 * @return
	 */
	String getNonce();

	/**
	 * Unrepeatable request signature
	 * 
	 * @return
	 */
	String getSignature();

}