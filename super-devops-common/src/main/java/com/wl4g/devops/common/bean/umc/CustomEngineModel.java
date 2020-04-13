package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

public class CustomEngineModel extends BaseBean {
    private static final long serialVersionUID = 381411777614066880L;


    private String name;

    private Integer datasourceId;

    private Integer status;

    private Integer[] notifyGroupIds;

    private String notifyTemplate;

    private String cron;

    private String codeContent;

    private String arguments;

    //other
    private String dataSourceName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer[] getNotifyGroupIds() {
        return notifyGroupIds;
    }

    public void setNotifyGroupIds(Integer[] notifyGroupIds) {
        this.notifyGroupIds = notifyGroupIds;
    }

    public String getNotifyTemplate() {
        return notifyTemplate;
    }

    public void setNotifyTemplate(String notifyTemplate) {
        this.notifyTemplate = notifyTemplate == null ? null : notifyTemplate.trim();
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron == null ? null : cron.trim();
    }

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent == null ? null : codeContent.trim();
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments == null ? null : arguments.trim();
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
}