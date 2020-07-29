package com.wl4g.devops.common.bean.gw;

import com.wl4g.devops.common.bean.BaseBean;

public class GWUpstreamGroupRef extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private Integer upstreamId;

    private Integer upstreamGroupId;

    private Integer weight;

    public Integer getUpstreamId() {
        return upstreamId;
    }

    public void setUpstreamId(Integer upstreamId) {
        this.upstreamId = upstreamId;
    }

    public Integer getUpstreamGroupId() {
        return upstreamGroupId;
    }

    public void setUpstreamGroupId(Integer upstreamGroupId) {
        this.upstreamGroupId = upstreamGroupId;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}