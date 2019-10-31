package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;
import java.util.Date;

public class GroupMenu extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private Integer groupId;

    private Integer menuId;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

}