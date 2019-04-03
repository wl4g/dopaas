package com.wl4g.devops.scm.client.configure.refresh;

import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseMeta;

public interface BeanRefresher {

	/**
	 * {@link com.wl4g.devops.scm.client.configure.watch.ZookeeperRefreshWatcher}
	 * {@link com.wl4g.devops.scm.client.configure.watch.TaskRefreshWatcher}
	 * 
	 * @param releaseMeta
	 *            release meta information.
	 */
	void refresh(ReleaseMeta releaseMeta);

	boolean isBeanCurrentlyInConfigure(Object bean);

}
