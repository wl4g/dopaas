/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.dao.scm;

import java.util.List;

import com.wl4g.devops.common.bean.share.*;
import org.apache.ibatis.annotations.Param;

/**
 * 应用组管理DAO接口
 */
public interface AppClusterDao {

	public Long insert(InstanceOfGroup iog);

	public boolean delete(AppCluster group);

	public boolean deleteEnv(Environment group);

	public boolean update(AppCluster group);

	public InstanceOfGroup select(AppCluster group);

	public InstanceOfGroup selectEnv(AppCluster group);

	public List<AppGroupList> list(AppGroupList agl);

	public List<AppGroupList> groupEnvlist(AppGroupList agl);

	public boolean insertInstance(InstanceOfGroup iog);

	public boolean insertEnvironment(InstanceOfGroup iog);

	public boolean deleteInstance(AppInstance instance);

	public boolean updateInstance(AppInstance instance);

	public boolean updateEnvironment(Environment instance);

	public List<AppCluster> grouplist();

	public List<Environment> environmentlist(@Param(value = "appClusterId") String appClusterId);

	public List<AppInstance> instancelist(AppInstance appInstance);

	public String selectEnvName(@Param(value = "envId") String envId);

	public AppInstance getAppInstance(@Param(value = "id") String id);

	public AppCluster getAppGroup(@Param(value = "id") Integer id);

	public AppCluster getAppGroupByName(@Param(value = "name") String name);

}