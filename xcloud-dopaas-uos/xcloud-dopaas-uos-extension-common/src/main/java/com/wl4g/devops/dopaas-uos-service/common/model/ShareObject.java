package com.wl4g.devops.uos.common.model;

/**
 * @author vjay
 * @date 2020-08-13 17:50:00
 */
public class ShareObject {

    private Integer expireSec;

    private String url;

    public Integer getExpireSec() {
        return expireSec;
    }

    public void setExpireSec(Integer expireSec) {
        this.expireSec = expireSec;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
