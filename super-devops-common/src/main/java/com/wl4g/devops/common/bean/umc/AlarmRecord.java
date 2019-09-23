package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AlarmRecord extends BaseBean implements Serializable {
    private static final long serialVersionUID = 381411777614066880L;

    private String name;

    private Integer templateId;

    private Date gatherTime;

    private Date createTime;

    private String alarmType;

    private List<AlarmRule> alarmRules;

    private AlarmTemplate alarmTemplate;

    private String alarmNote;

    private List<AlarmNotificationContact> notificationContacts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Date getGatherTime() {
        return gatherTime;
    }

    public void setGatherTime(Date gatherTime) {
        this.gatherTime = gatherTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType == null ? null : alarmType.trim();
    }

    public List<AlarmRule> getAlarmRules() {
        return alarmRules;
    }

    public void setAlarmRules(List<AlarmRule> alarmRules) {
        this.alarmRules = alarmRules;
    }

    public AlarmTemplate getAlarmTemplate() {
        return alarmTemplate;
    }

    public void setAlarmTemplate(AlarmTemplate alarmTemplate) {
        this.alarmTemplate = alarmTemplate;
    }

    public String getAlarmNote() {
        return alarmNote;
    }

    public void setAlarmNote(String alarmNote) {
        this.alarmNote = alarmNote;
    }

    public List<AlarmNotificationContact> getNotificationContacts() {
        return notificationContacts;
    }

    public void setNotificationContacts(List<AlarmNotificationContact> notificationContacts) {
        this.notificationContacts = notificationContacts;
    }
}