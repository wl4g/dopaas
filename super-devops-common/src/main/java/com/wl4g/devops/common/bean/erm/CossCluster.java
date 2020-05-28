package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class CossCluster extends BaseBean {
    private static final long serialVersionUID = 4324569366421220002L;

    private String name;

    private String masterAddr;

    private String accessToken;

    private String organizationCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr == null ? null : masterAddr.trim();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken == null ? null : accessToken.trim();
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode == null ? null : organizationCode.trim();
    }
}