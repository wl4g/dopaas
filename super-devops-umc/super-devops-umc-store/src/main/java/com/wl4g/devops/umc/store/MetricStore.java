package com.wl4g.devops.umc.store;

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel;

/**
 * UMC metric store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public interface MetricStore {

    boolean save(MetricModel.MetricAggregate aggregate);

}
