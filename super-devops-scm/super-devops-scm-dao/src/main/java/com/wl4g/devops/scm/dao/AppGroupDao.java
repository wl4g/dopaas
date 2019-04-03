package com.wl4g.devops.scm.dao;

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

}
