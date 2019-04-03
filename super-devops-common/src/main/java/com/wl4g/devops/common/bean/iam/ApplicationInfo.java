package com.wl4g.devops.common.bean.iam;

import java.io.Serializable;

import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

/**
 * Application information
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年12月15日
 * @since
 */
public class ApplicationInfo implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	/**
	 * Application name
	 */
	private String appName;

	/**
	 * View intranet Basic URI.<br/>
	 * e.g: https://portal.domain.com <br/>
	 */
	private String viewExtranetBaseUri;

	/**
	 * Intranet Basic URI.<br/>
	 * e.g: https://portal.domain.com/portal <br/>
	 */
	private String extranetBaseUri;

	/**
	 * Intranet Basic URI.<br/>
	 * e.g: http://192.168.210.101:8080/myapp <br/>
	 */
	private String intranetBaseUri;

	public ApplicationInfo() {
		super();
	}

	public ApplicationInfo(String appName, String extranetBaseUri) {
		Assert.hasText(appName, "'applicationName' must not be empty");
		Assert.hasText(extranetBaseUri, "'extranetBaseUri' must not be empty");
		this.setAppName(appName);
		this.setExtranetBaseUri(extranetBaseUri);
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String applicationName) {
		this.appName = applicationName;
	}

	public String getViewExtranetBaseUri() {
		return viewExtranetBaseUri;
	}

	public void setViewExtranetBaseUri(String viewExtranetBaseUri) {
		this.viewExtranetBaseUri = viewExtranetBaseUri;
	}

	public String getExtranetBaseUri() {
		return extranetBaseUri;
	}

	public void setExtranetBaseUri(String extranetBaseUri) {
		this.extranetBaseUri = extranetBaseUri;
	}

	public String getIntranetBaseUri() {
		return intranetBaseUri;
	}

	public void setIntranetBaseUri(String internalBaseUri) {
		this.intranetBaseUri = internalBaseUri;
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}
