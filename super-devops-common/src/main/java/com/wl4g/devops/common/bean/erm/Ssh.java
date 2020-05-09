package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class Ssh extends BaseBean {

    private static final long serialVersionUID = -7546448616357790576L;

    private String name;

    private String username;

    private String password;

    private String sshKey;

    private String sshKeyPub;

    private String authType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey == null ? null : sshKey.trim();
    }

    public String getSshKeyPub() {
        return sshKeyPub;
    }

    public void setSshKeyPub(String sshKeyPub) {
        this.sshKeyPub = sshKeyPub == null ? null : sshKeyPub.trim();
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType == null ? null : authType.trim();
    }

}