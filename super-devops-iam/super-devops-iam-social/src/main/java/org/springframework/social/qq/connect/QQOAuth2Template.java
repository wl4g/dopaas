package org.springframework.social.qq.connect;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

public class QQOAuth2Template extends OAuth2Template {

	private static final String URI_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
	private static final String URI_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";

	public QQOAuth2Template(String appId, String appSecret) {
		super(appId, appSecret, URI_AUTHORIZE, URI_ACCESS_TOKEN);
		setUseParametersForClientAuthentication(true);
	}

	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		return this.extractAccessGrant(this.getRestTemplate().postForObject(accessTokenUrl, parameters, String.class));
	}

	private AccessGrant extractAccessGrant(String result) {
		String[] items = result.split("&");

		String accessToken = items[0].split("=")[1];
		Long expiresIn = new Long(items[1].split("=")[1]);
		String refreshToken = items[2].split("=")[1];
		return this.createAccessGrant(accessToken, null, refreshToken, expiresIn, null);
	}

	@Override
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = super.createRestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
	}

}
