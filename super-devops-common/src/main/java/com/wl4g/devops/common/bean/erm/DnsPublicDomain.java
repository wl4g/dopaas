package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class DnsPublicDomain extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private String zone;

    private String dnsKind;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone == null ? null : zone.trim();
    }

    public String getDnsKind() {
        return dnsKind;
    }

    public void setDnsKind(String dnsKind) {
        this.dnsKind = dnsKind == null ? null : dnsKind.trim();
    }
}