package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-16 14:45:00
 */
public interface CiService {

	List<AppGroup> grouplist();

	List<Environment> environmentlist(String groupId);

	List<AppInstance> instancelist(AppInstance appInstance);

	Trigger getTriggerByProjectAndBranch(Integer projectId,String branchName);


	void hook(String projectName,String branchName,String url);


}
