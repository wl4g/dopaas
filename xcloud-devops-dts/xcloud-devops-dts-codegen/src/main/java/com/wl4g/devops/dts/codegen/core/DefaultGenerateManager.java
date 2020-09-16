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
package com.wl4g.devops.dts.codegen.core;

import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.core.context.DefaultGenerateContext;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.core.param.GenericParameter;
import com.wl4g.devops.dts.codegen.dao.GenProjectDao;
import com.wl4g.devops.dts.codegen.dao.GenTableColumnDao;
import com.wl4g.devops.dts.codegen.dao.GenTableDao;
import com.wl4g.devops.dts.codegen.engine.GeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.GeneratorProvider.GenProviderAlias;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.wl4g.components.common.lang.Assert2.notNullOf;

/**
 * {@link DefaultGenerateManager}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class DefaultGenerateManager implements GenerateManager {

	@Autowired
	protected CodegenProperties config;

	/** {@link NamingPrototypeBeanFactory} */
	protected final NamingPrototypeBeanFactory beanFactory;

	@Autowired
	protected GenTableDao genTableDao;

	@Autowired
	protected GenProjectDao genProjectDao;

	@Autowired
	protected GenTableColumnDao genColumnDao;

	public DefaultGenerateManager(NamingPrototypeBeanFactory beanFactory) {
		notNullOf(beanFactory, "beanFactory");
		this.beanFactory = beanFactory;
	}

	@Override
	public void execute(GenericParameter param) {
		// Gets generate configuration.

		GenProject genProject = genProjectDao.selectByPrimaryKey(param.getProjectId());

		List<GenTable> genTables = genTableDao.selectByProjectId(param.getProjectId());
		for(GenTable genTable : genTables){
			List<GenTableColumn> genColumns = genColumnDao.selectByTableId(genTable.getId());
			genTable.setGenTableColumns(genColumns);
		}
		genProject.setGenTables(genTables);

		// New context.
		GenerateContext context = new DefaultGenerateContext(config, genProject);
		// TODO ...

		GeneratorProvider provider = beanFactory.getPrototypeBean(GenProviderAlias.SPINGCLOUD_MVN, context);
		provider.run();

		// TODO package return

	}

}