package com.wl4g.devops.iam.common.authc;

import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * IAM authentication token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public interface IamAuthenticationToken extends HostAuthenticationToken {

	String getFromAppName();

	String getRedirectUrl();

}
