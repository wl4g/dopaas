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
package com.wl4g.devops.uci.pipeline.provider.app;

import com.wl4g.devops.uci.core.context.PipelineContext;
import com.wl4g.devops.uci.pipeline.provider.RestorableDeployPipelineProvider;
import com.wl4g.devops.common.bean.cmdb.AppInstance;

import java.io.File;

/**
 * Android projects pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 */
public class AndroidPipelineProvider extends RestorableDeployPipelineProvider {

	public AndroidPipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	public void execute() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rollback() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Runnable newPipeDeployer(AppInstance instance) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doBuildWithDefaultCommand(String projectDir, File jobLogFile, Long taskId) throws Exception {
		throw new UnsupportedOperationException();
	}

}