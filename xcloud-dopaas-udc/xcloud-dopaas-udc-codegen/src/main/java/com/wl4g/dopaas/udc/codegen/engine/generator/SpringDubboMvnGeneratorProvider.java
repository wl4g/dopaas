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
package com.wl4g.dopaas.udc.codegen.engine.generator;

import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.constant.UdcConstants.GenProviderAlias;
import com.wl4g.dopaas.udc.codegen.engine.context.GenerateContext;

/**
 * {@link SpringDubboMvnGeneratorProvider}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-10-08
 * @sine v1.0.0
 * @see
 */
public class SpringDubboMvnGeneratorProvider extends BasedJvmGeneratorProvider {

	public SpringDubboMvnGeneratorProvider(@NotNull GenerateContext context) {
		super(context, null);
	}

	@Override
	public void doGenerate() throws Exception {
		doGenerateWithTemplates(GenProviderAlias.SPINGDUBBO_MVN);
	}

}