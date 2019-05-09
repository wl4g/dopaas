package org.springframework.social.qq.api.impl;

import org.springframework.social.qq.api.QQ;
import org.springframework.social.qq.api.UserOperations;
import org.springframework.social.qq.api.model.UserInfo;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class UserTemplate extends AbstractQQApiBinding implements UserOperations {

	private final String appId;
	private final String accessToken;
	private final String openId;

	private static final String URI_USER_INFO = "https://graph.qq.com/user/get_user_info";

	public UserTemplate(QQ api, RestTemplate restTemplate, boolean isAuthorized, String appId, String openId,
			String accessToken) {
		super(api, restTemplate, isAuthorized);
		this.appId = appId;
		this.accessToken = accessToken;
		this.openId = openId;
	}

	@Override
	public UserInfo getUserInfo() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("access_token", accessToken);
		params.set("oauth_consumer_key", appId);
		params.set("openid", openId);
		params.set("format", "json");
		String userInfoJson = this.getRestTemplate().getForObject(URIBuilder.fromUri(URI_USER_INFO).queryParams(params).build(),
				String.class);
		if (StringUtils.isEmpty(userInfoJson)) {
			return null;
		}
		// JSONObject jsonObject = JSON.parseObject(userInfoJson);
		// if (jsonObject != null && !jsonObject.isEmpty()) {
		// String ret = "ret";
		// String gender = "男";
		// if (jsonObject.containsKey(ret)) {
		// if (jsonObject.getIntValue(ret) == 0) {
		// userInfo.setName(jsonObject.containsKey("nickname") ?
		// jsonObject.getString("nickname") : "");
		// userInfo.setFigureUrl(jsonObject.containsKey("figureurl_qq_2") ?
		// jsonObject.getString("figureurl_qq_2") : "");
		// if (StringUtils.isEmpty(userInfo.getFigureUrl())) {
		// userInfo.setFigureUrl(
		// jsonObject.containsKey("figureurl_qq_1") ?
		// jsonObject.getString("figureurl_qq_1") : "");
		// }
		// userInfo.setSex(jsonObject.containsKey("gender") &&
		// gender.equals(jsonObject.getString("sex")) ? "1" : "0");
		// userInfo.setOpenid(openId);
		// return userInfo;
		// }
		// }
		// }
		return JacksonUtils.parseJSON(userInfoJson, UserInfo.class);
	}
}
