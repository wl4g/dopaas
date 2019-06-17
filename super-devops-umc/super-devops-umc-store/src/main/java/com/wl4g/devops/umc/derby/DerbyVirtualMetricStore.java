package com.wl4g.devops.umc.derby;

import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;
import com.wl4g.devops.common.bean.umc.model.physical.Docker;
import com.wl4g.devops.umc.store.VirtualMetricStore;

/**
 * Derby Virtual(docker) store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class DerbyVirtualMetricStore implements VirtualMetricStore {

	@Override
	public boolean save(PhysicalInfo baseTemple) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean save(Docker docker) {
		throw new UnsupportedOperationException();
	}

}
