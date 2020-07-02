package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class DnsPrivateResolution extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private Integer domainId;

    private String host;

    private String resolveType;

    private String lineIsp;

    private String value;

    private Integer ttl;

    private Integer priority;

    private String status;

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host == null ? null : host.trim();
    }

    public String getResolveType() {
        return resolveType;
    }

    public void setResolveType(String resolveType) {
        this.resolveType = resolveType == null ? null : resolveType.trim();
    }

    public String getLineIsp() {
        return lineIsp;
    }

    public void setLineIsp(String lineIsp) {
        this.lineIsp = lineIsp == null ? null : lineIsp.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

}