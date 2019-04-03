package com.wl4g.devops.iam.authc.credential;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

import com.wl4g.devops.iam.authc.Oauth2SnsAuthenticationToken;

/**
 * Oauth2 are bound matcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class Oauth2AuthorizingBoundMatcher extends IamBasedMatcher {

	/**
	 * Oauth2 authorized matches.
	 * {@link com.wl4g.devops.iam.realm.Oauth2SnsAuthorizingRealm#doAuthenticationInfo}
	 */
	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		Oauth2SnsAuthenticationToken tk = (Oauth2SnsAuthenticationToken) token;
		if (info != null && !info.getPrincipals().isEmpty()) {
			return true;
		}
		if (log.isWarnEnabled()) {
			log.warn(String.format(
					"Logon failed, because social account provider[%s], openId[%s], unionId[%s], from [%s] unbound system account",
					tk.getSocial().getProvider(), tk.getSocial().getOpenId(), tk.getSocial().getUnionId(), tk.getHost()));
		}
		throw new NoSuchSocialBindException("Logon failed, because there is no system account bound yet");
	}

}
