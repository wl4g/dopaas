package com.wl4g.devops.umc.store.adapter;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.umc.model.physical.Cpu;
import com.wl4g.devops.common.bean.umc.model.physical.Disk;
import com.wl4g.devops.common.bean.umc.model.physical.Mem;
import com.wl4g.devops.common.bean.umc.model.physical.Net;
import com.wl4g.devops.umc.store.PhysicalMetricStore;

/**
 * Foundation metric store adapter
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class PhysicalMetricStoreAdapter implements PhysicalMetricStore {

	final protected List<PhysicalMetricStore> metricStores;

	public PhysicalMetricStoreAdapter(List<PhysicalMetricStore> metricStores) {
		Assert.notEmpty(metricStores, "FoundationMetricStores must not be empty");
		this.metricStores = unmodifiableList(metricStores.stream()
				.filter(s -> !PhysicalMetricStoreAdapter.class.isAssignableFrom(s.getClass())).collect(toList()));
	}

	@Override
	public boolean save(Cpu cpu) {
		return false;
	}

	@Override
	public boolean save(Mem mem) {
		return false;
	}

	@Override
	public boolean save(Disk disk) {
		return false;
	}

	@Override
	public boolean save(Net net) {
		return false;
	}

}
