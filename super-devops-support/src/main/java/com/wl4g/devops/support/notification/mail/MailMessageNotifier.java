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

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.Date;
import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;

import com.wl4g.devops.support.notification.AbstractMessageNotifier;
import com.wl4g.devops.support.notification.GenericNotifyMessage;

/**
 * {@link MailMessageNotifier}, Full compatibility with native spring mail!
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public class MailMessageNotifier extends AbstractMessageNotifier<MailNotifyProperties> {

	/**
	 * Java mail sender.
	 */
	final protected JavaMailSenderImpl mailSender;

	public MailMessageNotifier(MailNotifyProperties config) {
		super(config);
		this.mailSender = new JavaMailSenderImpl();
		if (!isNull(config.getProperties())) {
			this.mailSender.setJavaMailProperties(new Properties() {
				private static final long serialVersionUID = 1395782904610029089L;
				{
					putAll(config.getProperties());
				}
			});
		}
		this.mailSender.setDefaultEncoding(config.getDefaultEncoding().name());
		this.mailSender.setProtocol(config.getProtocol());
		this.mailSender.setHost(config.getHost());
		this.mailSender.setPort(config.getPort());
		this.mailSender.setUsername(config.getUsername());
		this.mailSender.setPassword(config.getPassword());
	}

	@Override
	public NotifierKind kind() {
		return NotifierKind.Mail;
	}

	/**
	 * Send mail messages.
	 * 
	 * @param simpleMessages
	 */
	@Override
	public void send(GenericNotifyMessage msg) {
		String mailMsgType = msg.getParameterAsString(KEY_MAILMSG_TYPE, "simple");
		switch (mailMsgType) {
		case "simple":
			SimpleMailMessage simpleMsg = new SimpleMailMessage();
			// Add "<>" symbol to send out?
			/*
			 * Preset from account, otherwise it would be wrong: 501 mail from
			 * address must be same as authorization user.
			 */
			simpleMsg.setFrom(simpleMsg.getFrom() + "<" + config.getUsername() + ">");
			simpleMsg.setSentDate(new Date());

			MailMessageBuilder builder = (MailMessageBuilder) msg.getParameter(KEY_MAILMSG_BUILDER, null);
			notNullOf(builder, "mailMessageBuilder");
			builder.copyTo(simpleMsg);

			mailSender.send(simpleMsg);
			break;
		case "mime": // TODO implements!!!
			log.warn("No implements MimeMailMessage!!!");
			break;
		default:
			throw new UnsupportedOperationException(format("No supported mail message type of %s", mailMsgType));
		}

	}

	@Override
	public <R> R sendForReply(GenericNotifyMessage message) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Send mail message type keyname. </br>
	 * 
	 * <pre>
	 * <b>simple</b> => {@link SimpleMailMessage}
	 * <b>mime</b> => {@link MimeMailMessage}
	 * </pre>
	 */
	final public static String KEY_MAILMSG_TYPE = "mailMsgType";

	/**
	 * Mail message builder keyname.
	 */
	final public static String KEY_MAILMSG_BUILDER = "mailMsgBuilder";

}
