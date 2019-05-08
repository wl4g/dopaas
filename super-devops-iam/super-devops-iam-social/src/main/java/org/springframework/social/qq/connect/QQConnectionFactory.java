package org.springframework.social.qq.connect;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.qq.api.QQ;

/**
 * @author renq
 */
public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

	public QQConnectionFactory(String appId, String appSecret) {
		super("qq", new QQServiceProvider(appId, appSecret), new QQAdapter());
	}

}
