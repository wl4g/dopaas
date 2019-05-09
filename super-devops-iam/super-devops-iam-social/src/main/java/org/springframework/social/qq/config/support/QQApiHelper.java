package org.springframework.social.qq.config.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.xml.ApiHelper;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.qq.api.QQ;

public class QQApiHelper implements ApiHelper<QQ> {

	private final static Log logger = LogFactory.getLog(QQApiHelper.class);

	private final UsersConnectionRepository usersConnectionRepository;

	private final UserIdSource userIdSource;

	public QQApiHelper(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
		this.usersConnectionRepository = usersConnectionRepository;
		this.userIdSource = userIdSource;
	}

	@Override
	public QQ getApi() {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting API binding instance for QQ");
		}

		Connection<QQ> connection = usersConnectionRepository.createConnectionRepository(userIdSource.getUserId())
				.findPrimaryConnection(QQ.class);
		if (logger.isDebugEnabled() && connection == null) {
			logger.debug("No current connection; Returning default QQTemplate instance.");
		}
		return connection != null ? connection.getApi() : null;
	}

}
