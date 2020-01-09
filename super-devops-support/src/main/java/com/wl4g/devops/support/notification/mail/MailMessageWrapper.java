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
package com.wl4g.devops.support.notification.mail;

import static java.util.Objects.nonNull;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMailMessage;

import com.wl4g.devops.support.notification.NotifyMessage;

public class MailMessageWrapper implements NotifyMessage {
	private static final long serialVersionUID = 2028478380512096634L;

	private SimpleMailMessage simpleMessage;

	private MimeMailMessage mimeMessage;

	public MailMessageWrapper(SimpleMailMessage simpleMessage) {
		super();
		this.simpleMessage = simpleMessage;
	}

	public MailMessageWrapper(MimeMailMessage mimeMessage) {
		super();
		this.mimeMessage = mimeMessage;
	}

	public boolean hasSimpleMessage() {
		return nonNull(getSimpleMessage());
	}

	public SimpleMailMessage getSimpleMessage() {
		return simpleMessage;
	}

	public boolean hasMimeMessage() {
		return nonNull(getMimeMessage());
	}

	public MimeMailMessage getMimeMessage() {
		return mimeMessage;
	}

}
