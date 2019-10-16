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

/**
 * CICD configuration properties.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public class CiCdProperties {

	/**
	 * Pipeline executor properties.
	 */
	private ExecutorProperties executor = new ExecutorProperties();

	/**
	 * Pipeline VCS properties.
	 */
	private VcsSourceProperties vcs = new VcsSourceProperties();

	/**
	 * Pipeline job properties.
	 */
	private JobProperties job = new JobProperties();

	/**
	 * Pipeline transform properties.
	 */
	private TranformProperties tranform = new TranformProperties();

	public ExecutorProperties getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorProperties executor) {
		this.executor = executor;
	}

	public VcsSourceProperties getVcs() {
		return vcs;
	}

	public void setVcs(VcsSourceProperties vcs) {
		this.vcs = vcs;
	}

	public JobProperties getJob() {
		return job;
	}

	public void setJob(JobProperties job) {
		this.job = job;
	}

	public TranformProperties getTranform() {
		return tranform;
	}

	public void setTranform(TranformProperties tranform) {
		this.tranform = tranform;
	}

}