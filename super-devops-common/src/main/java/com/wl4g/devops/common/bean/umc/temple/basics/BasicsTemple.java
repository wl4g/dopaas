package com.wl4g.devops.common.bean.umc.temple.basics;

import com.wl4g.devops.common.bean.umc.temple.BaseTemple;

/**
 * @author vjay
 * @date 2019-06-10 14:37:00
 */
public class BasicsTemple extends BaseTemple {

    private double[] cpu;

    private Mem mem;

    private Disk[] disk;

    private Net net;

    public double[] getCpu() {
        return cpu;
    }

    public void setCpu(double[] cpu) {
        this.cpu = cpu;
    }

    public Mem getMem() {
        return mem;
    }

    public void setMem(Mem mem) {
        this.mem = mem;
    }

    public Disk[] getDisk() {
        return disk;
    }

    public void setDisk(Disk[] disk) {
        this.disk = disk;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }
}
