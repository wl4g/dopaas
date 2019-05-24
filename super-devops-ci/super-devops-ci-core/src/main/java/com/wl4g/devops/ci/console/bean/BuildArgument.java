package com.wl4g.devops.ci.console.bean;

import com.wl4g.devops.shell.annotation.ShellOption;

import java.io.Serializable;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-21 15:53:00
 */
public class BuildArgument implements Serializable {
    private static final long serialVersionUID = -90377698662015272L;

    @ShellOption(opt = "p", lopt = "app", help = "app name")
    private String appGroupName;

    @ShellOption(opt = "b", lopt = "branch", help = "branch name")
    private String branchName;

    @ShellOption(opt = "i", lopt = "instances", help = "List<AppInstance> type argument")
    private List<String> instances;


    public String getAppGroupName() {
        return appGroupName;
    }

    public void setAppGroupName(String appGroupName) {
        this.appGroupName = appGroupName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }
}
