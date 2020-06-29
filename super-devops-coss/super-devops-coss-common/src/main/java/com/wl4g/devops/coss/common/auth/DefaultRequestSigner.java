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
