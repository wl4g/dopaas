package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class PipelineHistoryStep extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer pipeHistoryId;

    private Integer type;

    private String status;

    private Long costTime;

    public Integer getPipeHistoryId() {
        return pipeHistoryId;
    }

    public void setPipeHistoryId(Integer pipeHistoryId) {
        this.pipeHistoryId = pipeHistoryId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }
}