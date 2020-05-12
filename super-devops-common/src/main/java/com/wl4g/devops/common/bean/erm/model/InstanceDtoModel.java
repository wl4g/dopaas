package com.wl4g.devops.common.bean.erm.model;

import com.wl4g.devops.common.bean.erm.AppInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2020-05-12 14:35:00
 */
public class InstanceDtoModel {

    private String envType;

    private Integer serverType;

    private List<AppInstance> instances = new ArrayList<>();

    public String getEnvType() {
        return envType;
    }

    public void setEnvType(String envType) {
        this.envType = envType;
    }

    public Integer getServerType() {
        return serverType;
    }

    public void setServerType(Integer serverType) {
        this.serverType = serverType;
    }

    public List<AppInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<AppInstance> instances) {
        this.instances = instances;
    }

    public static List<InstanceDtoModel> instanesToDtoModels(List<AppInstance> instances) {
        List<InstanceDtoModel> instanceDtoModels = new ArrayList<>();
        InstanceDtoModel dev = new InstanceDtoModel();
        dev.setEnvType("dev");
        InstanceDtoModel fat = new InstanceDtoModel();
        fat.setEnvType("fat");
        InstanceDtoModel pro = new InstanceDtoModel();
        pro.setEnvType("pro");
        InstanceDtoModel uat = new InstanceDtoModel();
        uat.setEnvType("uat");
        for (AppInstance appInstance : instances) {
            if (StringUtils.equalsIgnoreCase(appInstance.getEnvType(), "dev")) {
                dev.setServerType(appInstance.getServerType());
                dev.getInstances().add(appInstance);
            } else if (StringUtils.equalsIgnoreCase(appInstance.getEnvType(), "fat")) {
                fat.setServerType(appInstance.getServerType());
                fat.getInstances().add(appInstance);
            } else if (StringUtils.equalsIgnoreCase(appInstance.getEnvType(), "pro")) {
                pro.setServerType(appInstance.getServerType());
                pro.getInstances().add(appInstance);
            } else if (StringUtils.equalsIgnoreCase(appInstance.getEnvType(), "uat")) {
                uat.setServerType(appInstance.getServerType());
                uat.getInstances().add(appInstance);
            }
        }
        instanceDtoModels.add(dev);
        instanceDtoModels.add(fat);
        instanceDtoModels.add(pro);
        instanceDtoModels.add(uat);
        return instanceDtoModels;
    }

    public static List<AppInstance> dtoModelToInstances(List<InstanceDtoModel> instanceDtoModels) {
        List<AppInstance> instances = new ArrayList<>();
        for (InstanceDtoModel instanceDtoModel : instanceDtoModels) {
            List<AppInstance> instancesInModel = instanceDtoModel.getInstances();
            if(!CollectionUtils.isEmpty(instancesInModel)){
                for(AppInstance instance : instancesInModel){
                    instance.setEnvType(instanceDtoModel.getEnvType());
                    instance.setServerType(instanceDtoModel.getServerType());
                }
                instances.addAll(instancesInModel);
            }
        }
        return instances;
    }
}
