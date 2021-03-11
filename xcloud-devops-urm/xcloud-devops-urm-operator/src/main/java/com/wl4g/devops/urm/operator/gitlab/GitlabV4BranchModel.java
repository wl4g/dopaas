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
package com.wl4g.devops.urm.operator.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.urm.operator.model.VcsBranchModel;

/**
 * {@link GitlabV4BranchModel}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2020-04-20 14:40:00
 * @sine v1.0.0
 * @see
 */
public class GitlabV4BranchModel extends VcsBranchModel {

	private Boolean merged;
	@JsonProperty(value = "protected")
	public boolean _protected;
	private Boolean developers_can_push;
	private Boolean developers_can_merge;
	private Boolean can_push;
	@JsonProperty(value = "default")
	public boolean _default;
	private Commit commit;

	public Boolean getMerged() {
		return merged;
	}

	public void setMerged(Boolean merged) {
		this.merged = merged;
	}

	public boolean is_protected() {
		return _protected;
	}

	public void set_protected(boolean _protected) {
		this._protected = _protected;
	}

	public Boolean getDevelopers_can_push() {
		return developers_can_push;
	}

	public void setDevelopers_can_push(Boolean developers_can_push) {
		this.developers_can_push = developers_can_push;
	}

	public Boolean getDevelopers_can_merge() {
		return developers_can_merge;
	}

	public void setDevelopers_can_merge(Boolean developers_can_merge) {
		this.developers_can_merge = developers_can_merge;
	}

	public Boolean getCan_push() {
		return can_push;
	}

	public void setCan_push(Boolean can_push) {
		this.can_push = can_push;
	}

	public boolean is_default() {
		return _default;
	}

	public void set_default(boolean _default) {
		this._default = _default;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	public static class Commit {
		private String id;
		private String short_id;
		private String created_at;
		private String title;
		private String message;
		private String author_name;
		private String author_email;
		private String authored_date;
		private String committer_name;
		private String committer_email;
		private String committed_date;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getShort_id() {
			return short_id;
		}

		public void setShort_id(String short_id) {
			this.short_id = short_id;
		}

		public String getCreated_at() {
			return created_at;
		}

		public void setCreated_at(String created_at) {
			this.created_at = created_at;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getAuthor_name() {
			return author_name;
		}

		public void setAuthor_name(String author_name) {
			this.author_name = author_name;
		}

		public String getAuthor_email() {
			return author_email;
		}

		public void setAuthor_email(String author_email) {
			this.author_email = author_email;
		}

		public String getAuthored_date() {
			return authored_date;
		}

		public void setAuthored_date(String authored_date) {
			this.authored_date = authored_date;
		}

		public String getCommitter_name() {
			return committer_name;
		}

		public void setCommitter_name(String committer_name) {
			this.committer_name = committer_name;
		}

		public String getCommitter_email() {
			return committer_email;
		}

		public void setCommitter_email(String committer_email) {
			this.committer_email = committer_email;
		}

		public String getCommitted_date() {
			return committed_date;
		}

		public void setCommitted_date(String committed_date) {
			this.committed_date = committed_date;
		}
	}

}