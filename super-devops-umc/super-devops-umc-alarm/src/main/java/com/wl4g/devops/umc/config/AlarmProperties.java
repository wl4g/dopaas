package com.wl4g.devops.umc.config;

import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;

/**
 * Alarm properties.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class AlarmProperties extends RunProperties {
	private static final long serialVersionUID = -3690593536229115411L;

	public AlarmProperties() {
		// No need to start the asynchronous running of the boss.
		setAsync(false);
	}

}
