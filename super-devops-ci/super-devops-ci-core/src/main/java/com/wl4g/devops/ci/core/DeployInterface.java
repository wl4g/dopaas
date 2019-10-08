package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.bean.BaseDeployBean;

import java.util.List;

/**
 * @author vjay
 * @date 2019-09-23 17:19:00
 */
public interface DeployInterface {

    //========== Step1 ==========

    //get source from git
    void getSource(BaseDeployBean bean);

    //build
    void build(BaseDeployBean bean) throws Exception;

    //before scp
    void preCommand(BaseDeployBean bean) throws Exception;


    //========== Step2 ==========

    //transport to instance
    List<Thread> deploy(BaseDeployBean bean);


    //========== other ==========

    //backup
    void bakcup(BaseDeployBean bean) throws Exception;

    //rollback
    void rollback(BaseDeployBean bean);



}
