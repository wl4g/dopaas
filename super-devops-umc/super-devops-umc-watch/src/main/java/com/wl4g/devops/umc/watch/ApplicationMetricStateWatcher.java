package com.wl4g.devops.umc.watch;

import com.wl4g.devops.umc.config.WatchProperties;

/**
 * 
 * Application of system metrics status monitor, including but not limited to:
 * application of health status indicators such as connection redis, kafka, etc.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public class ApplicationMetricStateWatcher extends AbstractMetricStateWatcher {

	public ApplicationMetricStateWatcher(WatchProperties config) {
		super(config);
	}

}
