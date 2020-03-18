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
package com.wl4g.devops.common.bean.ci.model;

/**
 * get from hook's info
 * 
 * @author vjay
 * @date 2019-05-14 17:04:00
 */
public class HookInfo {

	private String ref;

	private String branchName;

	private Repository repository;

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
		String[] a = ref.split("/");
		this.branchName = a[a.length - 1];
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	@Override
	public String toString() {
		return "HookInfo [ref=" + ref + ", branchName=" + branchName + ", repository=" + repository + "]";
	}

	public class Repository {

		private String name;

		private String gitHttpUrl;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGitHttpUrl() {
			return gitHttpUrl;
		}

		public void setGit_http_url(String git_http_url) {
			this.gitHttpUrl = git_http_url;
		}

		@Override
		public String toString() {
			return "Repository [name=" + name + ", gitHttpUrl=" + gitHttpUrl + "]";
		}

	}

}