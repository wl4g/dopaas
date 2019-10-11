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
import com.wl4g.devops.ci.pipeline.DjangoStandardPipelineProvider;
import com.wl4g.devops.ci.pipeline.DockerNativePipelineProvider;
import com.wl4g.devops.ci.pipeline.MvnAssembleTarPipelineProvider;
import com.wl4g.devops.ci.pipeline.PipelineProvider.PipelineType;
import com.wl4g.devops.ci.pipeline.SpringExecutableJarPipelineProvider;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.vcs.git.GitlabV4VcsOperator;
import com.wl4g.devops.support.beans.DelegateAlias;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * CICD auto configuration.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月21日
 * @since
 */
@Configuration
public class CiCdAutoConfiguration {

	/*@Bean
	@ConfigurationProperties(prefix = "deploy")
	public CiCdProperties cicdProperties() {
		return new CiCdProperties();
	}*/

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
	public CiCdConsole cicdConsole() {
		return new CiCdConsole();
	}

	@Bean
	public GitlabV4VcsOperator gitlabV4Operator() {
		return new GitlabV4VcsOperator();
	}

	//
	// Pipeline provider.
	//

	@Bean
	@DelegateAlias({ PipelineType.DJANGO_STD1, PipelineType.DJANGO_STD2 })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DjangoStandardPipelineProvider djangoStandardPipelineProvider(PipelineInfo info) {
		return new DjangoStandardPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.MVN_ASSEMBLE_TAR1, PipelineType.MVN_ASSEMBLE_TAR2 })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MvnAssembleTarPipelineProvider mvnAssembleTarPipelineProvider(PipelineInfo info) {
		return new MvnAssembleTarPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.SPRING_EXECUTABLE_JAR1, PipelineType.SPRING_EXECUTABLE_JAR2 })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SpringExecutableJarPipelineProvider springExecutableJarPipelineProvider(PipelineInfo info) {
		return new SpringExecutableJarPipelineProvider(info);
	}

	@Bean
	@DelegateAlias({ PipelineType.DOCKER_NATIVE1, PipelineType.DOCKER_NATIVE2 })
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DockerNativePipelineProvider dockerNativePipelineProvider(PipelineInfo info) {
		return new DockerNativePipelineProvider(info);
	}

}