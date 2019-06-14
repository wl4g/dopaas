package com.wl4g.devops.umc.store.interfaces;

import com.wl4g.devops.common.bean.umc.temple.basics.Cpu;
import com.wl4g.devops.common.bean.umc.temple.basics.Disk;
import com.wl4g.devops.common.bean.umc.temple.basics.Mem;
import com.wl4g.devops.common.bean.umc.temple.basics.Net;

/**
 * @author vjay
 * @date 2019-06-10 15:00:00
 */
public interface BasicsInterface {

    boolean save(Cpu cpu);

    boolean save(Mem mem);

    boolean save(Disk disk);

    boolean save(Net net);


}
