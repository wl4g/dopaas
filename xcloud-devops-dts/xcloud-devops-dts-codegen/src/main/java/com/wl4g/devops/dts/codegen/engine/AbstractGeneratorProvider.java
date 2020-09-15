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
package com.wl4g.devops.dts.codegen.engine;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.utils.GenCodeUtils;

import java.io.File;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

/**
 * {@link AbstractGeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public abstract class AbstractGeneratorProvider implements GeneratorProvider {

	protected final SmartLogger log = getLogger(getClass());

	/** {@link GenerateContext} */
	protected final GenerateContext context;

	public AbstractGeneratorProvider(GenerateContext context) {
		notNullOf(context, "context");
		this.context = context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	void genFileToLoacl(String templateName) throws Exception {
		GenProject genProject = context.getGenProject();

		File jobDir = context.getJobDir();

		//TODO
		String genPath = "/Users/vjay/git/xcloud-devops/xcloud-devops-dts/xcloud-devops-dts-codegen/src/main/resources/ftl/"+templateName;
		GenCodeUtils.genCode(genPath, genProject, jobDir.getAbsolutePath());

	}

}