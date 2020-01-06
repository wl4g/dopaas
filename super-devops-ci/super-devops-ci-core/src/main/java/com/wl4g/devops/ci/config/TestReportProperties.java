package com.wl4g.devops.ci.config;

/**
 * @author vjay
 * @date 2020-01-06 17:25:00
 */
public class TestReportProperties {

    private String uploadPath;

    private String downloadUrl;

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
