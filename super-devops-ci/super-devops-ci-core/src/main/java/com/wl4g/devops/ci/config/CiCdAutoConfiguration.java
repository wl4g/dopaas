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

import com.wl4g.devops.ci.console.CiCdConsole;
import com.wl4g.devops.ci.core.DefaultPipeline;
import com.wl4g.devops.ci.core.Pipeline;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.pipeline.*;
import com.wl4g.devops.ci.pipeline.job.DjangoStandardTransferJob;
import com.wl4g.devops.ci.pipeline.job.DockerNativePipeTransferJob;
import com.wl4g.devops.ci.pipeline.job.MvnAssembleTarPipeTransferJob;
import com.wl4g.devops.ci.pipeline.job.NpmViewPipeTransferJob;
import com.wl4g.devops.ci.pipeline.job.SpringExecutableJarTransferJob;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.pipeline.timing.TimingPipelineManager;
import com.wl4g.devops.ci.pipeline.timing.TimingPipelineJob;
import com.wl4g.devops.ci.vcs.git.GitlabV4VcsOperator;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.support.beans.prototype.DelegateAlias;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

/**
 * CICD auto configuration.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月21日
 * @since
 */
@Configuration
public class CiCdAutoConfiguration {

	// --- BASIC ---

	@Bean
	@ConfigurationProperties(prefix = "pipeline")
	public CiCdProperties ciCdProperties() {
		return new CiCdProperties();
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

	@Bean
	public PipelineJobExecutor pipelineJobExecutor(CiCdProperties config) {
		return new PipelineJobExecutor(config);
	}

	@Bean
	public Pipeline defaultPipeline() {
		return new DefaultPipeline();
	}

	@Bean
	public GitlabV4VcsOperator gitlabV4Operator() {
		return new GitlabV4VcsOperator();
	}

	@Bean
	public GlobalTimeoutJobCleanupFinalizer globalTimeoutJobCleanFinalizer() {
		return new GlobalTimeoutJobCleanupFinalizer();
	}

	// --- CONSOLE ---

	@Bean
	public CiCdConsole cicdConsole() {
		return new CiCdConsole();
	}

	// --- PIPELINE PROVIDERs ---

	@Bean
	@DelegateAlias({ PipelineType.MVN_ASSEMBLE_TAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipelineProvider mvnAssembleTarPipelineProvider(PipelineInfo info) {
		return new MvnAssembleTarPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.SPRING_EXECUTABLE_JAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarPipelineProvider springExecutableJarPipelineProvider(PipelineInfo info) {
		return new SpringExecutableJarPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.DOCKER_NATIVE })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipelineProvider dockerNativePipelineProvider(PipelineInfo info) {
		return new DockerNativePipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.DJANGO_STANDARD })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DjangoStandardPipelineProvider djangoStandardPipelineProvider(PipelineInfo info) {
		return new DjangoStandardPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.NPM_VIEW })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NpmViewPipelineProvider npmViewPipelineProvider(PipelineInfo info) {
		return new NpmViewPipelineProvider(info);
	}

	// --- PIPELINE TRANSFER JOBs ---

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipeTransferJob mvnAssembleTarPipeTransferJob(MvnAssembleTarPipelineProvider provider, Project project,
			AppInstance instance, List<TaskHistoryDetail> taskHistoryDetails, String tarPath, String path) {
		return new MvnAssembleTarPipeTransferJob(provider, project, instance, taskHistoryDetails, tarPath, path);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipeTransferJob dockerNativePipeTransferJob(DockerNativePipelineProvider provider, Project project,
			AppInstance instance, List<TaskHistoryDetail> taskHistoryDetails) {
		return new DockerNativePipeTransferJob(provider, project, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NpmViewPipeTransferJob npmViewPipeTransferJob(NpmViewPipelineProvider provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		return new NpmViewPipeTransferJob(provider, project, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarTransferJob springExecutableJarTransferJob(SpringExecutableJarPipelineProvider provider,
			Project project, AppInstance instance, List<TaskHistoryDetail> taskHistoryDetails) {
		return new SpringExecutableJarTransferJob(provider, project, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DjangoStandardTransferJob djangoStandardTransferJob(DjangoStandardPipelineProvider provider, Project project,
			AppInstance instance, List<TaskHistoryDetail> taskHistoryDetails) {
		return new DjangoStandardTransferJob(provider, project, instance, taskHistoryDetails);
	}

	// --- TIMING SCHEDULE ---

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public TimingPipelineJob timingPipelineJob(Trigger trigger, Project project, Task task, List<TaskDetail> taskDetails) {
		return new TimingPipelineJob(trigger, project, task, taskDetails);
	}

	@Bean
	public TimingPipelineManager timingPipelineManager() {
		return new TimingPipelineManager();
	}

}