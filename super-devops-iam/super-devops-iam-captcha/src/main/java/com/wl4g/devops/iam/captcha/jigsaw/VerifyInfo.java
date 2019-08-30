package com.wl4g.devops.iam.captcha.jigsaw;

/**
 * @author vjay
 * @date 2019-08-30 10:50:00
 */
public class VerifyInfo{
    private String uuid;
    private Integer x;

    /*unused yet*/
    private Integer[] trail;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer[] getTrail() {
        return trail;
    }

    public void setTrail(Integer[] trail) {
        this.trail = trail;
    }
}
