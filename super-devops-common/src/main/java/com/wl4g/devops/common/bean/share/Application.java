package com.wl4g.devops.common.bean.share;

public class Application {
    private String appName;

    private String viewExtranetBaseuri;

    private String extranetBaseuri;

    private String intranetBaseuri;

    private String remark;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getViewExtranetBaseuri() {
        return viewExtranetBaseuri;
    }

    public void setViewExtranetBaseuri(String viewExtranetBaseuri) {
        this.viewExtranetBaseuri = viewExtranetBaseuri == null ? null : viewExtranetBaseuri.trim();
    }

    public String getExtranetBaseuri() {
        return extranetBaseuri;
    }

    public void setExtranetBaseuri(String extranetBaseuri) {
        this.extranetBaseuri = extranetBaseuri == null ? null : extranetBaseuri.trim();
    }

    public String getIntranetBaseuri() {
        return intranetBaseuri;
    }

    public void setIntranetBaseuri(String intranetBaseuri) {
        this.intranetBaseuri = intranetBaseuri == null ? null : intranetBaseuri.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}