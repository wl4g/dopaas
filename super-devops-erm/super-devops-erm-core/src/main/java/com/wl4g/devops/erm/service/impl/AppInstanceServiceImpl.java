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
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.bean.erm.Host;
import com.wl4g.devops.dao.erm.AppInstanceDao;
import com.wl4g.devops.dao.erm.HostDao;
import com.wl4g.devops.erm.service.AppInstanceService;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.cli.command.RemoteDestroableCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getCurrentOrganizationCode;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getCurrentOrganizationCodes;

@Service
@Transactional
public class AppInstanceServiceImpl implements AppInstanceService {

    @Autowired
    private AppInstanceDao appInstanceDao;

    @Autowired
    private HostDao appHostDao;

    @Autowired
    private DestroableProcessManager pm;

    @Override
    public PageModel list(PageModel pm, String name, Integer instanceId, String envType, Integer deployType) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(appInstanceDao.list(getCurrentOrganizationCodes(), name, instanceId,envType,deployType));
        return pm;
    }

    @Override
    public void save(AppInstance appInstance) {
        if (appInstance.getId() == null) {
            insert(appInstance);
        } else {
            update(appInstance);
        }
    }

    private void insert(AppInstance appInstance) {
        appInstance.preInsert(getCurrentOrganizationCode());
        appInstanceDao.insertSelective(appInstance);
    }

    private void update(AppInstance appInstance) {
        appInstance.preUpdate();
        appInstanceDao.updateByPrimaryKeySelective(appInstance);
    }

    /*private void checkRepeat(List<AppInstance> instances) {
        Assert2.notEmptyOf(instances, "instances");
        for (int i = 0; i < instances.size(); i++) {
            for (int j = i + 1; j < instances.size() - 1; j++) {
                isRepeatBetweenTwo(instances.get(i), instances.get(j));
            }
        }
    }

    private void isRepeatBetweenTwo(AppInstance instance1, AppInstance instance2) {
        if (Objects.isNull(instance1) || Objects.isNull(instance2)) {
            return;
        }
        if (!StringUtils.equals(instance1.getEndpoint(), instance2.getEndpoint())) {
            return;
        }
        if (!StringUtils.equals(instance1.getEnvType(), instance2.getEnvType())) {
            return;
        }
        if (instance1.getHostId().intValue() != instance2.getHostId().intValue()) {
            return;
        }
        throw new InvalidParameterException(
                String.format("Instances is repeat;instance1=%s instance2=%s", instance1.toString(), instance2.toString()));
    }*/


    @Override
    public void del(Integer instanceId) {
        AppInstance appInstance = new AppInstance();
        appInstance.setId(instanceId);
        appInstance.setDelFlag(DEL_FLAG_DELETE);
        appInstanceDao.updateByPrimaryKeySelective(appInstance);
    }

    @Override
    public AppInstance detail(Integer instanceId) {
        Assert.notNull(instanceId, "instanceId is null");
        AppInstance appInstance = appInstanceDao.selectByPrimaryKey(instanceId);
        return appInstance;
    }

    @Override
    public List<AppInstance> getInstancesByClusterIdAndEnvType(Integer clusterId, String envType) {
        Assert.notNull(clusterId, "clusterId is null");
        Assert.notNull(envType, "envType is null");
        return appInstanceDao.selectByClusterIdAndEnvType(clusterId, envType);
    }

    @Override
    public void testSSHConnect(Integer hostId, String sshUser, String sshKey) throws Exception {
        Host appHost = appHostDao.selectByPrimaryKey(hostId);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String command = "echo " + uuid;
        String echoStr;
        try {
            echoStr = pm.execWaitForComplete(
                    new RemoteDestroableCommand(command, 10000, sshUser, appHost.getHostname(), sshKey.toCharArray()));
        } catch (UnknownHostException e) {
            throw new UnknownHostException(appHost.getHostname() + ": Name or service not known");
        }
        if (Objects.isNull(echoStr) || !uuid.equals(echoStr.replaceAll("\n", ""))) {
            throw new IOException("Test Connect Fail");
        }
    }
}