package com.wl4g.devops.ci.config;

import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;

/**
 * Task executor configuration such as CICD pipeline process
 * construction/compilation/forwarding.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class ExecutorProperties extends RunProperties {
	private static final long serialVersionUID = -7007748978859003620L;

	public ExecutorProperties() {
		// By default.
		setConcurrency(5);
		setAcceptQueue(32);
	}

}