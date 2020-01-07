package com.wl4g.devops.tool.common.function;

/**
 * Generic callback of function.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月7日 v1.0.0
 * @see {@link java.util.function.Function}
 */
public interface CallbackFunction<T> {

	/**
	 * Do processing.
	 * 
	 * @param t
	 * @throws Exception
	 */
	void process(T t) throws Exception;

}
