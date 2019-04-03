package com.wl4g.devops.iam.client.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.client.config.IamClientProperties.ClientParamProperties;

@ConfigurationProperties(prefix = "spring.cloud.devops.iam.client")
public class IamClientProperties extends AbstractIamProperties<ClientParamProperties> implements InitializingBean {
	private static final long serialVersionUID = -8848998112902613969L;

	/**
	 * IAM server base URI (public network)
	 */
	private String baseUri = "http://localhost:14040/devops-iam";

	/**
	 * Application name. Example: http://host:port/myapp/shiro-cas
	 */
	private String serviceName = "defaultApp";

	/**
	 * IAM server login page URI
	 */
	private String loginUri = "http://localhost:14040/iam/default-view/login.html";

	/**
	 * This success(index) page URI
	 */
	private String successUri = "http://localhost:8080/index";

	/**
	 * IAM server unauthorized(403) page URI
	 */
	private String unauthorizedUri = "http://localhost:14040/iam/default-view/403.html";

	/**
	 * Re-login callback URL, whether to use the previously remembered URL
	 */
	private boolean useRememberRedirect = false;

	/**
	 * Secondary authenticator provider name.
	 */
	private String secondAuthenticatorProvider = "wechat";

	/**
	 * Filter chains.
	 */
	private Map<String, String> filterChain = new HashMap<>();

	/**
	 * IAM client parameters configuration properties.
	 */
	private ClientParamProperties param = new ClientParamProperties();

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String getLoginUri() {
		return loginUri;
	}

	public void setLoginUri(String loginUri) {
		this.loginUri = WebUtils2.cleanURI(loginUri);
	}

	@Override
	public String getSuccessUri() {
		return successUri;
	}

	public void setSuccessUri(String successUri) {
		this.successUri = successUri;
	}

	@Override
	public String getUnauthorizedUri() {
		return unauthorizedUri;
	}

	public void setUnauthorizedUri(String unauthorizedUri) {
		this.unauthorizedUri = unauthorizedUri;
	}

	public boolean isUseRememberRedirect() {
		return useRememberRedirect;
	}

	public void setUseRememberRedirect(boolean useRememberRedirect) {
		this.useRememberRedirect = useRememberRedirect;
	}

	public String getSecondAuthenticatorProvider() {
		return secondAuthenticatorProvider;
	}

	public void setSecondAuthenticatorProvider(String secondAuthcProvider) {
		this.secondAuthenticatorProvider = secondAuthcProvider;
	}

	public Map<String, String> getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(Map<String, String> filterChain) {
		this.filterChain = filterChain;
	}

	@Override
	public ClientParamProperties getParam() {
		return param;
	}

	@Override
	public void setParam(ClientParamProperties param) {
		this.param = param;
	}

	public void validation() {
		Assert.notNull(this.getBaseUri(), "'baseUri' must be null");
		Assert.notNull(this.getServiceName(), "'serviceName' must be null");
		Assert.notNull(this.getFilterChain(), "'filterChain' must be null");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Validate attributes
		this.validation();
	}

	/**
	 * IAM client parameters configuration properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月29日
	 * @since
	 */
	public static class ClientParamProperties extends ParamProperties {
		private static final long serialVersionUID = 3258460473777285504L;

	}

}
