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
package com.wl4g.devops.erm.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.erm.Idc;
import com.wl4g.devops.dao.erm.IdcDao;
import com.wl4g.devops.erm.service.IdcService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.iam.common.utils.IamOrganizationUtils.getCurrentOrganizationCode;
import static com.wl4g.devops.iam.common.utils.IamOrganizationUtils.getCurrentOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class IdcServiceImpl implements IdcService {

    @Autowired
    private IdcDao idcDao;

    @Override
    public PageModel page(PageModel pm,String name) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(idcDao.list(getCurrentOrganizationCodes(), name));
        return pm;
    }

    @Override
    public List<Idc> getForSelect() {
        return idcDao.list(getCurrentOrganizationCodes(),null);
    }

    public void save(Idc idc){
        if(isNull(idc.getId())){
            idc.preInsert(getCurrentOrganizationCode());
            insert(idc);
        }else{
            idc.preUpdate();
            update(idc);
        }
    }

    private void insert(Idc idc){
        idcDao.insertSelective(idc);
    }

    private void update(Idc idc){
        idcDao.updateByPrimaryKeySelective(idc);
    }


    public Idc detail(Integer id){
        Assert.notNull(id,"id is null");
        return idcDao.selectByPrimaryKey(id);
    }

    public void del(Integer id){
        Assert.notNull(id,"id is null");
        Idc idc = new Idc();
        idc.setId(id);
        idc.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        idcDao.updateByPrimaryKeySelective(idc);
    }



}