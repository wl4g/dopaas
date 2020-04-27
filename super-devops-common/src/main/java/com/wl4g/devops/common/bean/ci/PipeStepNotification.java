package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class PipeStepNotification extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer pipeId;

    private Integer enable;

    private Integer contactGroupId;

    public Integer getPipeId() {
        return pipeId;
    }

    public void setPipeId(Integer pipeId) {
        this.pipeId = pipeId;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getContactGroupId() {
        return contactGroupId;
    }

    public void setContactGroupId(Integer contactGroupId) {
        this.contactGroupId = contactGroupId;
    }
}