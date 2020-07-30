package com.wl4g.devops.common.bean.gw;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class GWUpstream extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private String name;

    private String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri == null ? null : uri.trim();
    }

}