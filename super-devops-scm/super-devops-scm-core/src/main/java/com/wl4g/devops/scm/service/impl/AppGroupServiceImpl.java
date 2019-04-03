package com.wl4g.devops.scm.service.impl;

import java.util.List;

import com.wl4g.devops.common.bean.scm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wl4g.devops.scm.dao.AppGroupDao;
import com.wl4g.devops.scm.service.AppGroupService;

@Service
@Transactional
public class AppGroupServiceImpl implements AppGroupService {

	@Autowired
	private AppGroupDao appGroupDao;

	@Override
	public void insert(InstanceOfGroup iog) {
		iog.preInsert();
		appGroupDao.insert(iog);
		if (null != iog.getAppInstance() && !iog.getAppInstance().isEmpty()) {
			appGroupDao.insertInstance(iog);
		}
	}

	@Override
	public boolean delete(AppGroup group) {
		group.preUpdate();
		return appGroupDao.delete(group);
	}

	@Override
	public boolean deleteEnv(Environment group) {
		group.preUpdate();
		return appGroupDao.deleteEnv(group);
	}

	@Override
	public boolean update(AppGroup group) {
		group.preUpdate();
		return appGroupDao.update(group);
	}

	@Override
	public InstanceOfGroup select(AppGroup group) {
		return appGroupDao.select(group);
	}

	@Override
	public InstanceOfGroup selectEnv(AppGroup group) {
		return appGroupDao.selectEnv(group);
	}

	@Override
	public List<AppGroupList> list(AppGroupList agl) {
		return appGroupDao.list(agl);
	}

	public List<AppGroupList> groupEnvlist(AppGroupList agl) {
		return appGroupDao.groupEnvlist(agl);
	}

	@Override
	public boolean insertInstance(InstanceOfGroup iog) {
		iog.preInsert();
		return appGroupDao.insertInstance(iog);
	}

	public boolean insertEnvironment(InstanceOfGroup iog) {
		iog.preInsert();
		return appGroupDao.insertEnvironment(iog);
	}

	@Override
	public boolean deleteInstance(AppInstance instance) {
		instance.preUpdate();
		return appGroupDao.deleteInstance(instance);
	}

	@Override
	public boolean updateInstance(AppInstance instance) {
		instance.preUpdate();
		return appGroupDao.updateInstance(instance);
	}

	public boolean updateEnvironment(Environment instance) {
		instance.preUpdate();
		return appGroupDao.updateEnvironment(instance);
	}

	@Override
	public List<AppGroup> grouplist() {
		return appGroupDao.grouplist();
	}

	public List<Environment> environmentlist(String groupId) {
		return appGroupDao.environmentlist(groupId);
	}

	public List<AppInstance> instancelist(AppInstance appInstance) {
		return appGroupDao.instancelist(appInstance);
	}

	@Override
	public String selectEnvName(String envId) {
		return appGroupDao.selectEnvName(envId);
	}

	@Override
	public AppInstance getAppInstance(String id) {
		return appGroupDao.getAppInstance(id);
	}
}
