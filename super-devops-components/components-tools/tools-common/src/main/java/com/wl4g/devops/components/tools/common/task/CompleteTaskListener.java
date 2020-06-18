package com.wl4g.devops.components.tools.common.task;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

/**
 * Wait completion task listener.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月17日
 * @since
 */
public interface CompleteTaskListener {

	/**
	 * Call-back completion listener.
	 * 
	 * @param ex
	 * @param completed
	 * @param uncompleteds
	 * @throws Exception
	 */
	void onComplete(TimeoutException ex, long completed, Collection<Runnable> uncompleteds) throws Exception;

}
