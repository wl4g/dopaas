/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.pipeline.handler.DockerNativePipelineHandler;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.codec.FileCodec;

import java.io.File;

/**
 * Docker native integrate pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class DockerNativePipelineProvider extends AbstractPipelineProvider {

    public DockerNativePipelineProvider(PipelineInfo deployProviderBean) {
        super(deployProviderBean);
    }

    /**
     * execute -- build , push , pull , run
     *
     * @throws Exception
     */
    @Override
    public void execute() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setProjectId(getPipelineInfo().getProject().getId());
        build(getPipelineInfo().getTaskHistory(), taskResult, false);

        // get sha and md5
        setShaGit(GitUtils.getLatestCommitted(getPipelineInfo().getPath()));

        // docker build
        dockerBuild(getPipelineInfo().getPath());

        // Each install pull and restart
        for (AppInstance instance : getPipelineInfo().getInstances()) {
            Runnable task = new DockerNativePipelineHandler(this, getPipelineInfo().getProject(), instance, getPipelineInfo().getTaskHistoryDetails());
            Thread t = new Thread(task);
            t.start();
            t.join();
        }

        if (log.isInfoEnabled()) {
            log.info("Maven assemble deploy done!");
        }
    }

    /**
     * Roll-back
     *
     * @throws Exception
     */
    @Override
    public void rollback() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setProjectId(getPipelineInfo().getProject().getId());

        // check bakup file isExist
        String oldFilePath = config.getBackup().getBaseDir() + "/" + subPackname(getPipelineInfo().getProject().getTarPath()) + "#"
                + getPipelineInfo().getTaskHistory().getRefId();

        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {
            getBackupLocal(oldFilePath, getPipelineInfo().getPath() + getPipelineInfo().getProject().getTarPath());
            setShaGit(getPipelineInfo().getRefTaskHistory().getShaGit());
        } else {
            build(getPipelineInfo().getTaskHistory(), taskResult, true);
            setShaGit(GitUtils.getLatestCommitted(getPipelineInfo().getPath()));
        }

        setShaLocal(FileCodec.getFileMD5(new File(getPipelineInfo().getPath() + getPipelineInfo().getProject().getTarPath())));
        // backup in local
        //backupLocal(getPath() + getProject().getTarPath(), getTaskHistory().getId().toString());

        // scp to server
        for (AppInstance instance : getPipelineInfo().getInstances()) {
            Runnable task = new DockerNativePipelineHandler(this, getPipelineInfo().getProject(), instance, getPipelineInfo().getTaskHistoryDetails());
            Thread t = new Thread(task);
            t.start();
            t.join();
        }

        if (log.isInfoEnabled()) {
            log.info("Maven assemble deploy done!");
        }
    }

}