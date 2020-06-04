package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class DockerInstance extends BaseBean {

    private static final long serialVersionUID = -7546448616357790576L;

    private Integer dockerId;

    private Integer hostId;

    public Integer getDockerId() {
        return dockerId;
    }

    public void setDockerId(Integer dockerId) {
        this.dockerId = dockerId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }
}