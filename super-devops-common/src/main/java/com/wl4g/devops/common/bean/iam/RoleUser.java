package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class RoleUser extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private Integer userId;

    private Integer roleId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}