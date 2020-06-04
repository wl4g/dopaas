package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Objects;

public class PipeStepNotification extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer pipeId;

    private Integer enable;

    private Integer[] contactGroupId;

    private String contactGroupIds;

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

    public Integer[] getContactGroupId() {
        return contactGroupId;
    }

    public void setContactGroupId(Integer[] contactGroupId) {
        this.contactGroupId = contactGroupId;
    }

    public void setContactGroupId2(String[] contactGroupId) {
        if(Objects.nonNull(contactGroupId)){
            Integer[] contactGroupIds = new Integer[contactGroupId.length];
            for(int i = 0; i<contactGroupId.length;i++){
                contactGroupIds[i] = Integer.parseInt(contactGroupId[i]);
            }
            this.contactGroupId = contactGroupIds;
        }
    }

    public String getContactGroupIds() {
        return contactGroupIds;
    }

    public void setContactGroupIds(String contactGroupIds) {
        this.contactGroupIds = contactGroupIds;
    }
}