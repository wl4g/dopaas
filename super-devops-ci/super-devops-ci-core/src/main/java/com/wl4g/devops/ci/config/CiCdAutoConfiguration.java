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
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.pcm.PcmOperator;
import com.wl4g.devops.ci.pcm.PcmOperator.PcmKind;
import com.wl4g.devops.ci.pcm.jira.JiraPcmOperator;
import com.wl4g.devops.ci.pcm.redmine.RedminePcmOperator;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.pipeline.*;
import com.wl4g.devops.ci.pipeline.coordinate.GlobalTimeoutJobCleanupCoordinator;
import com.wl4g.devops.ci.pipeline.deploy.Python3PipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.RktNativePipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.DockerNativePipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.GolangModPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.MvnAssembleTarPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.NpmViewPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.SpringExecutableJarPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.ViewNativePipeDeployer;
import com.wl4g.devops.ci.pipeline.timing.PipelineTaskScheduler;
import com.wl4g.devops.ci.vcs.VcsOperator;
import com.wl4g.devops.ci.vcs.VcsOperator.VcsProviderKind;
import com.wl4g.devops.ci.vcs.alicode.AlicodeVcsOperator;
import com.wl4g.devops.ci.vcs.bitbucket.BitbucketVcsOperator;
import com.wl4g.devops.ci.vcs.coding.CodingVcsOperator;
import com.wl4g.devops.ci.vcs.gitee.GiteeVcsOperator;
import com.wl4g.devops.ci.vcs.github.GithubVcsOperator;
import com.wl4g.devops.ci.vcs.gitlab.GitlabV4VcsOperator;
import com.wl4g.devops.ci.pipeline.timing.TimingPipelineProvider;
import com.wl4g.devops.ci.tool.LogPipelineCleaner;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.framework.beans.PrototypeAlias;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;

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

	// --- Basic's ---

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
	public DefaultPipelineManager defualtPipelineManager() {
		return new DefaultPipelineManager();
	}

	@Bean
	public GlobalTimeoutJobCleanupCoordinator globalTimeoutJobCleanCoordinator() {
		return new GlobalTimeoutJobCleanupCoordinator();
	}

	// --- Console's. ---

	@Bean
	public CiCdConsole cicdConsole() {
		return new CiCdConsole();
	}

	// --- VCS's (Version Control System) ---

	@Bean
	public GitlabV4VcsOperator gitlabV4VcsOperator() {
		return new GitlabV4VcsOperator();
	}

	@Bean
	public GithubVcsOperator githubV4VcsOperator() {
		return new GithubVcsOperator();
	}

	@Bean
	public BitbucketVcsOperator bitbucketVcsOperator() {
		return new BitbucketVcsOperator();
	}

	@Bean
	public CodingVcsOperator codingVcsOperator() {
		return new CodingVcsOperator();
	}

	@Bean
	public GiteeVcsOperator giteeVcsOperator() {
		return new GiteeVcsOperator();
	}

	@Bean
	public AlicodeVcsOperator alicodeVcsOperator() {
		return new AlicodeVcsOperator();
	}

	@Bean
	public GenericOperatorAdapter<VcsProviderKind, VcsOperator> compositeVcsOperateAdapter(List<VcsOperator> operators) {
		return new GenericOperatorAdapter<VcsProviderKind, VcsOperator>(operators) {
		};
	}

	// --- Flow Manager ---
	@Bean
	public FlowManager flowManager() {
		return new FlowManager();
	}

	// --- Pipeline provider's. ---

	@Bean
	@PrototypeAlias({ PipelineKind.MVN_ASSEMBLE_TAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipelineProvider mvnAssembleTarPipelineProvider(PipelineContext context) {
		return new MvnAssembleTarPipelineProvider(context);
	}

	@Bean
	@PrototypeAlias({ PipelineKind.SPRING_EXECUTABLE_JAR })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarPipelineProvider springExecutableJarPipelineProvider(PipelineContext context) {
		return new SpringExecutableJarPipelineProvider(context);
	}

	@Bean
	@PrototypeAlias({ PipelineKind.NPM_VIEW })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NpmViewPipelineProvider npmViewPipelineProvider(PipelineContext context) {
		return new NpmViewPipelineProvider(context);
	}

	@Bean
	@PrototypeAlias({ PipelineKind.VIEW_NATIVE })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ViewNativePipelineProvider viewNativePipelineProvider(PipelineContext context) {
		return new ViewNativePipelineProvider(context);
	}

	@Bean
	@PrototypeAlias({ PipelineKind.PYTHON3_STANDARD })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Python3PipelineProvider python3StandardPipelineProvider(PipelineContext context) {
		return new Python3PipelineProvider(context);
	}

	@Bean
	@PrototypeAlias({ PipelineKind.GOLANG_STANDARD })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public GolangModPipelineProvider golangModPipelineProvider(PipelineContext context) {
		return new GolangModPipelineProvider(context);
	}

	@Bean
	@PrototypeAlias({ PipelineKind.DOCKER_NATIVE })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipelineProvider dockerNativePipelineProvider(PipelineContext context) {
		return new DockerNativePipelineProvider(context);
	}

	@Bean
	@PrototypeAlias({ PipelineKind.RKT_NATIVE })
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
	public ViewNativePipeDeployer viewNativePipeDeployer(ViewNativePipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new ViewNativePipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarPipeDeployer springExecutableJarPipeDeployer(SpringExecutableJarPipelineProvider provider,
			AppInstance instance, List<TaskHistoryInstance> taskHistoryInstances) {
		return new SpringExecutableJarPipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Python3PipeDeployer python3PipeDeployer(Python3PipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new Python3PipeDeployer(provider, instance, taskHistoryInstances);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public GolangModPipeDeployer golangModPipeDeployer(Python3PipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		return new GolangModPipeDeployer(provider, instance, taskHistoryInstances);
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

	// --- Timing scheduling's. ---

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public TimingPipelineProvider timingPipelineJob(Trigger trigger, Project project, Task task,
			List<TaskInstance> taskInstances) {
		return new TimingPipelineProvider(trigger, project, task, taskInstances);
	}

	@Bean
	public PipelineTaskScheduler timingPipelineManager() {
		return new PipelineTaskScheduler();
	}

	// --- Tool's. ---

	@Bean
	public LogPipelineCleaner logPipelineCleaner() {
		return new LogPipelineCleaner();
	}

	// --- PCM's (Project collaboration management). ---

	@Bean
	public JiraPcmOperator jiraPcmOperator() {
		return new JiraPcmOperator();
	}

	@Bean
	public RedminePcmOperator redminePcmOperator() {
		return new RedminePcmOperator();
	}

	@Bean
	public GenericOperatorAdapter<PcmKind, PcmOperator> compositePcmOperatorAdapter(List<PcmOperator> operators) {
		return new GenericOperatorAdapter<PcmKind, PcmOperator>(operators) {
		};
	}

}