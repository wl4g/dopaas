package com.wl4g.devops.umc.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.common.bean.umc.model.physical.*;
import com.wl4g.devops.common.bean.umc.model.physical.Cpu;
import com.wl4g.devops.common.bean.umc.model.physical.Disk;
import com.wl4g.devops.common.bean.umc.model.physical.Mem;
import com.wl4g.devops.common.bean.umc.model.physical.Net;
import com.wl4g.devops.umc.store.PhysicalMetricStore;
import com.wl4g.devops.umc.store.VirtualMetricStore;

/**
 * Abstract collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public abstract class AbstractCollectReceiver implements CollectReceiver {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Physical metric store adapter. */
	final protected PhysicalMetricStore pStore;

	/** Virtual metric store adapter. */
	final protected VirtualMetricStore vStore;

	public AbstractCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore) {
		super();
		this.pStore = pStore;
		this.vStore = vStore;
	}

	//
	// Physical storage
	//

	protected void putPhysical(Total total) {
		if (total.getMemInfo() != null) {
			Mem mem = new Mem();
			mem.setPhysicalId(total.getPhysicalId());
			mem.setType(total.getType());
			mem.setMemInfo(total.getMemInfo());
			putPhysicalMem(mem);
		}
		if (total.getCpu() != null) {
			Cpu cpu = new Cpu();
			cpu.setPhysicalId(total.getPhysicalId());
			cpu.setType(total.getType());
			cpu.setCpu(total.getCpu());
			putPhysicalCpu(cpu);
		}
		if (total.getDiskInfos() != null) {
			Disk disk = new Disk();
			disk.setPhysicalId(total.getPhysicalId());
			disk.setType(total.getType());
			disk.setDiskInfos(total.getDiskInfos());
			putPhysicalDisk(disk);
		}
		if (total.getNetInfos() != null) {
			Net net = new Net();
			net.setPhysicalId(total.getPhysicalId());
			net.setType(total.getType());
			net.setNetInfos(total.getNetInfos());
			putPhysicalNet(net);
		}
		if (total.getDockerInfo() != null) {
			Docker docker = new Docker();
			docker.setPhysicalId(total.getPhysicalId());
			docker.setType(total.getType());
			docker.setDockerInfo(total.getDockerInfo());
			putVirtualDocker(docker);
		}
	}

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
