package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class Department extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private String name;

    private Integer dutyUserId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getDutyUserId() {
        return dutyUserId;
    }

    public void setDutyUserId(Integer dutyUserId) {
        this.dutyUserId = dutyUserId;
    }

}