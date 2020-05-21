package com.wl4g.devops.iam.configure;

import com.wl4g.devops.tool.common.log.SmartLogger;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.wl4g.devops.iam.common.utils.IamOrganizationUtils.setDefaultCurrentOrganization;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

@Service
public class StandardSecurityCoprocessor implements ServerSecurityCoprocessor {

	private SmartLogger log = getLogger(getClass());

	@Override
	public void postAuthenticatingSuccess(AuthenticationToken token, Subject subject, HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> respParams) {
		setDefaultCurrentOrganization();
	}




}
