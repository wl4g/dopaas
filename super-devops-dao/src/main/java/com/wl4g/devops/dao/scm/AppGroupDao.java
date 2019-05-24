/*
 * Copyright 2015 the original author or authors.
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

import com.wl4g.devops.common.bean.scm.*;
import org.apache.ibatis.annotations.Param;

/**
 * 应用组管理DAO接口
 */
public interface AppGroupDao {

	public Long insert(InstanceOfGroup iog);

	public boolean delete(AppGroup group);

	public boolean deleteEnv(Environment group);

	public boolean update(AppGroup group);

	public InstanceOfGroup select(AppGroup group);

	public InstanceOfGroup selectEnv(AppGroup group);

	public List<AppGroupList> list(AppGroupList agl);

	public List<AppGroupList> groupEnvlist(AppGroupList agl);

	public boolean insertInstance(InstanceOfGroup iog);

	public boolean insertEnvironment(InstanceOfGroup iog);

	public boolean deleteInstance(AppInstance instance);

	public boolean updateInstance(AppInstance instance);

	public boolean updateEnvironment(Environment instance);

	public List<AppGroup> grouplist();

	public List<Environment> environmentlist(@Param(value = "groupId") String groupId);

	public List<AppInstance> instancelist(AppInstance appInstance);

	public String selectEnvName(@Param(value = "envId") String envId);

	public AppInstance getAppInstance(@Param(value = "id") String id);

	public AppGroup getAppGroup(@Param(value = "id") String id);

	public AppGroup getAppGroupByName(@Param(value = "name") String name);

}