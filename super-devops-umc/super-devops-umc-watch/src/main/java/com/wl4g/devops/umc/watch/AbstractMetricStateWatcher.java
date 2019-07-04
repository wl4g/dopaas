package com.wl4g.devops.umc.watch;

import java.util.List;

import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.TaskProperties;
import com.wl4g.devops.umc.config.WatchProperties;

/**
 * Metrics state indicators watcher of based.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public abstract class AbstractMetricStateWatcher extends GenericTaskRunner<TaskProperties> {

	public AbstractMetricStateWatcher(WatchProperties config) {
		super(config.getWatch());
	}

	@Override
	public void run() {

	}

	protected List<String> fetchMetricTargets() {
		return null;
	}

}
