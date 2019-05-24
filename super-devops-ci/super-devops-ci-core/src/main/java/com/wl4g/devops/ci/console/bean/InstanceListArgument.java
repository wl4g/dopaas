package com.wl4g.devops.ci.console.bean;

import com.wl4g.devops.shell.annotation.ShellOption;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2019-05-21 16:18:00
 */
public class InstanceListArgument implements Serializable {
    private static final long serialVersionUID = -90377698662015272L;

    @ShellOption(opt = "p", lopt = "app", help = "app name", required = false)
    private String appGroupName;

    @ShellOption(opt = "e", lopt = "env", help = "env name", required = false)
    private String envName;

    public String getAppGroupName() {
        return appGroupName;
    }

    public void setAppGroupName(String appGroupName) {
        this.appGroupName = appGroupName;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }
}
