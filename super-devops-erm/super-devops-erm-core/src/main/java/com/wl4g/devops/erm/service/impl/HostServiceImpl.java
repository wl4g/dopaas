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
import com.wl4g.devops.common.bean.erm.Host;
import com.wl4g.devops.common.bean.erm.HostSsh;
import com.wl4g.devops.dao.erm.HostDao;
import com.wl4g.devops.dao.erm.HostSshDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.erm.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class HostServiceImpl implements HostService {

    @Autowired
    private HostDao appHostDao;

    @Autowired
    private HostSshDao hostSshDao;

    @Override
    public List<Host> list(String name, String hostname, Integer idcId) {
        List<Host> list = appHostDao.list(getRequestOrganizationCodes(), name, hostname, idcId);
        return list;
    }

    @Override
    public PageModel page(PageModel pm,String name, String hostname, Integer idcId) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(appHostDao.list(getRequestOrganizationCodes(), name, hostname, idcId));
        return pm;
    }

    @Override
    public void save(Host host){
        if(isNull(host.getId())){
            host.preInsert(getRequestOrganizationCode());
            insert(host);
        }else{
            host.preUpdate();
            update(host);
        }
    }

    private void insert(Host host){
        appHostDao.insertSelective(host);
        List<Integer> sshIds = host.getSshIds();
        if(!CollectionUtils.isEmpty(sshIds)){
            List<HostSsh> hostSshs = new ArrayList<>();
            for(Integer sshId : sshIds){
                HostSsh hostSsh = new HostSsh();
                hostSsh.preInsert();
                hostSsh.setHostId(host.getId());
                hostSsh.setSshId(sshId);
                hostSshs.add(hostSsh);
            }
            hostSshDao.insertBatch(hostSshs);
        }
    }

    private void update(Host host){
        appHostDao.updateByPrimaryKeySelective(host);
        hostSshDao.deleteByHostId(host.getId());
        List<Integer> sshIds = host.getSshIds();
        if(!CollectionUtils.isEmpty(sshIds)){
            List<HostSsh> hostSshs = new ArrayList<>();
            for(Integer sshId : sshIds){
                HostSsh hostSsh = new HostSsh();
                hostSsh.preInsert();
                hostSsh.setHostId(host.getId());
                hostSsh.setSshId(sshId);
                hostSshs.add(hostSsh);
            }
            hostSshDao.insertBatch(hostSshs);
        }
    }


    public Host detail(Integer id){
        Assert.notNull(id,"id is null");
        Host host = appHostDao.selectByPrimaryKey(id);
        List<Integer> integers = hostSshDao.selectByHostId(id);
        host.setSshIds(integers);
        return host;
    }

    public void del(Integer id){
        Assert.notNull(id,"id is null");
        Host host = new Host();
        host.setId(id);
        host.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        appHostDao.updateByPrimaryKeySelective(host);
    }



}