package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.anno.DeployType;
import com.wl4g.devops.ci.constant.DeployTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vjay
 * @date 2019-09-23 10:29:00
 */
@Component
public class DeployAdapter {

    private final Map<DeployTypeEnum, DeployInterface> deployInterfaceMap;

    /**
     * 部署
     */
    public void deploy(DeployTypeEnum deployTypeEnum) throws Exception {
        DeployInterface deployInterface = getDeploy(deployTypeEnum);
        deployInterface.getSource();
        deployInterface.build();
        deployInterface.bakcup();
        deployInterface.preCommand();
        deployInterface.transport();
        deployInterface.postCommand();
    }

    /**
     * 回滚
     */
    public void rollback(DeployTypeEnum deployTypeEnum) throws Exception {
        DeployInterface deployInterface = getDeploy(deployTypeEnum);
        deployInterface.rollback();
        deployInterface.build();
        deployInterface.preCommand();
        deployInterface.transport();
        deployInterface.postCommand();
    }

    public DeployInterface getDeploy(DeployTypeEnum deployTypeEnum){
        return deployInterfaceMap.get(deployTypeEnum);
    }

    @Autowired
    public DeployAdapter(List<DeployInterface> deploys) {
        this.deployInterfaceMap = Collections.unmodifiableMap(buildDeployStrategies(deploys));
    }

    private Map<DeployTypeEnum, DeployInterface> buildDeployStrategies(List<DeployInterface> deploys) {
        return deploys.stream()
                .filter(deploy -> deploy.getClass().isAnnotationPresent(DeployType.class))
                .collect(Collectors.toMap(o -> o.getClass().getAnnotation(DeployType.class).value(), o -> o));
    }


}
