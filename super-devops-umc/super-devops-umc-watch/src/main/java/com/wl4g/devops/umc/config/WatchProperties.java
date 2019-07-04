package com.wl4g.devops.umc.config;

import com.wl4g.devops.support.task.GenericTaskRunner.TaskProperties;

/**
 * Watch properties .
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2017年11月16日
 * @since
 */
public class WatchProperties {

	final public static String KEY_RECEIVER_PREFIX = "spring.cloud.devops.umc.watch";

	private TaskProperties watch = new TaskProperties();

	public WatchProperties() {
		this.watch.setConcurrency(1); // By default
	}

	public TaskProperties getWatch() {
		return watch;
	}

	public void setWatch(TaskProperties watch) {
		this.watch = watch;
	}

}
