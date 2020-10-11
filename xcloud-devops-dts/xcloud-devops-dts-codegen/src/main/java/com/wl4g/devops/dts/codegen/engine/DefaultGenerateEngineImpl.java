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

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.components.common.lang.StringUtils2;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import com.wl4g.devops.dts.codegen.bean.extra.TableExtraOptionDefinition.GenTableExtraOption;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.dao.GenDataSourceDao;
import com.wl4g.devops.dts.codegen.dao.GenProjectDao;
import com.wl4g.devops.dts.codegen.dao.GenTableColumnDao;
import com.wl4g.devops.dts.codegen.dao.GenTableDao;
import com.wl4g.devops.dts.codegen.engine.context.DefaultGenerateContext;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.context.GeneratedResult;
import com.wl4g.devops.dts.codegen.engine.context.GenericParameter;
import com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator;
import com.wl4g.devops.dts.codegen.service.GenProjectService;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.String.valueOf;

import java.io.IOException;
import java.util.List;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.components.core.bean.BaseBean.DISABLED;
import static com.wl4g.devops.dts.codegen.engine.GenProviderSetDefinition.getProviders;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

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
	 * {@link GenTemplateLocator}
	 */
	@Autowired
	protected GenTemplateLocator locator;

	/**
	 * {@link NamingPrototypeBeanFactory}
	 */
	@Autowired
	protected NamingPrototypeBeanFactory beanFactory;

	@Autowired
	protected GenTableDao genTableDao;

	@Autowired
	protected GenDataSourceDao genDataSourceDao;

	@Autowired
	protected GenProjectDao genProjectDao;

	@Autowired
	protected GenTableColumnDao genColumnDao;

	@Autowired
	protected GenProjectService genProjectService;

	@Override
	public GeneratedResult execute(GenericParameter param) {
		// Gets gen project.
		GenProject project = genProjectService.detail(param.getProjectId());
		notNullOf(project.getExtraOptions(), "projectExtraOptions");

		// Gets genDatasource.
		GenDataSource datasource = genDataSourceDao.selectByPrimaryKey(project.getDatasourceId());

		// Gets genTable.
		List<GenTable> tables = genTableDao.selectByProjectId(param.getProjectId());
		for (GenTable tab : tables) {
			// Skip disable genTable(entity) rendering.
			if (equalsIgnoreCase(tab.getStatus(), valueOf(DISABLED))) {
				continue;
			}
			// Gets genTable columns.
			tab.setGenTableColumns(genColumnDao.selectByTableId(tab.getId()));
			// Gets primary column.
			tab.setPk(notNull(getGenColumnsPrimaryKey(tab.getGenTableColumns()), "'%s' has no primary key?", tab.getTableName()));
			// Table extra options.
			tab.setExtraOptions(parseJSON(tab.getExtraOptionsJson(), new TypeReference<List<GenTableExtraOption>>() {
			}));
		}
		project.setGenTables(tables);

		// Gets DB metadata resolver.
		MetadataResolver resolver = beanFactory.getPrototypeBean(datasource.getType(), datasource);
		// Create generate context.
		GenerateContext context = new DefaultGenerateContext(config, locator, resolver, project, datasource);

		// Invoking generate with providers.
		List<String> providers = getProviders(project.getProviderSet());
		safeList(providers).forEach(p -> {
			try (GeneratorProvider generator = beanFactory.getPrototypeBean(p, context);) {
				generator.run();
			} catch (IOException e) {
				log.error("", e);
			}
		});

		log.info("Generated projec codes successfully. project: {}", toJSONString(project));
		return new GeneratedResult(project, datasource, context.getJobId());
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