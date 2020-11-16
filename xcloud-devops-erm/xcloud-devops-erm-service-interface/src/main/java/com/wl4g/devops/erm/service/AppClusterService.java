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
package com.wl4g.devops.erm.service;

import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppEnvironment;
import com.wl4g.devops.common.bean.erm.AppInstance;

import java.util.List;
import java.util.Map;

/**
 * Application cluster information service of {@link AppClusterService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-11-13
 * @sine v1.0
 * @see
 */
public interface AppClusterService {

	void save(AppCluster appCluster);

	Map<String, Object> list(PageModel<?> pm, String clusterName, Integer deployType);

	List<AppCluster> clusters();

	void del(Long clusterId);

	AppCluster detail(Long clusterId);

	List<AppInstance> getInstancesByClusterIdAndEnvType(Long clusterId, String envType);

	AppEnvironment getAppClusterEnvironment(Long clusterId, String envType);

}