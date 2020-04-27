package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class Pipeline extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private String pipeName;

    private Integer clusterId;

    private String providerKind;

    private String environment;

    private String parentAppHome;

    private String assetsDir;

    public String getPipeName() {
        return pipeName;
    }

    public void setPipeName(String pipeName) {
        this.pipeName = pipeName == null ? null : pipeName.trim();
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getProviderKind() {
        return providerKind;
    }

    public void setProviderKind(String providerKind) {
        this.providerKind = providerKind == null ? null : providerKind.trim();
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment == null ? null : environment.trim();
    }

    public String getParentAppHome() {
        return parentAppHome;
    }

    public void setParentAppHome(String parentAppHome) {
        this.parentAppHome = parentAppHome == null ? null : parentAppHome.trim();
    }

    public String getAssetsDir() {
        return assetsDir;
    }

    public void setAssetsDir(String assetsDir) {
        this.assetsDir = assetsDir == null ? null : assetsDir.trim();
    }

}