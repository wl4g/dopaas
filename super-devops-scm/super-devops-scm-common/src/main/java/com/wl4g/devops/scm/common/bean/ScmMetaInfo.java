package com.wl4g.devops.scm.common.bean;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2019-06-05 17:42:00
 */
public class ScmMetaInfo implements Serializable {

    private static final long serialVersionUID = 381411777614066880L;

    private String secretKey;

    private String signType;

    private String token;

    public ScmMetaInfo(){

    }


    public ScmMetaInfo(String secretKey, String signType, String token) {
        this.secretKey = secretKey;
        this.signType = signType;
        this.token = token;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
