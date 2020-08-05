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
package com.wl4g.devops.erm.service;

import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.devops.page.PageModel;

import java.util.List;

/**
 * 应用组管理Service接口
 *
 * @author sut
 * @date 2018年9月20日
 */
public interface AppInstanceService {

    void save(AppInstance appInstance);

    PageModel list(PageModel pm, String name, Integer clusterId, String envType, Integer serverType);

    void del(Integer clusterId);

    AppInstance detail(Integer clusterId);

    List<AppInstance> getInstancesByClusterIdAndEnvType(Integer clusterId, String envType);

    void testSSHConnect(Integer hostId, String sshUser, String sshKey) throws Exception, InterruptedException;

}