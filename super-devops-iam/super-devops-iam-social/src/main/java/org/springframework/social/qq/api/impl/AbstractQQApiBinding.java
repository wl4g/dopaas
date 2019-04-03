package org.springframework.social.qq.api.impl;

import java.net.URI;

import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.qq.api.QQ;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class AbstractQQApiBinding extends AbstractOAuth2ApiBinding {
	final protected static String API_URL = "https://graph.qq.com/";

	private final boolean isAuthorized;
	protected final RestTemplate restTemplate;
	protected final QQ api;

	public AbstractQQApiBinding(QQ api, RestTemplate restTemplate, boolean isAuthorized) {
		this.api = api;
		this.restTemplate = restTemplate;
		this.isAuthorized = isAuthorized;
	}

	protected void requireAuthorization() {
		if (!isAuthorized) {
			throw new MissingAuthorizationException("qq");
		}
	}

	protected URI buildUrl(String url) {
		return buildUrl(url, new LinkedMultiValueMap<String, String>());
	}

	protected URI buildUrl(String url, MultiValueMap<String, String> parameters) {
		return URIBuilder.fromUri(API_URL + url).queryParams(parameters).build();
	}

	protected URI buildUrl(String url, String name, String value) {
		return URIBuilder.fromUri(API_URL + url).queryParam(name, value).build();
	}

}
