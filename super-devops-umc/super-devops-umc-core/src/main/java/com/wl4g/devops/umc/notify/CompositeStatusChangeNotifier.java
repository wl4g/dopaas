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

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.support.notification.GenericNotifyMessage;
import com.wl4g.devops.support.notification.MessageNotifier;
import com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;
import com.wl4g.devops.support.notification.mail.MailMessageBuilder;
import com.wl4g.devops.support.notification.mail.MailMessageNotifier;
import com.wl4g.devops.umc.model.StatusMessage;
import org.springframework.beans.factory.annotation.Autowired;

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
	private GenericOperatorAdapter<NotifierKind, MessageNotifier> notifierAdapter;

	@Override
	protected void doNotify(StatusMessage status) {
		// 1.1 SMS notifier.
		/*
		 * try { log.debug("SMS通知... {}", status);
		 * smsHandle.send(status.getPhoneTo(), status.getAppInfo(),
		 * status.getFromStatus(), status.getToStatus(), status.getMsgId()); }
		 * catch (Exception e) { log.error("SMS notification failed.", e); }
		 */

		// 1.2 Mail notifier.
		try {
			GenericNotifyMessage msg = new GenericNotifyMessage();
			msg.addToObjects(getMailTo()).setTemplateKey("mailTpl1");
			MailMessageBuilder builder = new MailMessageBuilder().subject(getSubject());
			msg.addParameter(MailMessageNotifier.KEY_MAILMSG_SUBJECT, builder).addParameter("appName", status.getAppInfo());
			msg.addParameter("status", status.getToStatus()).addParameter("detailUrl", status.getDetailsUrl());
			notifierAdapter.forOperator(MailMessageNotifier.class).send(msg);
		} catch (Exception e) {
			log.error("Mail notification failed.", e);
		}
	}

}