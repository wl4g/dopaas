package com.wl4g.devops.common.bean.doc;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wl4g.devops.common.bean.BaseBean;

public class Share extends BaseBean {

    private static final long serialVersionUID = -7171357316844793042L;

    private String shareCode;

    private String docCode;

    private Integer shareType;

    private String passwd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    private Integer expireType;

    //other
    private String name;

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode == null ? null : shareCode.trim();
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode == null ? null : docCode.trim();
    }

    public Integer getShareType() {
        return shareType;
    }

    public void setShareType(Integer shareType) {
        this.shareType = shareType;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getExpireType() {
        return expireType;
    }

    public void setExpireType(Integer expireType) {
        this.expireType = expireType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}