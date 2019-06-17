package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.physical.*;
import com.wl4g.devops.umc.store.adapter.VirtualMetricStoreAdapter;
import com.wl4g.devops.umc.store.adapter.PhysicalMetricStoreAdapter;

/**
 * Abstract collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public abstract class AbstractCollectReceiver implements CollectReceiver {

	/** Physical metric store adapter. */
	final protected PhysicalMetricStoreAdapter pStore;

	/** Virtual metric store adapter. */
	final protected VirtualMetricStoreAdapter vStore;

	public AbstractCollectReceiver(PhysicalMetricStoreAdapter pStore, VirtualMetricStoreAdapter vStore) {
		super();
		this.pStore = pStore;
		this.vStore = vStore;
	}

	//
	// Physical storage
	//

	protected void putPhysicalMem(Mem mem) {
		pStore.save(mem);
	}

	protected void putPhysicalCpu(Cpu cpu) {
		pStore.save(cpu);
	}

	protected void putPhysicalDisk(Disk disk) {
		pStore.save(disk);
	}

	protected void putPhysicalNet(Net net) {
		pStore.save(net);
	}

	//
	// Virtual storage
	//



	protected void putVirtualMem(Mem mem) {
		// TODO
	}

	protected void putVirtualCpu(Cpu cpu) {
		// TODO
	}

	protected void putVirtualDisk(Disk disk) {
		// TODO
	}

	protected void putVirtualNet(Net net) {
		// TODO
	}

	protected void putVirtualDocker(Docker docker) {
		vStore.save(docker);
	}

}
