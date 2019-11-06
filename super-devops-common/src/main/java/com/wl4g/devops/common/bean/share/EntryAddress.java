package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class EntryAddress extends BaseBean implements Serializable {

    private static final long serialVersionUID = -7546448616357790576L;

    private Integer clusterId;

    private String name;

    private String displayName;

    private String type;

    private String envType;

    private String viewExtranetBaseUri;

    private String extranetBaseUri;

    private String intranetBaseUri;

    private String remark;

    public EntryAddress() {
    }

    public EntryAddress(String name, String viewExtranetBaseUri) {
        this.name = name;
        this.viewExtranetBaseUri = viewExtranetBaseUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}