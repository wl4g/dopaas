package com.wl4g.devops.umc.gather;

import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;

/**
 * @author vjay
 * @date 2019-06-10 14:04:00
 */
public abstract class AbstractGatherTemple {


    public abstract PhysicalInfo analysis();

    public abstract void storage(PhysicalInfo baseTemple);

    public void warn(PhysicalInfo baseTemple){

    }


}
