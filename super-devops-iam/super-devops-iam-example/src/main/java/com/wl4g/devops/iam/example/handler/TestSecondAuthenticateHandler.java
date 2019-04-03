package com.wl4g.devops.iam.example.handler;

import org.springframework.stereotype.Component;

import com.wl4g.devops.iam.client.authc.aop.SecondAuthenticateHandler;

@Component
public class TestSecondAuthenticateHandler implements SecondAuthenticateHandler {

	@Override
	public String[] doGetAuthorizers(String funcId) {
		if (funcId.equals("FunSensitiveApi")) {
			return new String[] { "bl001", "bl002", "admin" };
		}
		throw new IllegalStateException(String.format("Illegal function id [%s]", funcId));
	}

}
