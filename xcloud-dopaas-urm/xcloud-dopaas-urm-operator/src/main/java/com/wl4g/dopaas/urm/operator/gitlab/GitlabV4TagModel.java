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
package com.wl4g.dopaas.urm.operator.gitlab;

import com.wl4g.dopaas.urm.operator.model.VcsTagModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link GitlabV4TagModel}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2020-04-20
 * @sine v1.0
 * @see
 */
@Getter
@Setter
@ToString
public class GitlabV4TagModel extends VcsTagModel {
	private String message;
	private String target;
	private Commit commit;
	private Object release;

	@Getter
	@Setter
	@ToString
	public static class Commit {
		private String id;
		private String short_iD;
		private String created_at;
		private String[] parent_ids;
		private String title;
		private String message;
		private String author_name;
		private String author_email;
		private String authored_date;
		private String committer_name;
		private String committer_email;
		private String committed_date;
	}

}