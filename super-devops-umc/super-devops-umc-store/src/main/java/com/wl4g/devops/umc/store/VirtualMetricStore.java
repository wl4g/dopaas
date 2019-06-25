package com.wl4g.devops.umc.store;

import com.wl4g.devops.common.bean.umc.model.Base;
import com.wl4g.devops.common.bean.umc.model.virtual.Docker;

/**
 * Virtual(docker) containers monitor metric store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public interface VirtualMetricStore extends MetricStore {

	boolean save(Base baseTemple);

	boolean save(Docker docker);

}
