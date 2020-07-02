package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;
import java.util.List;

public class DnsPrivateDomain extends BaseBean {

    private static final long serialVersionUID = -3298424126317938674L;

    private String zone;

    private Integer dnsServerId;

    private String status;

    private Date registerDate;

    private Date dueDate;

    private List<DnsPrivateResolution> dnsPrivateResolutions;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone == null ? null : zone.trim();
    }

    public Integer getDnsServerId() {
        return dnsServerId;
    }

    public void setDnsServerId(Integer dnsServerId) {
        this.dnsServerId = dnsServerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<DnsPrivateResolution> getDnsPrivateResolutions() {
        return dnsPrivateResolutions;
    }

    public void setDnsPrivateResolutions(List<DnsPrivateResolution> dnsPrivateResolutions) {
        this.dnsPrivateResolutions = dnsPrivateResolutions;
    }
}