package com.wl4g.devops.scm.service;

import java.util.List;

import com.wl4g.devops.common.bean.scm.*;

/**
 * 应用组管理Service接口
 * 
 * @author sut
 * @date 2018年9月20日
 */
public interface AppGroupService {

	public void insert(InstanceOfGroup iog);

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

	public List<Environment> environmentlist(String groupId);

	public List<AppInstance> instancelist(AppInstance appInstance);

	public String selectEnvName(String envId);

	public AppInstance getAppInstance(String id);

}
