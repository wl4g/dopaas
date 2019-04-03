package com.wl4g.devops.iam.sns.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;

/**
 * Social networking services handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月7日
 * @since
 */
public interface SnsHandler {

	/**
	 * Getting request SNS authorizing URL
	 * 
	 * @param which
	 * @param provider
	 * @param state
	 * @param connectParams
	 * @return
	 */
	String connect(Which which, String provider, String state, Map<String, String> connectParams);

	/**
	 * SNS authorizing callback
	 * 
	 * @param which
	 * @param provider
	 * @param state
	 * @param code
	 * @param request
	 * @return
	 */
	String callback(Which which, String provider, String state, String code, HttpServletRequest request);

	/**
	 * Handling which(action) type
	 * 
	 * @return
	 */
	Which whichType();

}
