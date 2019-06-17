package com.wl4g.devops.umc.opentsdb;

import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.store.VirtualMetricStore;

/**
 * Virtual(docker) openTSDB store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class TsdbVirtualMetricStore implements VirtualMetricStore {

	final protected OpenTSDBClient client;

	public TsdbVirtualMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(PhysicalInfo baseTemple) {
		return false;
	}

}
