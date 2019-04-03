package com.wl4g.devops.iam.common.authc;

/**
 * Abstract IAM authentication token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public abstract class AbstractIamAuthenticationToken implements IamAuthenticationToken {

	private static final long serialVersionUID = 5483061935073949894L;

	/**
	 * Source application name
	 */
	final private String fromAppName;

	/**
	 * Source application callback URL
	 */
	final private String redirectUrl;

	public AbstractIamAuthenticationToken() {
		this.fromAppName = null;
		this.redirectUrl = null;
	}

	public AbstractIamAuthenticationToken(String fromAppName, String redirectUrl) {
		super();
		this.fromAppName = fromAppName;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public String getFromAppName() {
		return fromAppName;
	}

	@Override
	public String getRedirectUrl() {
		return redirectUrl;
	}

}
