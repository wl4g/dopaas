package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class DnsOperationLog extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private Integer zoneType;

    private String domain;

    private Date operDate;

    private String operBy;

    private String description;

    private String result;


    public Integer getZoneType() {
        return zoneType;
    }

    public void setZoneType(Integer zoneType) {
        this.zoneType = zoneType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain == null ? null : domain.trim();
    }

    public Date getOperDate() {
        return operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    public String getOperBy() {
        return operBy;
    }

    public void setOperBy(String operBy) {
        this.operBy = operBy == null ? null : operBy.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }
}