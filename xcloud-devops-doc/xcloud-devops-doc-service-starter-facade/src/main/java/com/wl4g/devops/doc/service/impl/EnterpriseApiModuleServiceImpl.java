// Generated by XCloud DevOps for Codegen, refer: http://dts.devops.wl4g.com

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

package com.wl4g.devops.doc.service.impl;


import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.doc.EnterpriseApiModule;
import com.wl4g.devops.doc.data.EnterpriseApiModuleDao;
import com.wl4g.devops.doc.service.EnterpriseApiModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

/**
 *  service implements of {@link EnterpriseApiModule}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date 
 * @since v1.0
 */
@Service
public class EnterpriseApiModuleServiceImpl implements EnterpriseApiModuleService {

    @Autowired
    private EnterpriseApiModuleDao enterpriseApiModuleDao;

    @Override
    public PageHolder<EnterpriseApiModule> page(PageHolder<EnterpriseApiModule> pm, EnterpriseApiModule enterpriseApiModule) {
        pm.setCurrentPage();
        pm.setRecords(enterpriseApiModuleDao.list(enterpriseApiModule));
        return pm;
    }

    @Override
    public List<EnterpriseApiModule> getByVersionIdAndParentId(Long versionId, Long parentId) {
        return enterpriseApiModuleDao.getByVersionIdAndParentId(versionId, parentId);
    }

    @Override
    public int save(EnterpriseApiModule enterpriseApiModule) {
        if (isNull(enterpriseApiModule.getId())) {
        	enterpriseApiModule.preInsert();
            return enterpriseApiModuleDao.insertSelective(enterpriseApiModule);
        } else {
        	enterpriseApiModule.preUpdate();
            return enterpriseApiModuleDao.updateByPrimaryKeySelective(enterpriseApiModule);
        }
    }

    @Override
    public EnterpriseApiModule detail(Long id) {
        notNullOf(id, "id");
        return enterpriseApiModuleDao.selectByPrimaryKey(id);
    }

    @Override
    public int del(Long id) {
        notNullOf(id, "id");
        EnterpriseApiModule enterpriseApiModule = new EnterpriseApiModule();
        enterpriseApiModule.setId(id);
        enterpriseApiModule.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        return enterpriseApiModuleDao.updateByPrimaryKeySelective(enterpriseApiModule);
    }

}
