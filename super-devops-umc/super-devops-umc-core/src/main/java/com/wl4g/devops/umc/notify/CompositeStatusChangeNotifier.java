/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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