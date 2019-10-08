package com.wl4g.devops.ci.core.provider;

import com.wl4g.devops.ci.anno.DeployType;
import com.wl4g.devops.ci.bean.BaseDeployBean;
import com.wl4g.devops.ci.constant.DeployTypeEnum;
import com.wl4g.devops.ci.core.BaseDeploy;
import com.wl4g.devops.ci.core.DeployInterface;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author vjay
 * @date 2019-09-30 10:09:00
 */
@DeployType(DeployTypeEnum.SpringbootExecutable)
@Component
public class SpringbootExecutableJarDeploy extends BaseDeploy implements DeployInterface {
    @Override
    public void getSource(BaseDeployBean bean) {

    }

    @Override
    public void build(BaseDeployBean bean) {

    }

    @Override
    public void preCommand(BaseDeployBean bean) {

    }

    @Override
    public List<Thread> deploy(BaseDeployBean bean) {
        return null;
    }

    @Override
    public void bakcup(BaseDeployBean bean) {

    }

    @Override
    public void rollback(BaseDeployBean bean) {

    }
}
