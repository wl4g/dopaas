package com.wl4g.devops.ci.core;

/**
 * @author vjay
 * @date 2019-09-23 17:19:00
 */
public interface DeployInterface {

    //========== Step1 ==========

    //get source from git
    void getSource();

    //build
    void build();

    //before scp
    void preCommand();


    //========== Step2 ==========

    //transport to instance
    void transport();

    //after transport , customize restart server command
    void postCommand();


    //========== other ==========

    //backup
    void bakcup();

    //rollback
    void rollback();

}
