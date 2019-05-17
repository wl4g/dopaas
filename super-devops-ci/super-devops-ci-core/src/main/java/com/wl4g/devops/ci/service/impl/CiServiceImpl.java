package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.dao.scm.AppGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-16 14:50:00
 */
@Service
public class CiServiceImpl implements CiService {

	@Autowired
	private AppGroupDao appGroupDao;

	@Override public List<AppGroup> grouplist() {
		return appGroupDao.grouplist();
	}

	@Override public List<Environment> environmentlist(String groupId) {
		return appGroupDao.environmentlist(groupId);
	}

	@Override public List<AppInstance> instancelist(AppInstance appInstance) {
		return appGroupDao.instancelist(appInstance);
	}

}
