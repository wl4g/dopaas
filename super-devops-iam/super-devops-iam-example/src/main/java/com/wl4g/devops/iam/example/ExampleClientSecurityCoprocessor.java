package com.wl4g.devops.iam.example;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import com.wl4g.devops.common.exception.iam.AfterAuthenticatSuccessException;
import com.wl4g.devops.iam.client.context.ClientSecurityCoprocessor;

@Service
public class ExampleClientSecurityCoprocessor implements ClientSecurityCoprocessor {

	final public static String KEY_EXAMPLE_STORE_IN_SESSION = "exampleKey1";

	@Override
	public void postAuthenticatingSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws AfterAuthenticatSuccessException {
		// TODO Auto-generated method stub

		subject.getSession().setAttribute(KEY_EXAMPLE_STORE_IN_SESSION, "12345");
	}

}
