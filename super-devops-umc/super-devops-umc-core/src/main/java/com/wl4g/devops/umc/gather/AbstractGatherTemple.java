package com.wl4g.devops.umc.gather;

import com.wl4g.devops.common.bean.umc.temple.BaseTemple;

/**
 * @author vjay
 * @date 2019-06-10 14:04:00
 */
public abstract class AbstractGatherTemple {


    public abstract BaseTemple analysis();

    public abstract void storage(BaseTemple baseTemple);

    public void warn(BaseTemple baseTemple){

    }


}
