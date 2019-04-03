package com.wl4g.devops.iam.session.mgt;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.wl4g.devops.iam.common.session.mgt.AbstractIamSessionManager;
import com.wl4g.devops.iam.config.IamProperties;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_TICKET_S;

/**
 * Custom WEB session manager
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class IamServerSessionManager extends AbstractIamSessionManager<IamProperties> {

	public IamServerSessionManager(IamProperties config) {
		super(config, CACHE_TICKET_S);
	}

	@Override
	protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
		return super.getSessionId(request, response);
	}

}