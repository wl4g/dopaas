package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.StatMetrics;
import com.wl4g.devops.common.bean.umc.model.third.KafkaStatInfo;
import com.wl4g.devops.common.bean.umc.model.third.RedisStatInfo;
import com.wl4g.devops.common.bean.umc.model.third.ZookeeperStatInfo;
import com.wl4g.devops.common.bean.umc.model.virtual.Docker;
import com.wl4g.devops.umc.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.common.bean.umc.model.physical.*;
import com.wl4g.devops.common.bean.umc.model.physical.Cpu;
import com.wl4g.devops.common.bean.umc.model.physical.Disk;
import com.wl4g.devops.common.bean.umc.model.physical.Mem;
import com.wl4g.devops.common.bean.umc.model.physical.Net;

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

	/** Redis metric store adapter. */
	final protected RedisMetricStore rStore;

	/** Zookeeper metric store adapter. */
	final protected ZookeeperMetricStore zStore;

	/** Kafka metric store adapter. */
	final protected KafkaMetricStore kStore;


	/** Kafka metric store adapter. */
	final protected StatInfoMetricStore mStore;

	public AbstractCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore
			, RedisMetricStore rStore, ZookeeperMetricStore zStore, KafkaMetricStore kStore,StatInfoMetricStore mStore) {

		super();
		this.pStore = pStore;
		this.vStore = vStore;
		this.rStore = rStore;
		this.zStore = zStore;
		this.kStore = kStore;
		this.mStore = mStore;
	}

	//
	// Physical storage
	//

	protected void putPhysical(PhysicalStatInfo physical) {
		if (physical.getMemInfo() != null) {
			Mem mem = new Mem();
			mem.setPhysicalId(physical.getPhysicalId());
			mem.setType(physical.getType());
			mem.setMemInfo(physical.getMemInfo());
			putPhysicalMem(mem);
		}
		if (physical.getCpu() != null) {
			Cpu cpu = new Cpu();
			cpu.setPhysicalId(physical.getPhysicalId());
			cpu.setType(physical.getType());
			cpu.setCpu(physical.getCpu());
			putPhysicalCpu(cpu);
		}
		if (physical.getDiskInfos() != null) {
			Disk disk = new Disk();
			disk.setPhysicalId(physical.getPhysicalId());
			disk.setType(physical.getType());
			disk.setDiskInfos(physical.getDiskInfos());
			putPhysicalDisk(disk);
		}
		if (physical.getNetInfos() != null) {
			Net net = new Net();
			net.setPhysicalId(physical.getPhysicalId());
			net.setType(physical.getType());
			net.setNetInfos(physical.getNetInfos());
			putPhysicalNet(net);
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
	protected void putVirtualDocker(Docker docker) {
		vStore.save(docker);
	}

	//
	// third storage
	//
	// redis
	protected void putRedis(RedisStatInfo redis) {
		rStore.save(redis);
	}

	// zookeeper
	protected void putZookeeper(ZookeeperStatInfo zookeeper) {
		zStore.save(zookeeper);
	}

	// kafka
	protected void putKafka(KafkaStatInfo kafka) {
		kStore.save(kafka);
	}


	//kafka
	protected void putMetrics(StatMetrics statMetrics){
		mStore.save(statMetrics);
	}

}
