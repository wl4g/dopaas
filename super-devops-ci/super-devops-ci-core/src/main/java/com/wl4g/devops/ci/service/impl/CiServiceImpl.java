package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.devtool.CiConstant;
import com.wl4g.devops.ci.devtool.DevConfig;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.subject.BaseSubject;
import com.wl4g.devops.ci.subject.JarSubject;
import com.wl4g.devops.ci.subject.TarSubject;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.ci.TriggerDetail;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import com.wl4g.devops.dao.ci.TriggerDetailDao;
import com.wl4g.devops.dao.scm.AppGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-05-16 14:50:00
 */
@Service
public class CiServiceImpl implements CiService {

	@Autowired
	private DevConfig devConfig;

	@Autowired
	private AppGroupDao appGroupDao;

	@Autowired
	private TriggerDao triggerDao;

	@Autowired
	private TriggerDetailDao triggerDetailDao;

	@Autowired
	private ProjectDao projectDao;



	@Override public List<AppGroup> grouplist() {
		return appGroupDao.grouplist();
	}

	@Override public List<Environment> environmentlist(String groupId) {
		return appGroupDao.environmentlist(groupId);
	}

	@Override public List<AppInstance> instancelist(AppInstance appInstance) {
		return appGroupDao.instancelist(appInstance);
	}

	@Override
	public Trigger getTriggerByProjectAndBranch(Integer projectId, String branchName) {
		Map<String,Object> map = new HashMap<>();
		map.put("projectId",projectId);
		map.put("branchName",branchName);
		Trigger trigger = triggerDao.getTriggerByProjectAndBranch(map);
		if(null==trigger){
			return null;
		}
		List<TriggerDetail> triggerDetails = triggerDetailDao.getDetailByTriggerId(trigger.getId());
		if(null==triggerDetails){
			return null;
		}
		trigger.setTriggerDetails(triggerDetails);
		return trigger;
	}

	public void hook(String projectName,String branchName,String url){
		Project project = projectDao.getByProjectName(projectName);
		AppGroup appGroup = appGroupDao.getAppGroup(project.getAppGroupId().toString());
		String alias = appGroup.getName();
		Trigger trigger = getTriggerByProjectAndBranch(project.getId(),branchName);

		List<AppInstance> instances = new ArrayList<>();
		for(TriggerDetail triggerDetail : trigger.getTriggerDetails()){
			AppInstance instance = appGroupDao.getAppInstance(triggerDetail.getInstanceId().toString());
			instances.add(instance);
		}

		BaseSubject subject = getSubject(trigger.getTarType(),devConfig.getGitBasePath()+"/"+projectName, url, branchName, alias,project.getTarPath(),instances);

		try {
			subject.excu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BaseSubject getSubject(int tarType,String path, String url, String branch, String alias,String tarPath,List<AppInstance> instances){
		switch(tarType){
			case CiConstant.TAR_TYPE_TAR :
				return new TarSubject(path, url, branch, alias,tarPath,instances);
			case CiConstant.TAR_TYPE_JAR :
				return new JarSubject(path, url, branch, alias,tarPath,instances);
			case CiConstant.TAR_TYPE_OTHER :
				//return new OtherSubject();
			default :
				throw new RuntimeException("unsuppost type:"+tarType);
		}
	}



}
