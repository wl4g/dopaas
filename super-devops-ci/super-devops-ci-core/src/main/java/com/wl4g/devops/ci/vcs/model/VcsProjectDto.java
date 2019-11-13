package com.wl4g.devops.ci.vcs.model;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2019-11-13 15:21:00
 */
public class VcsProjectDto implements Serializable {

    private static final long serialVersionUID = 3384209918335868080L;

    private Integer id;

    private String name;

    private String httpUrl;

    private String sshUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        this.sshUrl = sshUrl;
    }
}
