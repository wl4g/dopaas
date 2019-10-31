package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

public class Group extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private String name;

    private String displayName;

    private Integer type;

    private Integer parentId;

    private Integer dutyUserId;

    private Integer status;

    //other
    private List<Group> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? null : displayName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getDutyUserId() {
        return dutyUserId;
    }

    public void setDutyUserId(Integer dutyUserId) {
        this.dutyUserId = dutyUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Group> getChildren() {
        return children;
    }

    public void setChildren(List<Group> children) {
        this.children = children;
    }
}