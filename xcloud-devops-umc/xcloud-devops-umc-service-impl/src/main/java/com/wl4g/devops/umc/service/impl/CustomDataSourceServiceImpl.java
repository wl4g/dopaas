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
package com.wl4g.devops.umc.service.impl;

import static com.wl4g.devops.common.bean.umc.model.DataSourceProvide.MYSQL;
import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.umc.CustomDataSource;
import com.wl4g.devops.common.bean.umc.CustomDataSourceProperties;
import com.wl4g.devops.common.bean.umc.datasource.BaseDataSource;
import com.wl4g.devops.common.bean.umc.datasource.MysqlDataSource;
import com.wl4g.devops.common.bean.umc.model.DataSourceProvide;
import com.wl4g.devops.umc.dao.CustomDataSourcePropertiesDao;
import com.wl4g.devops.umc.dao.CustomDatasourceDao;
import com.wl4g.devops.umc.service.CustomDataSourceService;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class CustomDataSourceServiceImpl implements CustomDataSourceService {

	@Autowired
	private CustomDatasourceDao customDatasourceDao;

	@Autowired
	private CustomDataSourcePropertiesDao customDataSourcePropertiesDao;

	@Override
	public PageHolder<CustomDataSource> list(PageHolder<CustomDataSource> pm, String name) {
		pm.startPage();
		pm.setRecords(customDatasourceDao.list(name));
		return pm;
	}

	@Override
	public BaseDataSource detal(Long id) {
		CustomDataSource customDataSource = customDatasourceDao.selectByPrimaryKey(id);
		BaseDataSource baseDataSource = properties2Model(customDataSource);
		if (baseDataSource instanceof MysqlDataSource) {
			MysqlDataSource mysqlDataSource = (MysqlDataSource) baseDataSource;
			mysqlDataSource.setPassword("******");
		}
		return baseDataSource;
	}

	@Override
	public void save(BaseDataSource baseDataSource) {

		if (baseDataSource instanceof MysqlDataSource) {
			MysqlDataSource mysqlDataSource = (MysqlDataSource) baseDataSource;
			if (StringUtils.equalsAnyIgnoreCase(mysqlDataSource.getPassword(), "******")
					|| StringUtils.isBlank(mysqlDataSource.getPassword())) {
				mysqlDataSource.setPassword(null);
			}
		}
		CustomDataSource customDataSource = model2Properties(baseDataSource);
		if (customDataSource.getId() != null) {
			customDataSourcePropertiesDao.deleteByDataSourceId(customDataSource.getId());
			customDataSourcePropertiesDao.insertBatch(customDataSource.getCustomDataSourceProperties());
			customDataSource.preUpdate();
			customDatasourceDao.updateByPrimaryKeySelective(customDataSource);
		} else {
			customDataSource.preInsert();
			customDataSource.setStatus(1);
			List<CustomDataSourceProperties> customDataSourceProperties = customDataSource.getCustomDataSourceProperties();
			for (CustomDataSourceProperties properties : customDataSourceProperties) {
				properties.setDataSourceId(customDataSource.getId());
			}
			customDatasourceDao.insertSelective(customDataSource);
			customDataSourcePropertiesDao.insertBatch(customDataSource.getCustomDataSourceProperties());
		}
	}

	@Override
	public void del(Long id) {
		CustomDataSource customDatasource = new CustomDataSource();
		customDatasource.setId(id);
		customDatasource.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		customDatasource.preUpdate();
		customDatasourceDao.updateByPrimaryKeySelective(customDatasource);
	}

	public void testConnect(DataSourceProvide dataSourceProvide, String url, String username, String password, Long id)
			throws Exception {
		String mysqlClassName = "com.mysql.jdbc.Driver";
		String oracleClassName = "oracle.jdbc.driver.OracleDriver";
		Connection conn = null;
		/*
		 * if(StringUtils.isBlank(password) ||
		 * StringUtils.equalsAnyIgnoreCase("******",password)){ CustomDataSource
		 * customDataSource = customDatasourceDao.selectByPrimaryKey(id);
		 * if(Objects.nonNull(customDataSource)){ password =
		 * customDataSource.getPassword(); } }
		 */
		try {
			String className = null;
			if (MYSQL.equals(dataSourceProvide)) {
				className = mysqlClassName;
			} else {
				className = oracleClassName;
			}
			Class.forName(className);
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e1) {
			throw new ClassNotFoundException("system unSupport this database provider");
		} catch (SQLException e2) {
			throw new SQLException("Connect Fail, Please check your username and password");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<CustomDataSource> dataSources() {
		return customDatasourceDao.list(null);
	}

	public CustomDataSource model2Properties(BaseDataSource baseDataSource) {
		CustomDataSource customDataSource = new CustomDataSource();
		customDataSource.setId(baseDataSource.getId());
		customDataSource.setName(baseDataSource.getName());
		customDataSource.setProvider(baseDataSource.getProvider());
		customDataSource.setStatus(baseDataSource.getStatus());

		try {
			List<CustomDataSourceProperties> customDataSourceProperties = objectToCustomDataSourceProperties(baseDataSource,
					baseDataSource.getId());
			customDataSource.setCustomDataSourceProperties(customDataSourceProperties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		/*
		 * List<CustomDataSourceProperties> customDataSourceProperties = new
		 * ArrayList<>();
		 * if(MYSQL.toString().equalsIgnoreCase(baseDataSource.getProvider()) &&
		 * baseDataSource instanceof MysqlDataSource){ MysqlDataSource
		 * mysqlDataSource = (MysqlDataSource)baseDataSource; //url
		 * CustomDataSourceProperties url = new CustomDataSourceProperties();
		 * url.setDataSourceId(customDataSource.getId()); url.setKey(URL);
		 * url.setValue(mysqlDataSource.getUrl());
		 * customDataSourceProperties.add(url); //username
		 * CustomDataSourceProperties username = new
		 * CustomDataSourceProperties();
		 * username.setDataSourceId(customDataSource.getId());
		 * username.setKey(USERNAME);
		 * username.setValue(mysqlDataSource.getUsername());
		 * customDataSourceProperties.add(username); //password
		 * CustomDataSourceProperties password = new
		 * CustomDataSourceProperties();
		 * password.setDataSourceId(customDataSource.getId());
		 * password.setKey(PASSWORD);
		 * password.setValue(mysqlDataSource.getPassword());
		 * customDataSourceProperties.add(password); }
		 * customDataSource.setCustomDataSourceProperties(
		 * customDataSourceProperties);
		 */
		return customDataSource;
	}

	public List<CustomDataSourceProperties> objectToCustomDataSourceProperties(Object obj, Long dataSourceId)
			throws IllegalAccessException {
		String[] ignores = new String[] { "id", "name", "provider", "status", "delFlag", "createBy", "createDate", "updateBy",
				"updateDate", "remark", };
		List<CustomDataSourceProperties> customDataSourceProperties = new ArrayList<>();
		Class<?> clazz = obj.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			CustomDataSourceProperties customDataSourcePropertie = new CustomDataSourceProperties();
			customDataSourcePropertie.setDataSourceId(dataSourceId);
			field.setAccessible(true);
			String fieldName = field.getName();
			if (asList(ignores).contains(fieldName)) {
				continue;
			}
			String value = String.valueOf(field.get(obj));
			customDataSourcePropertie.setKey(fieldName);
			customDataSourcePropertie.setValue(value);
			customDataSourcePropertie.preInsert();
			customDataSourceProperties.add(customDataSourcePropertie);
		}
		return customDataSourceProperties;
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseDataSource> T properties2Model(CustomDataSource customDataSource) {
		String[] ignores = new String[] { "id", "name", "provider", "status", "delFlag", "createBy", "createDate", "updateBy",
				"updateDate", "remark", };
		if (MYSQL.toString().equalsIgnoreCase(customDataSource.getProvider())) {
			MysqlDataSource mysqlDataSource = new MysqlDataSource();
			mysqlDataSource.setId(customDataSource.getId());
			mysqlDataSource.setName(customDataSource.getName());
			mysqlDataSource.setProvider(customDataSource.getProvider());
			mysqlDataSource.setStatus(customDataSource.getStatus());

			List<CustomDataSourceProperties> customDataSourceProperties = customDataSource.getCustomDataSourceProperties();
			Map<String, String> map = new HashMap<>();
			for (CustomDataSourceProperties properties : customDataSourceProperties) {
				map.put(properties.getKey(), properties.getValue());
			}
			MysqlDataSource dataSourceProperties = JacksonUtils.parseJSON(JacksonUtils.toJSONString(map), MysqlDataSource.class);
			BeanUtils.copyProperties(dataSourceProperties, mysqlDataSource, ignores);

			/*
			 * for(CustomDataSourceProperties properties :
			 * customDataSourceProperties){
			 * if(URL.equalsIgnoreCase(properties.getKey())){
			 * mysqlDataSource.setUrl(properties.getValue()); }
			 * if(USERNAME.equalsIgnoreCase(properties.getKey())){
			 * mysqlDataSource.setUsername(properties.getValue()); }
			 * if(PASSWORD.equalsIgnoreCase(properties.getKey())){
			 * mysqlDataSource.setPassword(properties.getValue()); } }
			 */
			return (T) mysqlDataSource;
		}
		return null;
	}

}