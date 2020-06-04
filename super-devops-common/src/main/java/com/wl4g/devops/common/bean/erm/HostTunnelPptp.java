package com.wl4g.devops.common.bean.erm;

public class HostTunnelPptp extends HostTunnel {

    private static final long serialVersionUID = 4324569366421220002L;

    private String addr;

    private String username;

    private String password;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
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