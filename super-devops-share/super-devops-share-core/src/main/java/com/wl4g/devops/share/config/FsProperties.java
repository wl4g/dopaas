package com.wl4g.devops.share.config;

public class FsProperties {

    private String basePath;

    private String baseUrl;

    public String getBaseFilePath(){
        return getBasePath()+"/file";
    }

    public String getBaseImgPath(){
        return getBasePath()+"/img";
    }

    public String getBaseFileUrl(){
        return getBaseUrl()+"/fs/downloadFile";
    }

    public String getBaseImgUrl(){
        return getBaseUrl()+"/fs/downloadImg";
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
