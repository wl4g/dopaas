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
package com.wl4g.dopaas.lcdp.codegen.engine;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.infra.core.bean.BaseBean.DISABLED;
import static com.wl4g.dopaas.lcdp.codegen.engine.GenProviderSetDefinition.getProviders;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.infra.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.dopaas.common.bean.lcdp.GenDataSource;
import com.wl4g.dopaas.common.bean.lcdp.GenProject;
import com.wl4g.dopaas.common.bean.lcdp.GenTable;
import com.wl4g.dopaas.common.bean.lcdp.GenTableColumn;
import com.wl4g.dopaas.common.bean.lcdp.extra.TableExtraOptionDefinition.GenTableExtraOption;
import com.wl4g.dopaas.common.bean.lcdp.model.GeneratedResult;
import com.wl4g.dopaas.lcdp.codegen.config.CodegenProperties;
import com.wl4g.dopaas.lcdp.codegen.engine.context.DefaultGenerateContext;
import com.wl4g.dopaas.lcdp.codegen.engine.context.GenerateContext;
import com.wl4g.dopaas.lcdp.codegen.engine.context.GenericParameter;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.GeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver;
import com.wl4g.dopaas.lcdp.codegen.engine.template.GenTemplateLocator;
import com.wl4g.dopaas.lcdp.codegen.service.GenDataSourceService;
import com.wl4g.dopaas.lcdp.codegen.service.GenProjectService;
import com.wl4g.dopaas.lcdp.codegen.service.GenTableService;

/**
 * {@link DefaultGenerateEngineImpl}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class DefaultGenerateEngineImpl implements GenerateEngine {
	protected final SmartLogger log = getLogger(getClass());

	protected @Autowired NamingPrototypeBeanFactory beanFactory;
	protected @Autowired CodegenProperties config;
	protected @Autowired GenTemplateLocator locator;

	protected @Autowired GenTableService genTableService;
	protected @Autowired GenDataSourceService genDataSourceService;
	protected @Autowired GenProjectService genProjectService;

	@Override
	public GeneratedResult execute(GenericParameter param) {
		// Gen project.
		GenProject project = genProjectService.detail(param.getProjectId());
		notNullOf(project.getExtraOptions(), "projectExtraOptions");

		// Gen datasource.
		GenDataSource genDataSource = genDataSourceService.detail(project.getDatasourceId());

		// Gen tables.
		List<GenTable> tables = genTableService.findGenTables(param.getProjectId());
		for (GenTable t : tables) {
			// Skip disable genTable(entity) rendering.
			if (equalsIgnoreCase(t.getStatus(), valueOf(DISABLED))) {
				continue;
			}
			// Gets genTable columns.
			t.setGenTableColumns(genTableService.findGenTableColumns(t.getId()));
			// Gets primary column.
			t.setPk(notNull(getGenColumnsPrimaryKey(t.getGenTableColumns()), "'%s' has no primary key?", t.getTableName()));
			// Table extra options.
			t.setExtraOptions(parseJSON(t.getExtraOptionsJson(), new TypeReference<List<GenTableExtraOption>>() {
			}));
		}
		project.setGenTables(tables);

		// Gets DB metadata resolver.
		MetadataResolver resolver = beanFactory.getPrototypeBean(genDataSource.getType(), genDataSource);
		// Create generate context.
		GenerateContext context = new DefaultGenerateContext(config, locator, resolver, project, genDataSource);

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
		return new GeneratedResult(project, genDataSource, context.getJobId());
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