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
package com.wl4g.paas.uci.pipeline.provider;

import com.wl4g.paas.uci.core.context.PipelineContext;
import com.wl4g.paas.uci.pipeline.deploy.MvnAssembleTarPipeDeployer;
import com.wl4g.paas.common.bean.cmdb.AppInstance;

/**
 * Pipeline provider for deployment MAVEN assemble tar project.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class MvnAssembleTarPipelineProvider extends BasedMavenPipelineProvider {

	public MvnAssembleTarPipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	protected Runnable newPipeDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getPipelineHistoryInstances() };
		return beanFactory.getBean(MvnAssembleTarPipeDeployer.class, args);
	}

}