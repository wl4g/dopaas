package com.wl4g.devops.components.tools.common.function;

/**
 * Generic processor of function.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月7日 v1.0.0
 * @see {@link java.util.function.Function}
 */
public interface ProcessFunction<T, R> {

	/**
	 * Do processing.
	 * 
	 * @param t
	 * @throws Exception
	 */
	R process(T t) throws Exception;

}
