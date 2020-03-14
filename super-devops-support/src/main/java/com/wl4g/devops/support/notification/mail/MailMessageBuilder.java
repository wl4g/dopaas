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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.springframework.mail.MailMessage;
import org.springframework.util.Assert;

/**
 * {@link MailMessageBuilder}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月14日 v1.0.0
 * @see
 */
public class MailMessageBuilder implements Serializable {
	private static final long serialVersionUID = -3109825232296490237L;

	private String replyTo;

	private String[] to;

	private String[] cc;

	private String[] bcc;

	private String subject;

	private Date sentDate;

	/**
	 * Create a new {@code SimpleMailMessage}.
	 */
	public MailMessageBuilder() {
	}

	public MailMessageBuilder setReplyTo(String replyTo) {
		this.replyTo = replyTo;
		return this;
	}

	public String getReplyTo() {
		return this.replyTo;
	}

	public MailMessageBuilder setTo(String... to) {
		this.to = to;
		return this;
	}

	public String[] getTo() {
		return this.to;
	}

	public MailMessageBuilder setCc(String... cc) {
		this.cc = cc;
		return this;
	}

	public String[] getCc() {
		return this.cc;
	}

	public MailMessageBuilder setBcc(String... bcc) {
		this.bcc = bcc;
		return this;
	}

	public String[] getBcc() {
		return this.bcc;
	}

	public MailMessageBuilder setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public String getSubject() {
		return this.subject;
	}

	public MailMessageBuilder setSentDate(Date sentDate) {
		this.sentDate = sentDate;
		return this;
	}

	public Date getSentDate() {
		return this.sentDate;
	}

	/**
	 * Copy the contents of this message to the given target message.
	 * 
	 * @param target
	 *            the {@code MailMessage} to copy to
	 */
	public void copyTo(MailMessage target) {
		Assert.notNull(target, "'target' MailMessage must not be null");
		if (getReplyTo() != null) {
			target.setReplyTo(getReplyTo());
		}
		if (getTo() != null) {
			target.setTo(copy(getTo()));
		}
		if (getCc() != null) {
			target.setCc(copy(getCc()));
		}
		if (getBcc() != null) {
			target.setBcc(copy(getBcc()));
		}
		if (getSubject() != null) {
			target.setSubject(getSubject());
		}
		if (getSentDate() != null) {
			target.setSentDate(getSentDate());
		}

	}

	private static String[] copy(String[] state) {
		String[] copy = new String[state.length];
		System.arraycopy(state, 0, copy, 0, state.length);
		return copy;
	}

	@Override
	public String toString() {
		return "MailMessageBuilder [replyTo=" + replyTo + ", to=" + Arrays.toString(to) + ", cc=" + Arrays.toString(cc) + ", bcc="
				+ Arrays.toString(bcc) + ", subject=" + subject + "]";
	}

}
