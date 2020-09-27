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
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.dao.GenDataSourceDao;
import com.wl4g.devops.dts.codegen.service.GenDataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class GenDataSourceServiceImpl implements GenDataSourceService {

	@Autowired
	private GenDataSourceDao genDSDao;

	@Override
	public PageModel page(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		// desensitization
		List<GenDataSource> records = safeList(genDSDao.list(name)).stream().map(ds -> ds.withPassword("******"))
				.collect(toList());
		pm.setRecords(records);
		return pm;
	}

	@Override
	public List<GenDataSource> getForSelect() {
		// desensitization
		return safeList(genDSDao.list(null)).stream().map(ds -> ds.withPassword("******")).collect(toList());
	}

	public void save(GenDataSource gen) {
		if (isNull(gen.getId())) {
			gen.preInsert(getRequestOrganizationCode());
			insert(gen);
		} else {
			gen.preUpdate();
			update(gen);
		}
	}

	private void insert(GenDataSource gen) {
		genDSDao.insertSelective(gen);
	}

	private void update(GenDataSource gen) {
		if(StringUtils.equals("******",gen.getPassword())){
			gen.setPassword(null);
		}
		genDSDao.updateByPrimaryKeySelective(gen);
	}

	public GenDataSource detail(Integer id) {
		Assert.notNull(id, "id is null");
		return genDSDao.selectByPrimaryKey(id);
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		GenDataSource gen = new GenDataSource();
		gen.setId(id);
		gen.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genDSDao.updateByPrimaryKeySelective(gen);
	}

	public void testConnectDb(GenDataSource genDataSource) throws SQLException {
		Assert2.notNullOf(genDataSource, "genDataSource");
		if(isNull(genDataSource.getHost())){
			genDataSource = genDSDao.selectByPrimaryKey(genDataSource.getId());
		}
		String className = null;
		String url = null;
		//TODO
		switch (genDataSource.getType()){
			case "mysqlv5":
				className = "com.mysql.jdbc.Driver";//com.mysql.cj.jdbc.Driver  for new version
				url = "jdbc:mysql://" + genDataSource.getHost() +":" + genDataSource.getPort() + "/" +genDataSource.getDatabase() + "?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8";
				break;
			case "oracle":
				//TODO
				break;
			case "postgresql":
				//TODO
				break;
			default: throw new UnsupportedOperationException("Unsupported this db type: "+ genDataSource.getType());
		}

		Assert2.notNullOf(className, "className");
		Assert2.notNullOf(url, "url");

		try {
			Class.forName(className);
			DriverManager.getConnection(url,genDataSource.getUsername(),genDataSource.getPassword());
		} catch (ClassNotFoundException | SQLException e) {
			throw new SQLException("connect fail",e);
		}
	}

}