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

import com.wl4g.components.common.lang.StringUtils2;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.dao.GenProjectDao;
import com.wl4g.devops.dts.codegen.dao.GenTableColumnDao;
import com.wl4g.devops.dts.codegen.dao.GenTableDao;
import com.wl4g.devops.dts.codegen.engine.context.DefaultGenerateContext;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.context.GenericParameter;
import com.wl4g.devops.dts.codegen.engine.provider.GeneratorProvider;
import com.wl4g.devops.dts.codegen.service.GenProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.dts.codegen.engine.provider.GeneratorProvider.GenProviderSet.getProviders;

/**
 * {@link DefaultGenerateEngineImpl}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class DefaultGenerateEngineImpl implements GenerateEngine {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * {@link CodegenProperties}
	 */
	@Autowired
	protected CodegenProperties config;

	/**
	 * {@link NamingPrototypeBeanFactory}
	 */
	@Autowired
	protected NamingPrototypeBeanFactory beanFactory;

	@Autowired
	protected GenTableDao genTableDao;

	@Autowired
	protected GenProjectDao genProjectDao;

	@Autowired
	protected GenTableColumnDao genColumnDao;

	@Autowired
	protected GenProjectService genProjectService;

	@Override
	public String execute(GenericParameter param) {
		// Gets project generates configuration.
		GenProject project = genProjectService.detail(param.getProjectId());

		List<GenTable> tabs = genTableDao.selectByProjectId(param.getProjectId());
		for (GenTable tab : tabs) {
			List<GenTableColumn> cols = genColumnDao.selectByTableId(tab.getId());
			tab.setGenTableColumns(cols);
			BeanUtils.copyProperties(project, tab, "id", "genTables");
			tab.setPk(getGenColumnsPrimaryKey(cols));
		}
		project.setGenTables(tabs);

		// Create context.
		GenerateContext context = new DefaultGenerateContext(config, project);

		// Gets Generate of providers.
		List<String> providers = getProviders(project.getProviderSet());

		// Invoke generate providers.
		for (String p : providers) {
			GeneratorProvider provider = beanFactory.getPrototypeBean(p, context);
			provider.run();
		}

		log.info("Generated projec codes successfully. project: {}", toJSONString(project));
		return context.getJobDir().getAbsolutePath();
	}

	/**
	 * Gets primary key of generate columns.
	 * 
	 * @param cols
	 * @return
	 */
	private GenTableColumn getGenColumnsPrimaryKey(List<GenTableColumn> cols) {
		return safeList(cols).stream().filter(c -> StringUtils2.isTrue(c.getIsPk())).findFirst().orElse(null);
	}

}