package com.wl4g.dopaas.uci.pipeline.provider.model;

import java.util.List;

/**
 * @author vjay
 * @date 2021-04-15 14:47:00
 */
public class MetaModel {

    private String uciMetaVersion;
    private BuildInfo buildInfo = new BuildInfo();
    private SourceInfo sourceInfo = new SourceInfo();
    private PcmInfo pcmInfo = new PcmInfo();
    private DeployInfo deployInfo = new DeployInfo();

    public String getUciMetaVersion() {
        return uciMetaVersion;
    }

    public void setUciMetaVersion(String value) {
        this.uciMetaVersion = value;
    }

    public BuildInfo getBuildInfo() {
        return buildInfo;
    }

    public void setBuildInfo(BuildInfo value) {
        this.buildInfo = value;
    }

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(SourceInfo value) {
        this.sourceInfo = value;
    }

    public PcmInfo getPcmInfo() {
        return pcmInfo;
    }

    public void setPcmInfo(PcmInfo pcmInfo) {
        this.pcmInfo = pcmInfo;
    }

    public DeployInfo getDeployInfo() {
        return deployInfo;
    }

    public void setDeployInfo(DeployInfo value) {
        this.deployInfo = value;
    }

    public static class BuildInfo {
        private String serviceName;
        private String md5;
        private long totalBytes;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String value) {
            this.serviceName = value;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String value) {
            this.md5 = value;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public void setTotalBytes(long value) {
            this.totalBytes = value;
        }
    }

    public static class DeployInfo {
        private List<String> hosts;

        public List<String> getHosts() {
            return hosts;
        }

        public void setHosts(List<String> hosts) {
            this.hosts = hosts;
        }
    }

    public static class PcmInfo {
        private String pcmProjectName;
        private String pcmIssuesId;
        private String pcmIssuesSubject;

        public String getPcmProjectName() {
            return pcmProjectName;
        }

        public void setPcmProjectName(String pcmProjectName) {
            this.pcmProjectName = pcmProjectName;
        }

        public String getPcmIssuesId() {
            return pcmIssuesId;
        }

        public void setPcmIssuesId(String pcmIssuesId) {
            this.pcmIssuesId = pcmIssuesId;
        }

        public String getPcmIssuesSubject() {
            return pcmIssuesSubject;
        }

        public void setPcmIssuesSubject(String pcmIssuesSubject) {
            this.pcmIssuesSubject = pcmIssuesSubject;
        }
    }

    public static class SourceInfo {
        private String projectUrl;
        private String commitId;
        private String branchOrTag;
        private long timestamp;
        private String comment;

        public String getProjectUrl() { return projectUrl; }
        public void setProjectUrl(String value) { this.projectUrl = value; }

        public String getCommitId() { return commitId; }
        public void setCommitId(String value) { this.commitId = value; }

        public String getBranchOrTag() { return branchOrTag; }
        public void setBranchOrTag(String value) { this.branchOrTag = value; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long value) { this.timestamp = value; }

        public String getComment() { return comment; }
        public void setComment(String value) { this.comment = value; }
    }
}


