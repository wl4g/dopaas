package com.wl4g.devops.iam.handler;

import java.util.Map;

//import org.springframework.stereotype.Service;

import com.wl4g.devops.iam.handler.verification.SmsVerification.SmsHandleSender;

//@Service
public class AliyunSmsHandleSender implements SmsHandleSender {

	@Override
	public void doSend(Map<String, Object> parameters) {
		System.out.println("Do send sms message for :" + parameters);
	}

}
