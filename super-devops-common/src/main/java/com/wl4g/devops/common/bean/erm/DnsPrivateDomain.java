package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class DnsPrivateDomain extends BaseBean {

    private static final long serialVersionUID = -3298424126317938674L;

    private String zone;

    private Integer dnsServerId;

    private String status;

    private Date registerDate;

    private Date dueDate;

    private String remark;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    private Integer delFlag;

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

}