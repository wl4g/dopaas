package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;
import java.util.Date;

public class GroupUser extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private Integer groupId;

    private Integer userId;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}