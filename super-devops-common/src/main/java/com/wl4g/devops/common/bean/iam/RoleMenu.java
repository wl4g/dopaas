package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class RoleMenu extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private Integer roleId;

    private Integer menuId;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

}