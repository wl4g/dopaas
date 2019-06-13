package com.wl4g.devops.common.bean.umc.temple.basics;

/**
 * @author vjay
 * @date 2019-06-11 17:23:00
 */
public class Mem {

    private Long total;
    private Long free;
    private Double usedPercent;
    private Long used;
    private Long cached;
    private Long buffers;


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Double getUsedPercent() {
        return usedPercent;
    }

    public void setUsedPercent(Double usedPercent) {
        this.usedPercent = usedPercent;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getCached() {
        return cached;
    }

    public void setCached(Long cached) {
        this.cached = cached;
    }

    public Long getBuffers() {
        return buffers;
    }

    public void setBuffers(Long buffers) {
        this.buffers = buffers;
    }
}
