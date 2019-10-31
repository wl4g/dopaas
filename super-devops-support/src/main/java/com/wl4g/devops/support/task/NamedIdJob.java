package com.wl4g.devops.support.task;

import static org.springframework.util.Assert.hasText;

/**
 * Named ID job runnable.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月17日
 * @since
 */
public class NamedIdJob implements Runnable {

	/** Job namedId. */
	final private String id;

	public NamedIdJob(String id) {
		hasText(id, "Named ID must not be empty.");
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public void run() {
		// Ignore
	}

	@Override
	public String toString() {
		return "NamedIdJob@" + id;
	}

}