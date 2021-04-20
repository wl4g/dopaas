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

package com.wl4g.dopaas.common.bean.uci.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link BuildMetaInfo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2021-04-15
 * @sine v1.0
 * @see
 */
@Getter
@Setter
public class BuildMetaInfo {

	private String uciMetaVersion;
	private BuildInfo buildInfo = new BuildInfo();
	private SourceInfo sourceInfo = new SourceInfo();
	private PcmInfo pcmInfo = new PcmInfo();
	private DeployInfo deployInfo = new DeployInfo();

	@Getter
	@Setter
	public static class BuildInfo {
		private String serviceName;
		private String md5;
		private long totalBytes;
	}

	@Getter
	@Setter
	public static class DeployInfo {
		private List<String> hosts;
	}

	@Getter
	@Setter
	public static class PcmInfo {
		private String pcmProjectName;
		private String pcmIssuesId;
		private String pcmIssuesSubject;
	}

	@Getter
	@Setter
	public static class SourceInfo {
		private String projectUrl;
		private String commitId;
		private String branchOrTag;
		private long timestamp;
		private String comment;

	}
}
