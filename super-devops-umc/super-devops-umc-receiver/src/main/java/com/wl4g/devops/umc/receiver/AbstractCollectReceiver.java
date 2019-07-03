package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel;
import com.wl4g.devops.umc.store.MetricStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public abstract class AbstractCollectReceiver implements CollectReceiver {

	final protected Logger log = LoggerFactory.getLogger(getClass());



	/** metric store adapter. */
	final protected MetricStore store;

	public AbstractCollectReceiver(MetricStore store) {

		super();
		this.store = store;

	}

	//kafka
	protected void putMetrics(MetricModel.MetricAggregate aggregate){
		store.save(aggregate);
	}

}
