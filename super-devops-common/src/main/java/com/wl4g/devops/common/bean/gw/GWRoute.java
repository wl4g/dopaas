package com.wl4g.devops.common.bean.gw;

import com.wl4g.devops.common.bean.BaseBean;

public class GWRoute extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private Integer gatewayId;

    private Integer upstreamGroupId;

    private String name;

    private Integer order;

    private String remark;

    public Integer getUpstreamGroupId() {
        return upstreamGroupId;
    }

    public void setUpstreamGroupId(Integer upstreamGroupId) {
        this.upstreamGroupId = upstreamGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }
}