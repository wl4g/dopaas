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
import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;
import com.wl4g.devops.common.bean.erm.DnsPrivateResolution;
import com.wl4g.devops.dao.erm.DnsPrivateDomainDao;
import com.wl4g.devops.dao.erm.DnsPrivateResolutionDao;
import com.wl4g.devops.erm.dns.DnsServerInterface;
import com.wl4g.devops.erm.service.DnsPrivateDomainService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DnsPrivateDomainServiceImpl implements DnsPrivateDomainService {

//    final private static String RUNNING = "RUNNING";
//    final private static String DISABLED = "DISABLED";
//    final private static String EXPIRED = "EXPIRED";

    @Autowired
    private DnsPrivateDomainDao dnsPrivateDomainDao;

    @Autowired
    private DnsPrivateResolutionDao dnsPrivateResolutionDao;

    @Autowired
    private DnsServerInterface dnsServerInterface;

    @Override
    public PageModel page(PageModel pm,String zone) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        List<DnsPrivateDomain> list = dnsPrivateDomainDao.list(getRequestOrganizationCodes(), zone);
        pm.setRecords(list);
        return pm;
    }

    public void save(DnsPrivateDomain dnsPrivateDomain){
        if(isNull(dnsPrivateDomain.getId())){
            dnsPrivateDomain.preInsert(getRequestOrganizationCode());
            dnsPrivateDomain.setStatus("RUNNING");
            insert(dnsPrivateDomain);
        }else{
            dnsPrivateDomain.preUpdate();
            update(dnsPrivateDomain);
        }
    }

    private void insert(DnsPrivateDomain dnsPrivateDomain){
        dnsPrivateDomainDao.insertSelective(dnsPrivateDomain);
    }

    private void update(DnsPrivateDomain dnsPrivateDomain){
        dnsPrivateDomainDao.updateByPrimaryKeySelective(dnsPrivateDomain);
    }

    public DnsPrivateDomain detail(Integer id){
        Assert.notNull(id,"id is null");
        return dnsPrivateDomainDao.selectByPrimaryKey(id);
    }

    public void del(Integer id){
        Assert.notNull(id,"id is null");
        DnsPrivateDomain dnsPrivateDomain = dnsPrivateDomainDao.selectByPrimaryKey(id);
        dnsPrivateDomain.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        dnsPrivateDomainDao.updateByPrimaryKeySelective(dnsPrivateDomain);
        dnsServerInterface.delDomain(dnsPrivateDomain.getZone());
    }

    @Override
    public void loadDnsAtStart() {
        List<DnsPrivateDomain> list = dnsPrivateDomainDao.list(null, null);
        for(DnsPrivateDomain dnsPrivateDomain : list){
            List<DnsPrivateResolution> dnsPrivateResolutions = dnsPrivateResolutionDao.selectByDomainId(dnsPrivateDomain.getId());
            dnsPrivateDomain.setDnsPrivateResolutions(dnsPrivateResolutions);
            dnsServerInterface.putDomian(dnsPrivateDomain);
        }
    }


}