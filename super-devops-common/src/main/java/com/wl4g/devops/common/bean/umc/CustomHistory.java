package com.wl4g.devops.common.bean.umc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class CustomHistory extends BaseBean {
    private static final long serialVersionUID = 381411777614066880L;


    private Integer customEngineId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private Integer state;

    private String engineName;

    private Long costTime;

    public Integer getCustomEngineId() {
        return customEngineId;
    }

    public void setCustomEngineId(Integer customEngineId) {
        this.customEngineId = customEngineId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }
}