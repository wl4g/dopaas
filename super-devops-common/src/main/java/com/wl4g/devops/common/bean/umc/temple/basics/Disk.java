package com.wl4g.devops.common.bean.umc.temple.basics;

import com.wl4g.devops.common.bean.umc.temple.BaseTemple;

/**
 * @author vjay
 * @date 2019-06-11 17:25:00
 */
public class Disk extends BaseTemple {


    private DiskInfo[] diskInfos;

    public DiskInfo[] getDiskInfos() {
        return diskInfos;
    }

    public void setDiskInfos(DiskInfo[] diskInfos) {
        this.diskInfos = diskInfos;
    }

    public static class DiskInfo {

        private PartitionStat partitionStat;

        private Usage usage;

        public PartitionStat getPartitionStat() {
            return partitionStat;
        }

        public void setPartitionStat(PartitionStat partitionStat) {
            this.partitionStat = partitionStat;
        }

        public Usage getUsage() {
            return usage;
        }

        public void setUsage(Usage usage) {
            this.usage = usage;
        }

    }


    public static class PartitionStat {

        private String device;

        private String mountpoint;

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getMountpoint() {
            return mountpoint;
        }

        public void setMountpoint(String mountpoint) {
            this.mountpoint = mountpoint;
        }
    }

    public static class Usage {

        private String path;

        private Long total;

        private Long free;

        private Long used;

        private Double usedPercent;

        private Long inodesTotal;

        private Long inodesUsed;

        private Long inodesFree;

        private Double inodesUsedPercent;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

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

        public Long getUsed() {
            return used;
        }

        public void setUsed(Long used) {
            this.used = used;
        }

        public Double getUsedPercent() {
            return usedPercent;
        }

        public void setUsedPercent(Double usedPercent) {
            this.usedPercent = usedPercent;
        }

        public Long getInodesTotal() {
            return inodesTotal;
        }

        public void setInodesTotal(Long inodesTotal) {
            this.inodesTotal = inodesTotal;
        }

        public Long getInodesUsed() {
            return inodesUsed;
        }

        public void setInodesUsed(Long inodesUsed) {
            this.inodesUsed = inodesUsed;
        }

        public Long getInodesFree() {
            return inodesFree;
        }

        public void setInodesFree(Long inodesFree) {
            this.inodesFree = inodesFree;
        }

        public Double getInodesUsedPercent() {
            return inodesUsedPercent;
        }

        public void setInodesUsedPercent(Double inodesUsedPercent) {
            this.inodesUsedPercent = inodesUsedPercent;
        }
    }
}
