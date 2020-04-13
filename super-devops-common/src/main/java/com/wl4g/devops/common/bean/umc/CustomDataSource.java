package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.List;

public class CustomDataSource extends BaseBean {
    private static final long serialVersionUID = 381411777614066880L;

    private String name;

    private String provider;

    private Integer status;

    private List<CustomDataSourceProperties> customDataSourceProperties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider == null ? null : provider.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<CustomDataSourceProperties> getCustomDataSourceProperties() {
        return customDataSourceProperties;
    }

    public void setCustomDataSourceProperties(List<CustomDataSourceProperties> customDataSourceProperties) {
        this.customDataSourceProperties = customDataSourceProperties;
    }
}