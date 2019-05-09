package org.springframework.social.wechat.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public class WechatAdapter<T> implements ApiAdapter<T> {

	@Override
	public boolean test(T api) {
		return false;
	}

	@Override
	public void setConnectionValues(T api, ConnectionValues values) {
	}

	@Override
	public UserProfile fetchUserProfile(T api) {
		return null;
	}

	@Override
	public void updateStatus(T api, String message) {
	}

}
