package com.wl4g.devops.common.bean.doc;

import java.util.Date;

import com.wl4g.devops.common.bean.BaseBean;

public class Label extends BaseBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

}