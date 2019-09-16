package com.wl4g.devops.common.bean.share;

public class Application {
    private String appName;

    private String viewExtranetBaseUri;

    private String extranetBaseUri;

    private String intranetBaseUri;

    private String remark;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getViewExtranetBaseUri() {
        return viewExtranetBaseUri;
    }

    public void setViewExtranetBaseUri(String viewExtranetBaseUri) {
        this.viewExtranetBaseUri = viewExtranetBaseUri;
    }

    public String getExtranetBaseUri() {
        return extranetBaseUri;
    }

    public void setExtranetBaseUri(String extranetBaseUri) {
        this.extranetBaseUri = extranetBaseUri;
    }

    public String getIntranetBaseUri() {
        return intranetBaseUri;
    }

    public void setIntranetBaseUri(String intranetBaseUri) {
        this.intranetBaseUri = intranetBaseUri;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}