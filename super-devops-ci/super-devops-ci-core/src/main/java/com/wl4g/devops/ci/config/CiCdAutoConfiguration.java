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
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.pipeline.*;
import com.wl4g.devops.ci.pipeline.coordinate.GlobalTimeoutJobCleanupCoordinator;
import com.wl4g.devops.ci.pipeline.deploy.Python3StandardPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.RktNativePipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.DockerNativePipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.GolangStandardPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.MvnAssembleTarPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.NpmViewPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.SpringExecutableJarPipeDeployer;
import com.wl4g.devops.ci.pipeline.timing.PipelineTaskScheduler;
import com.wl4g.devops.ci.vcs.CompositeVcsOperateAdapter;
import com.wl4g.devops.ci.vcs.VcsOperator;
import com.wl4g.devops.ci.vcs.alicode.AlicodeVcsOperator;
import com.wl4g.devops.ci.vcs.bitbucket.BitbucketVcsOperator;
import com.wl4g.devops.ci.vcs.coding.CodingVcsOperator;
import com.wl4g.devops.ci.vcs.gitee.GiteeVcsOperator;
import com.wl4g.devops.ci.vcs.github.GithubVcsOperator;
import com.wl4g.devops.ci.vcs.gitlab.GitlabV4VcsOperator;
import com.wl4g.devops.ci.pipeline.timing.TimingPipelineProvider;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.framework.context.DelegateAlias;

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
	@ConfigurationProperties(prefix = "spring.cloud.devops.ci.pipeline")
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
	public PipelineManager defualtPipelineManager() {
		return new DefaultPipelineManager();
	}

	@Bean
	public GlobalTimeoutJobCleanupCoordinator globalTimeoutJobCleanCoordinator() {
		return new GlobalTimeoutJobCleanupCoordinator();
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

	// --- Pipeline provider's. ---

	@Bean
	@DelegateAlias({ PipelineType.MVN_ASSEMBLE_TAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipelineProvider mvnAssembleTarPipelineProvider(PipelineContext context) {
		return new MvnAssembleTarPipelineProvider(context);
	}

	@Bean
	@DelegateAlias({ PipelineType.SPRING_EXECUTABLE_JAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarPipelineProvider springExecutableJarPipelineProvider(PipelineContext context) {
		return new SpringExecutableJarPipelineProvider(context);
	}

	@Bean
	@DelegateAlias({ PipelineType.NPM_VIEW })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NpmViewPipelineProvider npmViewPipelineProvider(PipelineContext context) {
		return new NpmViewPipelineProvider(context);
	}

	@Bean
	@DelegateAlias({ PipelineType.PYTHON3_STANDARD })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Python3StandardPipelineProvider python3StandardPipelineProvider(PipelineContext context) {
		return new Python3StandardPipelineProvider(context);
	}

	@Bean
	@DelegateAlias({ PipelineType.GOLANG_STANDARD })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public GolangStandardPipelineProvider golangStandardPipelineProvider(PipelineContext context) {
		return new GolangStandardPipelineProvider(context);
	}

	@Bean
	@DelegateAlias({ PipelineType.DOCKER_NATIVE })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipelineProvider dockerNativePipelineProvider(PipelineContext context) {
		return new DockerNativePipelineProvider(context);
	}

	@Bean
	@DelegateAlias({ PipelineType.RKT_NATIVE })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RktNativePipelineProvider rktNativePipelineProvider(PipelineContext context) {
		return new RktNativePipelineProvider(context);
	}

	// --- Pipeline deployer's. ---

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipeDeployer mvnAssembleTarPipeDeployer(MvnAssembleTarPipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new MvnAssembleTarPipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NpmViewPipeDeployer npmViewPipeDeployer(NpmViewPipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new NpmViewPipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarPipeDeployer springExecutableJarPipeDeployer(SpringExecutableJarPipelineProvider provider,
			AppInstance instance, List<TaskHistoryInstance> taskHistoryInstances) {
		return new SpringExecutableJarPipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Python3StandardPipeDeployer python3StandardPipeDeployer(Python3StandardPipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new Python3StandardPipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public GolangStandardPipeDeployer golangStandardPipeDeployer(Python3StandardPipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new GolangStandardPipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipeDeployer dockerNativePipeDeployer(DockerNativePipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new DockerNativePipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RktNativePipeDeployer rktNativePipeDeployer(RktNativePipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new RktNativePipeDeployer(provider, instance, taskHistoryInstances);
	}

	// --- TIMING SCHEDULE ---

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public TimingPipelineProvider timingPipelineJob(Trigger trigger, Project project, Task task, List<TaskInstance> taskInstances) {
		return new TimingPipelineProvider(trigger, project, task, taskInstances);
	}

	@Bean
	public PipelineTaskScheduler timingPipelineManager() {
		return new PipelineTaskScheduler();
	}

}