package com.wl4g.devops.shell;

/**
 * Shell actuator
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月21日
 * @since
 */
public interface Actuator {

	/**
	 * Read commands to processing
	 * 
	 * @param line
	 * @Exception
	 */
	Object process(String line) throws Exception;

}
