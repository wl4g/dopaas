package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class Trigger extends BaseBean implements Serializable {

    private String name;

    private Integer clusterId;

    private Integer taskId;

    private Integer type;

    private String cron;

    private String sha;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron == null ? null : cron.trim();
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha == null ? null : sha.trim();
    }

    @Override
    public String toString() {
        return "Trigger{" +
                "name='" + name + '\'' +
                ", clusterId=" + clusterId +
                ", taskId=" + taskId +
                ", type=" + type +
                ", cron='" + cron + '\'' +
                ", sha='" + sha + '\'' +
                '}';
    }
}