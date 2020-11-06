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
package com.wl4g.devops.dts.codegen.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.dao.GenDataSourceDao;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.service.GenDataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class GenDataSourceServiceImpl implements GenDataSourceService {

	final private static SmartLogger log = SmartLoggerFactory.getLogger(GenDataSourceServiceImpl.class);

	@Autowired
	protected NamingPrototypeBeanFactory beanFactory;

	@Autowired
	private GenDataSourceDao genDSDao;

	@Override
	public PageModel<GenDataSource> page(PageModel<GenDataSource> pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		// desensitization
		pm.setRecords(safeList(genDSDao.list(name)).stream().map(ds -> ds.withPassword("******")).collect(toList()));
		return pm;
	}

	@Override
	public List<GenDataSource> loadDatasources() {
		// desensitization
		return safeList(genDSDao.list(null)).stream().map(ds -> ds.withPassword("******")).collect(toList());
	}

	public void save(GenDataSource datasource) {
		try (MetadataResolver resolver = beanFactory.getPrototypeBean(datasource.getType(), datasource);) {
			datasource.setDbversion(resolver.findDBVersion());
		} catch (Exception e) {
			log.error("Cannot get db version", e);
		}

		if (isNull(datasource.getId())) {
			datasource.preInsert(getRequestOrganizationCode());
			insert(datasource);
		} else {
			datasource.preUpdate();
			update(datasource);
		}
	}

	private void insert(GenDataSource gen) {
		genDSDao.insertSelective(gen);
	}

	private void update(GenDataSource gen) {
		if (StringUtils.equals("******", gen.getPassword())) {
			gen.setPassword(null);
		}
		genDSDao.updateByPrimaryKeySelective(gen);
	}

	public GenDataSource detail(Long id) {
		notNullOf(id, "id");
		return genDSDao.selectByPrimaryKey(id);
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		GenDataSource gen = new GenDataSource();
		gen.setId(id);
		gen.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genDSDao.updateByPrimaryKeySelective(gen);
	}

	public void testConnectDb(GenDataSource datasource) throws Exception {
		notNullOf(datasource, "genDataSource");
		hasTextOf(datasource.getType(), "dbType");

		// Test the connect directly before editing and saving
		if (isNull(datasource.getHost())) {
			// Use testing connect on the list after saving
			datasource = genDSDao.selectByPrimaryKey(datasource.getId());
		}

		try (MetadataResolver resolver = beanFactory.getPrototypeBean(datasource.getType(), datasource);) {
			String dbVersion = resolver.findDBVersion();
			log.info("Tested gen datasource connection and find that the DB server version is: {}({})", datasource.getType(),
					dbVersion);
		}

	}

}