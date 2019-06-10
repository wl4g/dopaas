package com.wl4g.devops.common.bean.umc.temple;

import java.math.BigDecimal;

/**
 * @author vjay
 * @date 2019-06-10 14:37:00
 */
public class BasicsTemple extends BaseTemple{

    private BigDecimal cpu;

    private BigDecimal mem;

    private BigDecimal disk;

    private BigDecimal net;

    public BigDecimal getCpu() {
        return cpu;
    }

    public void setCpu(BigDecimal cpu) {
        this.cpu = cpu;
    }

    public BigDecimal getMem() {
        return mem;
    }

    public void setMem(BigDecimal mem) {
        this.mem = mem;
    }

    public BigDecimal getDisk() {
        return disk;
    }

    public void setDisk(BigDecimal disk) {
        this.disk = disk;
    }

    public BigDecimal getNet() {
        return net;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }
}
