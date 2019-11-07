package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class ClusterConfig extends BaseBean implements Serializable {

    private static final long serialVersionUID = -7546448616357790576L;

    private Integer clusterId;

    private String name;

    private String displayName;

    private Integer type;

    private String envType;

    private String viewExtranetBaseUri;

    private String extranetBaseUri;

    private String intranetBaseUri;

    public ClusterConfig() {
    }

    public ClusterConfig(String name, String viewExtranetBaseUri) {
        this.name = name;
        this.viewExtranetBaseUri = viewExtranetBaseUri;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? null : displayName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEnvType() {
        return envType;
    }

    public void setEnvType(String envType) {
        this.envType = envType == null ? null : envType.trim();
    }

    public String getViewExtranetBaseUri() {
        return viewExtranetBaseUri;
    }

    public void setViewExtranetBaseUri(String viewExtranetBaseUri) {
        this.viewExtranetBaseUri = viewExtranetBaseUri == null ? null : viewExtranetBaseUri.trim();
    }

    public String getExtranetBaseUri() {
        return extranetBaseUri;
    }

    public void setExtranetBaseUri(String extranetBaseUri) {
        this.extranetBaseUri = extranetBaseUri == null ? null : extranetBaseUri.trim();
    }

    public String getIntranetBaseUri() {
        return intranetBaseUri;
    }

    public void setIntranetBaseUri(String intranetBaseUri) {
        this.intranetBaseUri = intranetBaseUri == null ? null : intranetBaseUri.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}