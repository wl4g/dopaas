package com.wl4g.devops.umc.gather;

import com.wl4g.devops.common.bean.umc.model.Base;

/**
 * @author vjay
 * @date 2019-06-10 14:04:00
 */
public abstract class AbstractGatherTemple {


    public abstract Base analysis();

    public abstract void storage(Base baseTemple);

    public void warn(Base baseTemple){

    }


}
