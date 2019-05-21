package com.wl4g.devops.common.bean.ci.dto;

/** get from hook's info
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
		this.branchName = a[a.length-1];
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

	public class Repository{

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
	}


}
