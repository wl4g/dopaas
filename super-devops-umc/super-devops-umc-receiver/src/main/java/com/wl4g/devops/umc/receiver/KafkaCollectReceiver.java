package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.umc.store.adapter.VirtualMetricStoreAdapter;
import com.wl4g.devops.umc.store.adapter.PhysicalMetricStoreAdapter;

/**
 * KAFKA collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class KafkaCollectReceiver extends AbstractCollectReceiver {

	public KafkaCollectReceiver(PhysicalMetricStoreAdapter pStore, VirtualMetricStoreAdapter vStore) {
		super(pStore, vStore);
	}

}
