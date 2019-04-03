package com.wl4g.devops.iam.authc.credential;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Default account credential matcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class DefaultCredentialsHashedMatcher extends AbstractAttemptsMatcher {

	@Override
	public boolean doCustomMatch(AuthenticationToken token, AuthenticationInfo info) {
		return this.securer.validate((String) token.getPrincipal(), (String) token.getCredentials(),
				(String) info.getCredentials());
	}

}
