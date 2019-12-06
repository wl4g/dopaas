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
package com.wl4g.devops.umc.notification.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import com.wl4g.devops.support.notification.mail.MailSenderTemplate;
import com.wl4g.devops.umc.notification.AbstractAlarmNotifier;
import com.wl4g.devops.umc.notification.AlarmType;

/**
 * @author vjay
 * @date 2019-06-10 15:10:00
 */
public class EmailNotifier extends AbstractAlarmNotifier {


	@Autowired
	private MailSenderTemplate mailHandle;

	@Override
	public AlarmType alarmType() {
		return AlarmType.EMAIL;
	}

	@Override
	public void simpleNotify(SimpleAlarmMessage message) {
		// TODO Auto-generated method stub
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("safec7782@sina.com"); // 设置显示的账号名(最终发送格式为: from显示名<from账号名>)
		msg.setSubject("UMC-Alarm");
		msg.setTo("1154635107@qq.com");
		msg.setText(message.getMessage());
		//msg.setSentDate(new Date());
		this.mailHandle.send(msg);

	}

	@Override
	public void templateNotify(TeampleAlarmMessage message) {
		// TODO Auto-generated method stub

	}

}