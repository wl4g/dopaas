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
package com.wl4g.devops.share.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.share.AppHost;
import com.wl4g.devops.dao.share.AppHostDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.share.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class HostServiceImpl implements HostService {

    @Autowired
    private AppHostDao appHostDao;

    @Override
    public List<AppHost> list(String name, String hostname, Integer idcId) {
        List<AppHost> list = appHostDao.list(name, hostname, idcId);
        return list;
    }

    @Override
    public PageModel page(PageModel pm,String name, String hostname, Integer idcId) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(appHostDao.list(name, hostname, idcId));
        return pm;
    }

    public void save(AppHost host){
        if(isNull(host.getId())){
            insert(host);
        }else{
            update(host);
        }
    }

    private void insert(AppHost host){

    }

    private void update(AppHost host){

    }



}