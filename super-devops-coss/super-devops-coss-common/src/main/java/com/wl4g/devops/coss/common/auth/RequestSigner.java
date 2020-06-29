package com.wl4g.devops.coss.common.auth;

import com.wl4g.devops.coss.common.exception.ClientCossException;
import com.wl4g.devops.coss.common.internal.RequestMessage;

/**
 * {@link RequestSigner}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public interface RequestSigner {

	/**
	 * Calc signature
	 * 
	 * @param request
	 * @throws ClientCossException
	 */
	public void sign(RequestMessage request) throws ClientCossException;

}
