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
package com.wl4g.dopaas.lcdp.dbop.service.impl;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static java.util.Objects.isNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.infra.core.bean.BaseBean;
import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.lcdp.dbop.DatabaseOperation;
import com.wl4g.dopaas.lcdp.dbop.data.DatabaseOperationDao;
import com.wl4g.dopaas.lcdp.dds.service.DatabaseOperationService;

/**
 * {@link DatabaseOperationServiceImpl}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2019-11-14
 * @sine v1.0
 * @see
 */
@Service
public class DatabaseOperationServiceImpl implements DatabaseOperationService {
    protected final SmartLogger log = getLogger(getClass());

    @Autowired
    private DatabaseOperationDao operationDao;

    @Override
    public PageHolder<DatabaseOperation> page(PageHolder<DatabaseOperation> pm, String name) {
        pm.useCount().bind();
        pm.setRecords(operationDao.list(name));
        return pm;
    }

    public void save(DatabaseOperation datasource) {
        if (isNull(datasource.getId())) {
            datasource.preInsert(getRequestOrganizationCode());
            insert(datasource);
        } else {
            datasource.preUpdate();
            update(datasource);
        }
    }

    private void insert(DatabaseOperation gen) {
        operationDao.insertSelective(gen);
    }

    private void update(DatabaseOperation gen) {
        operationDao.updateByPrimaryKeySelective(gen);
    }

    public DatabaseOperation detail(Long id) {
        notNullOf(id, "id");
        return operationDao.selectByPrimaryKey(id);
    }

    public void del(Long id) {
        Assert.notNull(id, "id is null");
        DatabaseOperation dbop = new DatabaseOperation();
        dbop.setId(id);
        dbop.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        operationDao.updateByPrimaryKeySelective(dbop);
    }

}