package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class AlarmCollector extends BaseBean implements Serializable {

    private static final long serialVersionUID = 381411777614066880L;

    private String name;

    private String addr;

    private Integer hostId;

    private Integer status;

    private String hname;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    @Override
    public String toString() {
        return "AlarmCollector{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", hostId=" + hostId +
                ", status=" + status +
                '}';
    }
}