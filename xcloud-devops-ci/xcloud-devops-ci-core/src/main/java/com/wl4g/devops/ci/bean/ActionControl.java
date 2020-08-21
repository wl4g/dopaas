package com.wl4g.devops.ci.bean;

/**
 * @author vjay
 * @date 2020-08-21 10:24:00
 */
public class ActionControl {

    private boolean test = false;

    private boolean deploy = true;

    private String branch;

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
