package com.wl4g.devops.umc.store;

import com.wl4g.devops.common.bean.umc.model.physical.Cpu;
import com.wl4g.devops.common.bean.umc.model.physical.Disk;
import com.wl4g.devops.common.bean.umc.model.physical.Mem;
import com.wl4g.devops.common.bean.umc.model.physical.Net;

/**
 * Foundation(Memory/network/core/disk) monitor data store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public interface PhysicalMetricStore extends MetricStore {

	boolean save(Cpu cpu);

	boolean save(Mem mem);

	boolean save(Disk disk);

	boolean save(Net net);

}
