package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

public class ContactChannel extends BaseBean {

    private static final long serialVersionUID = -7546448616357790576L;

    private Integer contactId;

    private String kind;

    private String primaryAddress;

    private Integer timeOfFreq;

    private Integer numOfFreq;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(String primaryAddress) {
        this.primaryAddress = primaryAddress == null ? null : primaryAddress.trim();
    }

    public Integer getTimeOfFreq() {
        return timeOfFreq;
    }

    public void setTimeOfFreq(Integer timeOfFreq) {
        this.timeOfFreq = timeOfFreq;
    }

    public Integer getNumOfFreq() {
        return numOfFreq;
    }

    public void setNumOfFreq(Integer numOfFreq) {
        this.numOfFreq = numOfFreq;
    }

}