package com.wl4g.devops.iam.sns.support;

public interface Oauth2OpenId {

	String openId();

	String unionId();

	<O extends Oauth2OpenId> O build(String message);

}
