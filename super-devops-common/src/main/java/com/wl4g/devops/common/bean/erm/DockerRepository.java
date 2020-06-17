package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class DockerRepository extends BaseBean {

    private static final long serialVersionUID = -7546448616357790576L;

    private String name;

    private String registryAddress;

    private String authConfig;

    //===expand

    private AuthConfigModel authConfigModel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress == null ? null : registryAddress.trim();
    }

    public String getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(String authConfig) {
        this.authConfig = authConfig == null ? null : authConfig.trim();
    }

    public AuthConfigModel getAuthConfigModel() {
        return authConfigModel;
    }

    public void setAuthConfigModel(AuthConfigModel authConfigModel) {
        this.authConfigModel = authConfigModel;
    }

    public static class AuthConfigModel{

        private String username;

        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}