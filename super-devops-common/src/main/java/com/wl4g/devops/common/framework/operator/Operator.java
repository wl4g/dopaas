package com.wl4g.devops.common.framework.operator;

/**
 * Generic operator adapter.
 * 
 * @param <K>
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @version 2020年1月6日 v1.0.0
 * @see
 */
public interface Operator<K extends Enum<?>> {

	/**
	 * Get the type of operator (kind).
	 *
	 * @return
	 */
	K kind();

}
