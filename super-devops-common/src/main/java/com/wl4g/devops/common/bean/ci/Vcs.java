package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class Vcs extends BaseBean implements Serializable {

    private static final long serialVersionUID = 381411777614066880L;

    private String name;

    private Integer provider;

    private Integer authType;

    private String baseUri;

    private String sshKeyPub;

    private String sshKey;

    private String token;

    private String username;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProvider() {
        return provider;
    }

    public void setProvider(Integer provider) {
        this.provider = provider;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri == null ? null : baseUri.trim();
    }

    public String getSshKeyPub() {
        return sshKeyPub;
    }

    public void setSshKeyPub(String sshKeyPub) {
        this.sshKeyPub = sshKeyPub == null ? null : sshKeyPub.trim();
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey == null ? null : sshKey.trim();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

}