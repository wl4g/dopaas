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

import com.wl4g.components.core.bean.ci.*;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.framework.beans.NamingPrototype;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.ci.console.CiCdConsole;
import com.wl4g.devops.ci.core.DefaultPipelineManager;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.pcm.PcmOperator;
import com.wl4g.devops.ci.pcm.PcmOperator.PcmKind;
import com.wl4g.devops.ci.pcm.jira.JiraPcmOperator;
import com.wl4g.devops.ci.pcm.redmine.RedminePcmOperator;
import com.wl4g.devops.ci.pipeline.*;
import com.wl4g.devops.ci.pipeline.deploy.*;
import com.wl4g.devops.ci.pipeline.provider.GolangModPipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.MvnAssembleTarPipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.NpmViewPipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.PipelineProvider.PipelineKind;
import com.wl4g.devops.ci.pipeline.provider.PipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.Python3PipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.SpringExecutableJarPipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.TimingPipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.ViewNativePipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.container.DockerNativePipelineProvider;
import com.wl4g.devops.ci.pipeline.provider.container.RktNativePipelineProvider;
import com.wl4g.devops.ci.tool.PipelineLogPurger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * CI/CD auto configuration.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月21日
 * @since
 */
@Configuration
public class CiAutoConfiguration {

	// --- Basic's ---

	@Bean
	@ConfigurationProperties(prefix = "spring.cloud.devops.ci.pipeline")
	public CiProperties ciCdProperties() {
		return new CiProperties();
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

	@Bean
	public PipelineJobExecutor pipelineJobExecutor(CiProperties config) {
		return new PipelineJobExecutor(config);
	}

	@Bean
	public DefaultPipelineManager defualtPipelineManager() {
		return new DefaultPipelineManager();
	}

	@Bean
	public TimeoutJobsEvictor globalTimeoutJobCleanCoordinator() {
		return new TimeoutJobsEvictor();
	}

	// --- Console's. ---

	@Bean
	public CiCdConsole cicdConsole() {
		return new CiCdConsole();
	}

	// --- Flow Manager ---
	@Bean
	public FlowManager flowManager() {
		return new FlowManager();
	}

	// --- Pipeline providers. ---

	@Bean
	@NamingPrototype({ PipelineKind.MVN_ASSEMBLE_TAR })
	public MvnAssembleTarPipelineProvider mvnAssembleTarPipelineProvider(PipelineContext context) {
		return new MvnAssembleTarPipelineProvider(context);
	}

	@Bean
	@NamingPrototype({ PipelineKind.SPRING_EXECUTABLE_JAR })
	public SpringExecutableJarPipelineProvider springExecutableJarPipelineProvider(PipelineContext context) {
		return new SpringExecutableJarPipelineProvider(context);
	}

	@Bean
	@NamingPrototype({ PipelineKind.NPM_VIEW })
	public NpmViewPipelineProvider npmViewPipelineProvider(PipelineContext context) {
		return new NpmViewPipelineProvider(context);
	}

	@Bean
	@NamingPrototype({ PipelineKind.VIEW_NATIVE })
	public ViewNativePipelineProvider viewNativePipelineProvider(PipelineContext context) {
		return new ViewNativePipelineProvider(context);
	}

	@Bean
	@NamingPrototype({ PipelineKind.PYTHON3_STANDARD })
	public Python3PipelineProvider python3StandardPipelineProvider(PipelineContext context) {
		return new Python3PipelineProvider(context);
	}

	@Bean
	@NamingPrototype({ PipelineKind.GOLANG_STANDARD })
	public GolangModPipelineProvider golangModPipelineProvider(PipelineContext context) {
		return new GolangModPipelineProvider(context);
	}

	@Bean
	@NamingPrototype({ PipelineKind.DOCKER_NATIVE })
	public DockerNativePipelineProvider dockerNativePipelineProvider(PipelineContext context) {
		return new DockerNativePipelineProvider(context);
	}

	@Bean
	@NamingPrototype({ PipelineKind.RKT_NATIVE })
	public RktNativePipelineProvider rktNativePipelineProvider(PipelineContext context) {
		return new RktNativePipelineProvider(context);
	}

	// --- Pipeline deployers. ---

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public MvnAssembleTarPipeDeployer mvnAssembleTarPipeDeployer(MvnAssembleTarPipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new MvnAssembleTarPipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public NpmViewPipeDeployer npmViewPipeDeployer(NpmViewPipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new NpmViewPipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public ViewNativePipeDeployer viewNativePipeDeployer(ViewNativePipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new ViewNativePipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public SpringExecutableJarPipeDeployer springExecutableJarPipeDeployer(SpringExecutableJarPipelineProvider provider,
			AppInstance instance, List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new SpringExecutableJarPipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public Python3PipeDeployer python3PipeDeployer(Python3PipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new Python3PipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public GolangModPipeDeployer golangModPipeDeployer(Python3PipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new GolangModPipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public DockerNativePipeDeployer dockerNativePipeDeployer(PipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new DockerNativePipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public RktNativePipeDeployer rktNativePipeDeployer(RktNativePipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new RktNativePipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public CossPipeDeployer cossPipeDeployer(ViewNativePipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		return new CossPipeDeployer(provider, instance, pipelineHistoryInstances);
	}

	// --- Timing scheduling's. ---

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public TimingPipelineProvider timingPipelineProvider(Trigger trigger, Pipeline pipeline) {
		return new TimingPipelineProvider(trigger, pipeline);
	}

	@Bean
	public TimingPipelineManager pipelineTaskScheduler() {
		return new TimingPipelineManager();
	}

	// --- Tools. ---

	@Bean
	public PipelineLogPurger logPipelineCleaner() {
		return new PipelineLogPurger();
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