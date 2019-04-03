package com.wl4g.devops.iam.client.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.filter.AbstractWhiteListInternalAuthenticationFilter;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_C_BASE;

import com.wl4g.devops.common.kit.access.IPAccessControl;

/**
 * Interactive authentication processing filter for internal and application
 * services
 * 
 * {@link org.apache.shiro.web.filter.authz.HostFilter}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月30日
 * @since
 */
@IamFilter
public class InternalWhiteListClientAuthenticationFilter extends AbstractWhiteListInternalAuthenticationFilter {
	final public static String NAME = "client-internal";

	public InternalWhiteListClientAuthenticationFilter(IPAccessControl control,
			AbstractIamProperties<? extends ParamProperties> config) {
		super(control, config);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return URI_C_BASE + "/**";
	}

}
