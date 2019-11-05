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
import com.wl4g.devops.ci.core.DefaultPipelineManager;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.PipelineContext;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.pipeline.*;
import com.wl4g.devops.ci.pipeline.job.DjangoStandardTransferJob;
import com.wl4g.devops.ci.pipeline.job.DockerNativePipeTransferJob;
import com.wl4g.devops.ci.pipeline.job.GolangTransferJob;
import com.wl4g.devops.ci.pipeline.job.MvnAssembleTarPipeTransferJob;
import com.wl4g.devops.ci.pipeline.job.NpmViewPipeTransferJob;
import com.wl4g.devops.ci.pipeline.job.SpringExecutableJarTransferJob;
import com.wl4g.devops.ci.pipeline.timing.TimingPipelineManager;
import com.wl4g.devops.ci.vcs.CompositeVcsOperateAdapter;
import com.wl4g.devops.ci.vcs.VcsOperator;
import com.wl4g.devops.ci.vcs.alicode.AlicodeVcsOperator;
import com.wl4g.devops.ci.vcs.bitbucket.BitbucketVcsOperator;
import com.wl4g.devops.ci.vcs.coding.CodingVcsOperator;
import com.wl4g.devops.ci.vcs.gitee.GiteeVcsOperator;
import com.wl4g.devops.ci.vcs.github.GithubVcsOperator;
import com.wl4g.devops.ci.vcs.gitlab.GitlabV4VcsOperator;
import com.wl4g.devops.ci.pipeline.timing.TimingPipelineJob;
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
	public PipelineManager defaultPipeliner() {
		return new DefaultPipelineManager();
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

	// --- VCS ---

	@Bean
	public VcsOperator gitlabV4VcsOperator() {
		return new GitlabV4VcsOperator();
	}

	@Bean
	public VcsOperator githubV4VcsOperator() {
		return new GithubVcsOperator();
	}

	@Bean
	public VcsOperator bitbucketVcsOperator() {
		return new BitbucketVcsOperator();
	}

	@Bean
	public VcsOperator codingVcsOperator() {
		return new CodingVcsOperator();
	}

	@Bean
	public VcsOperator giteeVcsOperator() {
		return new GiteeVcsOperator();
	}

	@Bean
	public VcsOperator alicodeVcsOperator() {
		return new AlicodeVcsOperator();
	}

	@Bean
	public CompositeVcsOperateAdapter compositeVcsOperateAdapter(List<VcsOperator> operators) {
		return new CompositeVcsOperateAdapter(operators);
	}

	// --- Pipeline providers. ---

	@Bean
	@DelegateAlias({ PipelineType.MVN_ASSEMBLE_TAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipelineProvider mvnAssembleTarPipelineProvider(PipelineContext info) {
		return new MvnAssembleTarPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.SPRING_EXECUTABLE_JAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarPipelineProvider springExecutableJarPipelineProvider(PipelineContext info) {
		return new SpringExecutableJarPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.DOCKER_NATIVE })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipelineProvider dockerNativePipelineProvider(PipelineContext info) {
		return new DockerNativePipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.NPM_VIEW })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NpmViewPipelineProvider npmViewPipelineProvider(PipelineContext info) {
		return new NpmViewPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.DJANGO_STANDARD })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DjangoStandardPipelineProvider djangoStandardPipelineProvider(PipelineContext info) {
		return new DjangoStandardPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.GOLANG_STANDARD })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public GolangPipelineProvider golangPipelineProvider(PipelineContext info) {
		return new GolangPipelineProvider(info);
	}

	// --- Pipeline transfer jobs. ---

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipeTransferJob mvnAssembleTarPipeTransferJob(MvnAssembleTarPipelineProvider provider,
			AppInstance instance, List<TaskHistoryDetail> taskHistoryDetails) {
		return new MvnAssembleTarPipeTransferJob(provider, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipeTransferJob dockerNativePipeTransferJob(DockerNativePipelineProvider provider, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		return new DockerNativePipeTransferJob(provider, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NpmViewPipeTransferJob npmViewPipeTransferJob(NpmViewPipelineProvider provider, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		return new NpmViewPipeTransferJob(provider, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarTransferJob springExecutableJarTransferJob(SpringExecutableJarPipelineProvider provider,
			AppInstance instance, List<TaskHistoryDetail> taskHistoryDetails) {
		return new SpringExecutableJarTransferJob(provider, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DjangoStandardTransferJob djangoStandardTransferJob(DjangoStandardPipelineProvider provider, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		return new DjangoStandardTransferJob(provider, instance, taskHistoryDetails);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public GolangTransferJob golangTransferJob(DjangoStandardPipelineProvider provider, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		return new GolangTransferJob(provider, instance, taskHistoryDetails);
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