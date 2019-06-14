package com.wl4g.devops.umc.store.opentsdb;

import com.wl4g.devops.common.bean.umc.temple.basics.Cpu;
import com.wl4g.devops.common.bean.umc.temple.basics.Disk;
import com.wl4g.devops.common.bean.umc.temple.basics.Mem;
import com.wl4g.devops.common.bean.umc.temple.basics.Net;
import com.wl4g.devops.umc.store.interfaces.BasicsInterface;
import com.wl4g.devops.umc.store.opentsdb.client.bean.request.Point;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.bean.umc.temple.basics.Disk.*;
import static com.wl4g.devops.common.bean.umc.temple.basics.Net.NetInfo;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;

/**
 * @author vjay
 * @date 2019-06-14 10:25:00
 */
@Component
public class BasicOpentsdb implements BasicsInterface {

    @Override
    public boolean save(Cpu cpu) {
        long timestamp = System.currentTimeMillis()/1000;//opentsdb用秒做时间戳
        Assert.notNull(cpu,"cpu is null");
        Assert.notEmpty(cpu.getCpu(),"cpu is null");
        Assert.hasText(cpu.getId(),"id can not null");
        Point point = Point.metric(METRIC_CPU).tag(TAG_ID,cpu.getId() ).value(timestamp, cpu.getCpu()[0]).build();
        OpentsdbConf.getClient().put(point);
        return true;
    }

    @Override
    public boolean save(Mem mem) {
        long timestamp = System.currentTimeMillis()/1000;//opentsdb用秒做时间戳
        Assert.notNull(mem,"mem is null");
        Assert.notNull(mem.getMemInfo(),"mem info is null");
        Mem.MemInfo memInfo = mem.getMemInfo();
        Point total = Point.metric(METRIC_MEM_TOTAL).tag(TAG_ID,mem.getId() ).value(timestamp, memInfo.getTotal()).build();
        Point used = Point.metric(METRIC_MEM_USED).tag(TAG_ID,mem.getId() ).value(timestamp, memInfo.getUsed()).build();
        Point free = Point.metric(METRIC_MEM_FREE).tag(TAG_ID,mem.getId() ).value(timestamp, memInfo.getFree()).build();
        Point usedPercent = Point.metric(METRIC_MEM_USED_PERCENT).tag(TAG_ID,mem.getId() ).value(timestamp, memInfo.getUsedPercent()).build();
        Point buffers = Point.metric(METRIC_MEM_BUFFERS).tag(TAG_ID,mem.getId() ).value(timestamp, memInfo.getBuffers()).build();
        Point cache = Point.metric(METRIC_MEM_CACHE).tag(TAG_ID,mem.getId() ).value(timestamp, memInfo.getCached()).build();
        OpentsdbConf.getClient().put(total);
        OpentsdbConf.getClient().put(used);
        OpentsdbConf.getClient().put(free);
        OpentsdbConf.getClient().put(usedPercent);
        OpentsdbConf.getClient().put(buffers);
        OpentsdbConf.getClient().put(cache);
        return true;
    }

    @Override
    public boolean save(Disk disk) {
        long timestamp = System.currentTimeMillis()/1000;//opentsdb用秒做时间戳
        Assert.notNull(disk,"disk is null");
        Assert.notNull(disk.getDiskInfos(),"disks info is null");
        for(DiskInfo diskInfo : disk.getDiskInfos()){
            PartitionStat partitionStat = diskInfo.getPartitionStat();
            Usage usage = diskInfo.getUsage();

            Point total = Point.metric(METRIC_DISK_TOTAL).tag(TAG_ID, disk.getId())
                    //.tag(TAG_DISK_MOUNT_POINT,partitionStat.getMountpoint())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getTotal()).build();
            Point free = Point.metric(METRIC_DISK_FREE).tag(TAG_ID, disk.getId())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getFree()).build();
            Point used = Point.metric(METRIC_DISK_USED).tag(TAG_ID, disk.getId())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getUsed()).build();
            Point usedPercent = Point.metric(METRIC_DISK_USED_PERCENT).tag(TAG_ID, disk.getId())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getUsedPercent()).build();
            Point inodesTotal = Point.metric(METRIC_DISK_INODES_TOTAL).tag(TAG_ID, disk.getId())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getInodesTotal()).build();
            Point inodesFree = Point.metric(METRIC_DISK_INODES_FREE).tag(TAG_ID, disk.getId())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getInodesFree()).build();
            Point inodesUsed = Point.metric(METRIC_DISK_INODES_USED).tag(TAG_ID, disk.getId())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getInodesUsed()).build();
            Point inodesUsedPercent = Point.metric(METRIC_DISK_INODES_USED_PERCENT).tag(TAG_ID, disk.getId())
                    .tag(TAG_DISK_DEVICE, partitionStat.getDevice())
                    .value(timestamp, usage.getInodesUsedPercent()).build();

            OpentsdbConf.getClient().put(total);
            OpentsdbConf.getClient().put(used);
            OpentsdbConf.getClient().put(free);
            OpentsdbConf.getClient().put(usedPercent);
            OpentsdbConf.getClient().put(inodesTotal);
            OpentsdbConf.getClient().put(inodesFree);
            OpentsdbConf.getClient().put(inodesUsed);
            OpentsdbConf.getClient().put(inodesUsedPercent);
        }
        return true;
    }

    @Override
    public boolean save(Net net) {
        long timestamp = System.currentTimeMillis()/1000;//opentsdb用秒做时间戳
        Assert.notNull(net,"net is null");
        Assert.notEmpty(net.getNetInfos(),"net info is null");
        for(NetInfo netInfo : net.getNetInfos()){
            Point up = Point.metric(METRIC_NET_UP).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getUp()).build();
            Point down = Point.metric(METRIC_NET_DOWN).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getDown()).build();
            Point count = Point.metric(METRIC_NET_COUNT).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getCount()).build();
            Point estab = Point.metric(METRIC_NET_ESTAB).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getEstab()).build();
            Point closeWait = Point.metric(METRIC_NET_CLOSE_WAIT).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getCloseWait()).build();
            Point timeWait = Point.metric(METRIC_NET_TIME_WAIT).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getTimeWait()).build();
            Point close = Point.metric(METRIC_NET_CLOSE).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getClose()).build();
            Point listen = Point.metric(METRIC_NET_LISTEN).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getListen()).build();
            Point closing = Point.metric(METRIC_NET_CLOSING).tag(TAG_ID,net.getId())
                    .tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort()))
                    .value(timestamp, netInfo.getClosing()).build();
            OpentsdbConf.getClient().put(up);
            OpentsdbConf.getClient().put(down);
            OpentsdbConf.getClient().put(count);
            OpentsdbConf.getClient().put(estab);
            OpentsdbConf.getClient().put(closeWait);
            OpentsdbConf.getClient().put(timeWait);
            OpentsdbConf.getClient().put(close);
            OpentsdbConf.getClient().put(listen);
            OpentsdbConf.getClient().put(closing);
        }
        return true;
    }
}
