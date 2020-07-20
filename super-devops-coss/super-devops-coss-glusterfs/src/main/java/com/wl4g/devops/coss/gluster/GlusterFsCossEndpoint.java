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
package com.wl4g.devops.coss.gluster;

import static java.nio.file.FileSystems.*;

import com.wl4g.devops.coss.gluster.config.GlusterFsCossProperties;
import com.wl4g.devops.coss.natives.StandardFSCossEndpoint;

/**
 * {@link GlusterFsCossEndpoint}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月20日 v1.0.0
 * @see <a href=
 *      "https://github.com/gluster/glusterfs-java-filesystem">glusterfs-java-filesystem</a>
 */
public class GlusterFsCossEndpoint extends StandardFSCossEndpoint<GlusterFsCossProperties> {

	public GlusterFsCossEndpoint(GlusterFsCossProperties config) {
		super(config, getFileSystem(config.getEndpointRootDir().toURI()));
	}

	@Override
	public CossProvider kind() {
		return CossProvider.GlusterFs;
	}

}