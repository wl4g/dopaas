package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class K8sInstance extends BaseBean {

    private static final long serialVersionUID = -7546448616357790576L;

    private Integer k8sId;

    private Integer hostId;

    public Integer getK8sId() {
        return k8sId;
    }

    public void setK8sId(Integer k8sId) {
        this.k8sId = k8sId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }
}