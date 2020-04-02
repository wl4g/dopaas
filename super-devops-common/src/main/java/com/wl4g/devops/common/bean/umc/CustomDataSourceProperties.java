package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

public class CustomDataSourceProperties extends BaseBean {

    private static final long serialVersionUID = 381411777614066880L;

    private Integer dataSourceId;

    private String key;

    private String value;

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }
}