package com.wl4g.devops.iam.common.filter;

public interface IamAuthenticationFilter {

	/**
	 * Get URI mapping
	 * {@link com.wl4g.devops.iam.common.config.AbstractIamConfiguration#shiroFilter}
	 * 
	 * @return
	 */
	String getUriMapping();

}
