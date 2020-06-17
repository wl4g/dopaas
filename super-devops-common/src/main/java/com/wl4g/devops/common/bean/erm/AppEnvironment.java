package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class AppEnvironment extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private Integer clusterId;

    private Integer repositoryId;

    private String envType;

    private String organizationCode;

    private String runCommand;

    private String configContent;

    private String customRepositoryConfig;

    private String repositoryNamespace;

    //expand

    private DockerRepository dockerRepository;

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getEnvType() {
        return envType;
    }

    public void setEnvType(String envType) {
        this.envType = envType == null ? null : envType.trim();
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode == null ? null : organizationCode.trim();
    }

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand == null ? null : runCommand.trim();
    }

    public String getConfigContent() {
        return configContent;
    }

    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getCustomRepositoryConfig() {
        return customRepositoryConfig;
    }

    public void setCustomRepositoryConfig(String customRepositoryConfig) {
        this.customRepositoryConfig = customRepositoryConfig;
    }

    public DockerRepository getDockerRepository() {
        return dockerRepository;
    }

    public void setDockerRepository(DockerRepository dockerRepository) {
        this.dockerRepository = dockerRepository;
    }

    public String getRepositoryNamespace() {
        return repositoryNamespace;
    }

    public void setRepositoryNamespace(String repositoryNamespace) {
        this.repositoryNamespace = repositoryNamespace;
    }
}