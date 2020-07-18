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

import com.wl4g.devops.coss.common.exception.ClientCossException;
import com.wl4g.devops.coss.common.internal.RequestMessage;
import com.wl4g.devops.coss.common.internal.SignVersion;
import com.wl4g.devops.coss.common.utils.COSSHeaders;
import com.wl4g.devops.coss.common.utils.SignUtils;

/**
 * {@link DefaultRequestSigner}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public class DefaultRequestSigner implements RequestSigner {

	private String httpMethod;

	/* Note that resource path should not have been url-encoded. */
	private String resourcePath;

	private Credentials creds;

	private SignVersion signatureVersion;

	public DefaultRequestSigner(String httpMethod, String resourcePath, Credentials creds, SignVersion signatureVersion) {
		this.httpMethod = httpMethod;
		this.resourcePath = resourcePath;
		this.creds = creds;
		this.signatureVersion = signatureVersion;
	}

	@Override
	public void sign(RequestMessage request) throws ClientCossException {
		String accessKeyId = creds.getAccessKeyId();
		String secretAccessKey = creds.getSecretAccessKey();

		if (accessKeyId.length() > 0 && secretAccessKey.length() > 0) {
			String signature;

			if (signatureVersion == SignVersion.V2) {
				//TODO
//				signature = SignUtils.buildSignature(secretAccessKey, httpMethod, resourcePath, request);
//				request.addHeader(COSSHeaders.AUTHORIZATION,
//						SignUtils.composeRequestAuthorization(accessKeyId, signature, request));
			} else {
				//TODO
//				signature = SignUtils.buildSignature(secretAccessKey, httpMethod, resourcePath, request);
//				request.addHeader(COSSHeaders.AUTHORIZATION, SignUtils.composeRequestAuthorization(accessKeyId, signature));
			}
		}
	}

}