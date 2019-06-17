package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.physical.*;
import com.wl4g.devops.umc.store.adapter.PhysicalMetricStoreAdapter;
import com.wl4g.devops.umc.store.adapter.VirtualMetricStoreAdapter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;

/**
 * HTTP collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@ResponseBody
@com.wl4g.devops.umc.annotation.HttpCollectReceiver
public class RESTFulCollectReceiver extends AbstractCollectReceiver {

	public RESTFulCollectReceiver(PhysicalMetricStoreAdapter pStore, VirtualMetricStoreAdapter vStore) {
		super(pStore, vStore);
	}

	@RequestMapping(URI_PHYSICAL_MEM)
	public void memPhysicalReceive(@RequestBody Mem mem) {
		putPhysicalMem(mem);
	}

	@RequestMapping(URI_PHYSICAL_CPU)
	public void cpuPhysicalReceive(@RequestBody Cpu cpu) {
		putPhysicalCpu(cpu);
	}

	@RequestMapping(URI_PHYSICAL_DISK)
	public void diskPhysicalReceive(@RequestBody Disk disk) {
		putPhysicalDisk(disk);
	}

	@RequestMapping(URI_PHYSICAL_NET)
	public void netPhysicalReceive(@RequestBody Net net) {
		putPhysicalNet(net);
	}


	@RequestMapping(URI_VIRTUAL_DOCKER)
	public void dockerReceive(@RequestBody Docker docker) {


		//putPhysicalNet(net);
	}





}
