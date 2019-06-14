package com.wl4g.devops.umc.endpoint;

import com.wl4g.devops.common.bean.umc.temple.basics.Cpu;
import com.wl4g.devops.common.bean.umc.temple.basics.Disk;
import com.wl4g.devops.common.bean.umc.temple.basics.Mem;
import com.wl4g.devops.common.bean.umc.temple.basics.Net;
import com.wl4g.devops.common.constants.UMCDevOpsConstants;
import com.wl4g.devops.umc.store.opentsdb.BasicOpentsdb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vjay
 * @date 2019-06-11 21:29:00
 */
@RestController
@RequestMapping(UMCDevOpsConstants.URI_BASIC)
public class GatherController {

    @Autowired
    private BasicOpentsdb basicOpentsdb;

    @RequestMapping(UMCDevOpsConstants.URI_BASIC_MEM)
    public void mem(@RequestBody Mem mem){
        System.out.println(mem);
    }

    @RequestMapping(UMCDevOpsConstants.URI_BASIC_CPU)
    public void cpu(@RequestBody Cpu cpu){
        basicOpentsdb.save(cpu);
    }


    @RequestMapping(UMCDevOpsConstants.URI_BASIC_DISK)
    public void disk(@RequestBody Disk disk){
        System.out.println(disk);
    }

    @RequestMapping(UMCDevOpsConstants.URI_BASIC_NET)
    public void net(@RequestBody Net net){
        System.out.println(net);
    }


}
