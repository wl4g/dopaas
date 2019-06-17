package com.wl4g.devops.umc.store.adapter;

import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;
import com.wl4g.devops.common.bean.umc.model.physical.Docker;
import com.wl4g.devops.umc.store.VirtualMetricStore;
import org.springframework.util.Assert;

import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Virtual(docker) monitor data metric store adapter
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class VirtualMetricStoreAdapter implements VirtualMetricStore {

	final protected List<VirtualMetricStore> metricStores;

	public VirtualMetricStoreAdapter(List<VirtualMetricStore> metricStores) {
		Assert.notEmpty(metricStores, "VirtualMetricStoreAdapter must not be empty");
		this.metricStores = unmodifiableList(metricStores.stream()
				.filter(s -> !VirtualMetricStoreAdapter.class.isAssignableFrom(s.getClass())).collect(toList()));
	}

	@Override
	public boolean save(PhysicalInfo info) {
		return false;
	}

	@Override
	public boolean save(Docker docker) {
		return false;
	}

}
