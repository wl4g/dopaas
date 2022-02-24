/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.urm.config;

import com.wl4g.infra.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.dopaas.urm.operator.VcsOperator;
import com.wl4g.dopaas.urm.operator.alicode.AlicodeVcsOperator;
import com.wl4g.dopaas.urm.operator.bitbucket.BitbucketVcsOperator;
import com.wl4g.dopaas.urm.operator.coding.CodingVcsOperator;
import com.wl4g.dopaas.urm.operator.gitee.GiteeVcsOperator;
import com.wl4g.dopaas.urm.operator.github.GithubVcsOperator;
import com.wl4g.dopaas.urm.operator.gitlab.GitlabV4VcsOperator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.wl4g.dopaas.urm.operator.VcsOperator.VcsProviderKind;

/**
 * Vcs auto configuration.
 *
 * @version v1.0 2019年5月21日
 * @since
 */
@Configuration
public class UrmOperatorAutoConfiguration {

	// --- VCS operator's ---

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
	public GenericOperatorAdapter<VcsProviderKind, VcsOperator> compositeVcsOperateAdapter(List<VcsOperator> operators) {
		return new GenericOperatorAdapter<VcsProviderKind, VcsOperator>(operators) {
		};
	}

}