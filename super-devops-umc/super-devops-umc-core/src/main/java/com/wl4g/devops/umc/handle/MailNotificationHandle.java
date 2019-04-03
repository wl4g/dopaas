package com.wl4g.devops.umc.handle;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailNotificationHandle {
	final private Logger log = LoggerFactory.getLogger(getClass());

	@Value("${spring.mail.username}")
	private String fromUser;
	@Autowired
	private JavaMailSender mailSender;

	public void send(SimpleMailMessage... simpleMessages) {
		StringBuffer msgs = new StringBuffer();
		try {
			// Preset from account, otherwise it would be wrong: 501 mail from
			// address must be same as authorization user.
			for (SimpleMailMessage msg : simpleMessages) {
				msgs.append(msg.getText());
				msgs.append(",");
				msg.setFrom(msg.getFrom() + "<" + fromUser + ">"); // 要加“<>”这种格式才能发出去
			}

			// Do-send.
			this.mailSender.send(simpleMessages);

		} catch (Exception e) {
			log.error("Mail发送异常. request: {} {}", msgs.toString(), ExceptionUtils.getRootCauseMessage(e));
		}
	}

}
