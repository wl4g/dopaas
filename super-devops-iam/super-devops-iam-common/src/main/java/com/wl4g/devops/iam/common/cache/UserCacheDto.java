package com.wl4g.devops.iam.common.cache;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2019-11-04 17:59:00
 */
public class UserCacheDto implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private int userId;

    private String loginName;

    private String roles;

    private String permissions;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
