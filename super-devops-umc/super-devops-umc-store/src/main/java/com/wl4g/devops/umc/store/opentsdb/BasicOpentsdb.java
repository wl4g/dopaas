package com.wl4g.devops.umc.store.opentsdb;

import com.wl4g.devops.common.bean.umc.temple.basics.Cpu;
import com.wl4g.devops.common.bean.umc.temple.basics.Disk;
import com.wl4g.devops.common.bean.umc.temple.basics.Mem;
import com.wl4g.devops.common.bean.umc.temple.basics.Net;
import com.wl4g.devops.umc.store.interfaces.BasicsInterface;
import com.wl4g.devops.umc.store.opentsdb.client.bean.request.Point;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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
        Point point = Point.metric("basic.cpu")
                .tag("id",cpu.getId() )
                .value(timestamp, cpu.getCpu()[0])
                .build();
        OpentsdbConf.getClient().put(point);
        return true;
    }

    @Override
    public boolean save(Mem mem) {
        return false;
    }

    @Override
    public boolean save(Disk disk) {
        return false;
    }

    @Override
    public boolean save(Net net) {
        return false;
    }
}
