/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.ci.config;

import com.wl4g.devops.support.task.GenericTaskRunner;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * Deployments configuration properties.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public class CiCdProperties {

    private ExecutorProperties executor = new ExecutorProperties();

    private VcsProperties vcs = new VcsProperties();

    private BuildProperties build = new BuildProperties();

    private BackupProperties backup = new BackupProperties();

    private TranformProperties tranform = new TranformProperties();


    public ExecutorProperties getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorProperties executor) {
        this.executor = executor;
    }

    public VcsProperties getVcs() {
        return vcs;
    }

    public void setVcs(VcsProperties vcs) {
        this.vcs = vcs;
    }

    public BuildProperties getBuild() {
        return build;
    }

    public void setBuild(BuildProperties build) {
        this.build = build;
    }

    public BackupProperties getBackup() {
        return backup;
    }

    public void setBackup(BackupProperties backup) {
        this.backup = backup;
    }

    public TranformProperties getTranform() {
        return tranform;
    }

    public void setTranform(TranformProperties tranform) {
        this.tranform = tranform;
    }

    public static class ExecutorProperties extends GenericTaskRunner.RunProperties {

        public ExecutorProperties() {
            setConcurrency(5);
            setAcceptQueue(32);
        }

    }

    public static class VcsProperties {

        private GitProperties git;

        public GitProperties getGit() {
            return git;
        }

        public void setGit(GitProperties git) {
            this.git = git;
        }

        public static class GitProperties {
            private String baseUrl;
            private String username;
            private String password;
            private String token;

            /**
             * Git check out path
             */
            private String workspace;

            /**
             * credentials for git
             */
            private CredentialsProvider credentials;

            public String getBaseUrl() {
                return baseUrl;
            }

            public void setBaseUrl(String baseUrl) {
                this.baseUrl = baseUrl;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public void setWorkspace(String workspace) {
                this.workspace = workspace;
            }

            public String getWorkspace() {
                if (StringUtils.isBlank(workspace)) {// if blank ,user default
                    workspace = System.getProperties().getProperty("user.home") + "/git";
                }
                return workspace;
            }

            public CredentialsProvider getCredentials() {
                if (null == credentials) {
                    credentials = new UsernamePasswordCredentialsProvider(username, password);
                }
                return credentials;
            }
        }
    }

    //build
    public static class BuildProperties {

        private Integer jobCleanScan = 30;

        private Integer jobCleanTimeout = 600;

        private Integer jobShareDenpenyTryTimeout = 300;

        public Integer getJobCleanScan() {
            return jobCleanScan;
        }

        public void setJobCleanScan(Integer jobCleanScan) {
            this.jobCleanScan = jobCleanScan;
        }

        public Integer getJobCleanTimeout() {
            return jobCleanTimeout;
        }

        public void setJobCleanTimeout(Integer jobCleanTimeout) {
            this.jobCleanTimeout = jobCleanTimeout;
        }

        public Integer getJobShareDenpenyTryTimeout() {
            return jobShareDenpenyTryTimeout;
        }

        public void setJobShareDenpenyTryTimeout(Integer jobShareDenpenyTryTimeout) {
            this.jobShareDenpenyTryTimeout = jobShareDenpenyTryTimeout;
        }
    }

    //backup
    public static class BackupProperties {

        private String baseDir;

        public String getBaseDir() {
            if (StringUtils.isBlank(baseDir)) {// if blank ,user default
                baseDir = System.getProperties().getProperty("user.home") + "/git/bak";
            }
            return baseDir;
        }

        public void setBaseDir(String baseDir) {
            this.baseDir = baseDir;
        }
    }

    //tranform
    public static class TranformProperties {

        private String cipherKey;
        private MvnAssTar mvnAssTar;
        private DockerNative dockerNative;

        public String getCipherKey() {
            return cipherKey;
        }

        public void setCipherKey(String cipherKey) {
            this.cipherKey = cipherKey;
        }

        public MvnAssTar getMvnAssTar() {
            return mvnAssTar;
        }

        public void setMvnAssTar(MvnAssTar mvnAssTar) {
            this.mvnAssTar = mvnAssTar;
        }

        public DockerNative getDockerNative() {
            return dockerNative;
        }

        public void setDockerNative(DockerNative dockerNative) {
            this.dockerNative = dockerNative;
        }

        public static class MvnAssTar {

        }

        public static class DockerNative {
            public String dockerPushUsername;
            public String dockerPushPasswd;

            public String getDockerPushUsername() {
                return dockerPushUsername;
            }

            public void setDockerPushUsername(String dockerPushUsername) {
                this.dockerPushUsername = dockerPushUsername;
            }

            public String getDockerPushPasswd() {
                return dockerPushPasswd;
            }

            public void setDockerPushPasswd(String dockerPushPasswd) {
                this.dockerPushPasswd = dockerPushPasswd;
            }
        }
    }

}