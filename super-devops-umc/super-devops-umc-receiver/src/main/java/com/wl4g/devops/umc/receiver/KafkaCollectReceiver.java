package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.umc.store.PhysicalMetricStore;
import com.wl4g.devops.umc.store.VirtualMetricStore;

/**
 * KAFKA collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class KafkaCollectReceiver extends AbstractCollectReceiver {

	public KafkaCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore) {
		super(pStore, vStore);
	}

}
