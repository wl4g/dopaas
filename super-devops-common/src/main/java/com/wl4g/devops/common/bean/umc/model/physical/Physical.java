package com.wl4g.devops.common.bean.umc.model.physical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wl4g.devops.common.bean.umc.model.Base;

import static com.wl4g.devops.common.bean.umc.model.physical.Disk.DiskInfo;
import static com.wl4g.devops.common.bean.umc.model.physical.Mem.MemInfo;
import static com.wl4g.devops.common.bean.umc.model.physical.Net.NetInfo;

/**
 * @author vjay
 * @date 2019-06-19 14:07:00
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Physical extends Base {

    private static final long serialVersionUID = 457088159628513585L;

    private Double[] cpu;

    private DiskInfo[] diskInfos;


    private MemInfo memInfo;

    private NetInfo[] netInfos;

    public Physical(){

    }

    public Physical(Double[] cpu, DiskInfo[] diskInfos,  MemInfo memInfo, NetInfo[] netInfos) {
        this.cpu = cpu;
        this.diskInfos = diskInfos;
        this.memInfo = memInfo;
        this.netInfos = netInfos;
    }

    public Double[] getCpu() {
        return cpu;
    }

    public void setCpu(Double[] cpu) {
        this.cpu = cpu;
    }

    public DiskInfo[] getDiskInfos() {
        return diskInfos;
    }

    public void setDiskInfos(DiskInfo[] diskInfos) {
        this.diskInfos = diskInfos;
    }

    public MemInfo getMemInfo() {
        return memInfo;
    }

    public void setMemInfo(MemInfo memInfo) {
        this.memInfo = memInfo;
    }

    public NetInfo[] getNetInfos() {
        return netInfos;
    }

    public void setNetInfos(NetInfo[] netInfos) {
        this.netInfos = netInfos;
    }
}


