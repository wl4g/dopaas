package com.wl4g.devops.umc.store;

import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;
import com.wl4g.devops.common.bean.umc.model.physical.Docker;

/**
 * Virtual(docker) containers monitor metric store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public interface VirtualMetricStore extends MetricStore {

	boolean save(PhysicalInfo baseTemple);

	boolean save(Docker docker);

}
