package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

public class CustomAlarmEvent extends BaseBean {
    private static final long serialVersionUID = 381411777614066880L;

    private Integer customEngineId;

    private String notifyGroupIds;

    private String message;

    private String engineName;

    public Integer getCustomEngineId() {
        return customEngineId;
    }

    public void setCustomEngineId(Integer customEngineId) {
        this.customEngineId = customEngineId;
    }

    public String getNotifyGroupIds() {
        return notifyGroupIds;
    }

    public void setNotifyGroupIds(String notifyGroupIds) {
        this.notifyGroupIds = notifyGroupIds == null ? null : notifyGroupIds.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }
}