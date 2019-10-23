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
package com.wl4g.devops.share.service.impl;

import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.dao.share.AppClusterDao;
import com.wl4g.devops.dao.share.AppInstanceDao;
import com.wl4g.devops.share.service.AppClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;

@Service
@Transactional
public class AppClueterServiceImpl implements AppClusterService {

	@Autowired
	private AppClusterDao appClusterDao;

	@Autowired
	private AppInstanceDao appInstanceDao;


	@Override
	public void save(AppCluster appCluster) {
		if(appCluster.getId()==null){
			insert(appCluster);
		}else{
			update(appCluster);
		}
	}

	private void insert(AppCluster appCluster){
		appClusterDao.insertSelective(appCluster);
		Integer clusterId = appCluster.getId();
		List<AppInstance> instances = appCluster.getInstances();
		for(AppInstance appInstance : instances){
			appInstance.setClusterId(clusterId);
			appInstanceDao.insertSelective(appInstance);
		}
	}

	private void update(AppCluster appCluster){
		appClusterDao.updateByPrimaryKeySelective(appCluster);
		List<AppInstance> appInstances = appInstanceDao.selectByClusterId(appCluster.getId());
		List<AppInstance> noDelInstances = new ArrayList<>();
		for(AppInstance appInstance : appCluster.getInstances()){
			if(appInstance.getId()==null){//insert
				appInstance.setClusterId(appCluster.getId());
				appInstanceDao.insertSelective(appInstance);
			}else{//update
				appInstanceDao.updateByPrimaryKeySelective(appInstance);
			}
			for(AppInstance instance : appInstances){// if new data not include old data , remove
				if(instance.getId().intValue()==appInstance.getId().intValue()){
					noDelInstances.add(instance);
					break;
				}
			}
		}
		appInstances.removeAll(noDelInstances);
		for(AppInstance appInstance : appInstances){
			appInstanceDao.deleteByPrimaryKey(appInstance.getId());
		}
	}


	public void del(Integer clusterId){
		AppCluster appCluster = new AppCluster();
		appCluster.setId(clusterId);
		appCluster.setDelFlag(DEL_FLAG_DELETE);
		appClusterDao.updateByPrimaryKeySelective(appCluster);
	}






}