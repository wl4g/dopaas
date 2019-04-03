package com.wl4g.devops.iam.sns.support;

/**
 * OAuth2 response_type definition
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月3日
 * @since
 * @see https://www.cnblogs.com/giserliu/p/4372455.html
 */
public enum OAuth2ResponseType {

	CODE(true), TOKEN;

	private boolean isDefault = false;

	private OAuth2ResponseType() {
	}

	private OAuth2ResponseType(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public static OAuth2ResponseType getDefault() {
		OAuth2ResponseType defaultResponseType = null;
		for (OAuth2ResponseType rt : values()) {
			if (rt.isDefault()) {
				if (defaultResponseType != null) {
					throw new IllegalStateException("There can only be one default value");
				}
				defaultResponseType = rt;
			}
		}
		return defaultResponseType;
	}

}
