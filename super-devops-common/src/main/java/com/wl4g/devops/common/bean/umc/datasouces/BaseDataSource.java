package com.wl4g.devops.common.bean.umc.datasouces;

import com.wl4g.devops.common.bean.BaseBean;

/**
 * @author vjay
 * @date 2020-04-02 14:58:00
 */
public class BaseDataSource extends BaseBean {

    private static final long serialVersionUID = 381411777614066880L;

    private String name;

    private String provider;

    private Integer status;

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
}
