package com.wl4g.devops.umc.notify;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import com.wl4g.devops.umc.handle.MailNotificationHandle;
import com.wl4g.devops.umc.handle.SmsNotificationHandle;
import com.wl4g.devops.umc.model.StatusMessage;

/**
 * Composite status change notifier
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月8日
 * @since
 */
public class CompositeStatusChangeNotifier extends AbstractAdvancedNotifier {

	@Autowired
	private SmsNotificationHandle smsHandle;
	@Autowired
	private MailNotificationHandle mailHandle;

	@Override
	protected void doNotify(StatusMessage status) {
		// 1.1 SMS notifier.
		try {
			if (logger.isDebugEnabled())
				logger.debug("SMS通知... {}", status);

			this.smsHandle.send(status.getPhoneTo(), status.getAppInfo(), status.getFromStatus(), status.getToStatus(),
					status.getMsgId());
		} catch (Exception e) {
			logger.error("SMS notification failed.", e);
		}

		// 1.2 Mail notifier.
		try {
			StringBuffer content = new StringBuffer(status.getAppInfo());
			content.append(" ");
			content.append(status.getFromStatus());
			content.append(" to ");
			content.append(status.getToStatus());
			content.append(", See：");
			content.append(status.getDetailsUrl());

			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setSubject(getSubject());
			msg.setFrom(getFromName());
			msg.setTo(getMailTo());
			msg.setText(content.toString());
			msg.setSentDate(new Date());

			if (logger.isDebugEnabled())
				logger.debug("Mail通知... {}", status);

			this.mailHandle.send(msg);
		} catch (Exception e) {
			logger.error("Mail notification failed.", e);
		}
	}

}
