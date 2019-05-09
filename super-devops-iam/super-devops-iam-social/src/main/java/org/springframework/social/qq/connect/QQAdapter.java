package org.springframework.social.qq.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.qq.api.QQ;
import org.springframework.social.qq.api.model.UserInfo;

public class QQAdapter implements ApiAdapter<QQ> {

	@Override
	public boolean test(QQ qq) {
		try {
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void setConnectionValues(QQ qq, ConnectionValues connectionValues) {
		UserInfo userInfo = qq.userOperations().getUserInfo();
		connectionValues.setProviderUserId(userInfo.getOpenid());
		connectionValues.setDisplayName(userInfo.getName());
		connectionValues.setImageUrl(userInfo.getFigureUrl());
		connectionValues.setProfileUrl(null);
	}

	@Override
	public UserProfile fetchUserProfile(QQ qq) {
		UserInfo userInfo = qq.userOperations().getUserInfo();
		return new UserProfileBuilder().setName(userInfo.getName()).setUsername("QQ_" + userInfo.getOpenid()).build();
	}

	@Override
	public void updateStatus(QQ qq, String s) {

	}
}
