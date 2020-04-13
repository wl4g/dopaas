package com.wl4g.devops.common.bean.umc.datasouces;

public class MysqlDataSource extends BaseDataSource {

    private static final long serialVersionUID = 381411777614066880L;

    final public static String URL = "url";
    final public static String USERNAME = "username";
    final public static String PASSWORD = "password";

    private String url;

    private String username;

    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
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