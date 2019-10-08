package com.wl4g.devops.ci.core.provider;

import com.wl4g.devops.ci.anno.DeployType;
import com.wl4g.devops.ci.constant.DeployTypeEnum;
import com.wl4g.devops.ci.core.BaseDeploy;
import com.wl4g.devops.ci.core.DeployInterface;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2019-09-30 10:09:00
 */
@DeployType(DeployTypeEnum.MvnAssembleTar)
@Component
public class MvnAssembleTarDeploy extends BaseDeploy implements DeployInterface {
    @Override
    public void getSource() {

    }

    @Override
    public void build() {

    }

    @Override
    public void preCommand() {

    }

    @Override
    public void transport() {

    }

    @Override
    public void postCommand() {

    }

    @Override
    public void bakcup() {

    }

    @Override
    public void rollback() {

    }
}
