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
package com.wl4g.devops.umc.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.umc.CustomDataSource;
import com.wl4g.devops.common.bean.umc.model.DataSourceProvide;
import com.wl4g.devops.dao.umc.CustomDatasourceDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.umc.service.CustomDataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.common.bean.umc.model.DataSourceProvide.MYSQL;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class CustomDataSourceServiceImpl implements CustomDataSourceService {

    @Autowired
    private CustomDatasourceDao customDatasourceDao;

    @Override
    public PageModel list(PageModel pm, String name) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(customDatasourceDao.list(name));
        return pm;
    }

    @Override
    public CustomDataSource detal(Integer id) {
        CustomDataSource customDataSource = customDatasourceDao.selectByPrimaryKey(id);
        customDataSource.setPassword("******");
        return customDataSource;
    }

    @Override
    public void save(CustomDataSource customDataSource) {
        if (StringUtils.equalsAnyIgnoreCase(customDataSource.getPassword(), "******") || StringUtils.isBlank(customDataSource.getPassword())) {
            customDataSource.setPassword(null);
        }
        if (customDataSource.getId() != null) {
            customDataSource.preUpdate();
            customDatasourceDao.updateByPrimaryKeySelective(customDataSource);
        } else {
            customDataSource.preInsert();
            customDataSource.setStatus(1);
            customDatasourceDao.insertSelective(customDataSource);
        }
    }

    @Override
    public void del(Integer id) {
        CustomDataSource customDatasource = new CustomDataSource();
        customDatasource.setId(id);
        customDatasource.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        customDatasource.preUpdate();
        customDatasourceDao.updateByPrimaryKeySelective(customDatasource);
    }


    public void testConnect(DataSourceProvide dataSourceProvide,String url,String username,String password,Integer id) throws Exception{
        String mysqlClassName = "com.mysql.jdbc.Driver";
        String oracleClassName = "oracle.jdbc.driver.OracleDriver";
        Connection conn = null;
        if(StringUtils.isBlank(password) || StringUtils.equalsAnyIgnoreCase("******",password)){
            CustomDataSource customDataSource = customDatasourceDao.selectByPrimaryKey(id);
            if(Objects.nonNull(customDataSource)){
                password = customDataSource.getPassword();
            }
        }
        try {
            String className = null;
            if(MYSQL.equals(dataSourceProvide)){
                className = mysqlClassName;
            }else{
                className = oracleClassName;
            }
            Class.forName(className);
            conn = DriverManager.getConnection(url,username,password);
        } catch (ClassNotFoundException e1) {
            throw new ClassNotFoundException("system unSupport this database provider");
        }catch ( SQLException e2) {
            throw new SQLException("Connect Fail, Please check your username and password");
        }finally {
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


}