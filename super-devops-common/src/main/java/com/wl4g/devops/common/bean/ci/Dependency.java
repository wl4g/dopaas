package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.io.Serializable;

public class Dependency extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private Integer id;

    private Integer projectId;

    private Integer parentId;

    private String parentBranch;

    public Dependency() {

    }

    public Dependency(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentBranch() {
        return parentBranch;
    }

    public void setParentBranch(String parentBranch) {
        this.parentBranch = parentBranch;
    }
}