package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class PipelineHistoryInstance extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer pipeHistoryId;

    private Integer instanceId;

    private Integer status;

    private Date createDate;

    private Long costTime;

    public Integer getPipeHistoryId() {
        return pipeHistoryId;
    }

    public void setPipeHistoryId(Integer pipeHistoryId) {
        this.pipeHistoryId = pipeHistoryId;
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }
}