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
package com.wl4g.devops.uci.pipeline.provider.container;

import com.wl4g.devops.uci.core.context.PipelineContext;
import com.wl4g.devops.uci.pipeline.provider.AbstractPipelineProvider;
import com.wl4g.devops.common.bean.cmdb.AppInstance;

/**
 * CoreOS(Red hat) RKT integrate pipeline provider.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-16
 * @since
 */
public class RktNativePipelineProvider extends AbstractPipelineProvider implements ContainerPipelineProvider {

	public RktNativePipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	public void buildImage() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void imagePull(String remoteHost, String user, String sshkey, String image) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void stopContainer(String remoteHost, String user, String sshkey, String container) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroyContainer(String remoteHost, String user, String sshkey, String container) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void startContainer(String remoteHost, String user, String sshkey, String runContainerCommands) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Runnable newPipeDeployer(AppInstance instance) {
		throw new UnsupportedOperationException();
	}

}