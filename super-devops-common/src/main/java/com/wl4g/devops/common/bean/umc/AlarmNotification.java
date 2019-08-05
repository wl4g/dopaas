package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;
import java.util.Date;

public class AlarmNotification extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private Integer contactGroupId;

    private Integer recordId;

    private Date alarmTime;

    private String alarmNote;

    public Integer getContactGroupId() {
        return contactGroupId;
    }

    public void setContactGroupId(Integer contactGroupId) {
        this.contactGroupId = contactGroupId;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmNote() {
        return alarmNote;
    }

    public void setAlarmNote(String alarmNote) {
        this.alarmNote = alarmNote == null ? null : alarmNote.trim();
    }
}