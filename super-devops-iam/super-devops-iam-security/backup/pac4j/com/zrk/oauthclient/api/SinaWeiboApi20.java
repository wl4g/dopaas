package com.zrk.oauthclient.api;

import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 新浪微博登录api
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:41:45
 */
public class SinaWeiboApi20 extends DefaultApi20 {
	private static final String AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s";
	private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

	@Override
	public String getAccessTokenEndpoint() {
		return "https://api.weibo.com/oauth2/access_token";
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback. sinaWeibo does not support OOB");

		// Append scope if present
		if (config.hasScope()) {
			return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()),
					OAuthEncoder.encode(config.getScope()));
		} else {
			return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
		}
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new AccessTokenExtractor() {
			private static final String EMPTY_SECRET = "";

			@Override
			public Token extract(String response) {
				Preconditions.checkEmptyString(response,
						"Response body is incorrect. Can't extract a token from an empty string");
				final JsonNode json = JsonHelper.getFirstNode(response);
				String token = (String) JsonHelper.get(json, "access_token");
				if (token != null && !"".equals(token)) {
					return new Token(token, EMPTY_SECRET, response);
				} else {
					throw new OAuthException("Response body is incorrect. Can't extract a token from this: '" + response + "'",
							null);
				}
			}
		};
	}
}