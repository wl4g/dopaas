package org.springframework.social.qq.connect;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.social.qq.api.QQ;
import org.springframework.social.qq.api.impl.QQTemplate;

/**
 * @author renq
 */
public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {

	private String appId;

	public QQServiceProvider(OAuth2Operations oauth2Operations) {
		super(oauth2Operations);
	}

	public QQServiceProvider(String appId, String appSecret) {
		super(getOAuth2Template(appId, appSecret));
		this.appId = appId;
	}

	private static OAuth2Template getOAuth2Template(String appId, String appSecret) {
		QQOAuth2Template oAuth2Template = new QQOAuth2Template(appId, appSecret);
		oAuth2Template.setUseParametersForClientAuthentication(true);
		return oAuth2Template;
	}

	@Override
	public QQ getApi(String accessToken) {
		return new QQTemplate(appId, accessToken);
	}

}
