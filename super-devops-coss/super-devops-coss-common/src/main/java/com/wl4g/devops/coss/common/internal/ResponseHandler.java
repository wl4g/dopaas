package com.wl4g.devops.coss.common.internal;

import com.wl4g.devops.coss.common.exception.ClientCossException;
import com.wl4g.devops.coss.common.exception.CossException;

/**
 * {@link ResponseHandler}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public interface ResponseHandler {

	public void handle(ResponseMessage resp) throws CossException, ClientCossException;

}
