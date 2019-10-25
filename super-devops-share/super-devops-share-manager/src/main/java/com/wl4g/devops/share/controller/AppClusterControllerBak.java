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
 *//*

package com.wl4g.devops.share.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.share.service.AppClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * 应用组管理
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月2日
 * @since
 *//*

@RestController
@RequestMapping("/appGroup")
public class AppClusterControllerBak extends BaseController {

	@Autowired
	private AppClusterService appClusterService;

	*/
/**
	 * 添加应用环境组
	 * 
	 * @param
	 * @return
	 *//*

	@RequestMapping(value = "/envconfig_save", method = RequestMethod.POST)
	public RespBase<?> envconfigsave(@RequestBody InstanceOfGroup iog) {
		RespBase<?> resp = new RespBase<>();
		try {
			List<Environment> appInstance = iog.getEnvironment();
			List<Environment> insertappInstance = new ArrayList<>();
			List<Environment> updateInstance = new ArrayList<>();
			appInstance.stream().forEach(u -> {
				if (u.getId() == null) {
					insertappInstance.add(u);
				} else {
					if (iog.getId() != null) {
						u.setAppClusterId(String.valueOf(iog.getId()));
					}
					u.setCreateBy(iog.getCreateBy());
					u.setUpdateBy(iog.getUpdateBy());
					updateInstance.add(u);
				}
			});
			if (!insertappInstance.isEmpty()) {
				iog.setEnvironment(insertappInstance);
				if (iog.getId() != null) {
					appClusterService.update(iog);
				} else {
					appClusterService.insert(iog);
				}
				appClusterService.insertEnvironment(iog);
			}
			if (!updateInstance.isEmpty()) {
				appClusterService.update(iog);
				updateInstance.stream().forEach(u -> updateEnvironment(u));
			}

		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("添加应用组失败{0}", e);
		}
		if (log.isInfoEnabled()) {
			log.info("EnvconfigSave response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 添加实例组
	 * 
	 * @param
	 * @return
	 *//*

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public RespBase<?> insert(@RequestBody InstanceOfGroup iog) {
		if (log.isInfoEnabled()) {
			log.info("InstanceOfGroupInsert request ... {}", iog);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			List<AppInstance> appInstance = iog.getAppInstance();
			List<AppInstance> insertappInstance = new ArrayList<>();
			List<AppInstance> updateInstance = new ArrayList<>();
			appInstance.stream().forEach(u -> {
				if (u.getId() == null) {
					insertappInstance.add(u);
				} else {
					u.setAppClusterId(Long.valueOf(iog.getId()));
					u.setEnvId(iog.getEnvId());
					u.setCreateBy(iog.getCreateBy());
					u.setUpdateBy(iog.getUpdateBy());
					updateInstance.add(u);
				}
			});
			if (!insertappInstance.isEmpty()) {
				iog.setAppInstance(insertappInstance);
				appClusterService.insertInstance(iog);
			}
			if (!updateInstance.isEmpty()) {
				updateInstance.stream().forEach(u -> updateInstance(u));
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("添加应用组失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("InstanceOfGroupInsert response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 删除应用组
	 * 
	 * @param ap
	 * @return
	 *//*

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public RespBase<?> delete(AppCluster ap) {
		if (log.isInfoEnabled()) {
			log.info("AppGropDelete request ... {}", ap);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			if (null == ap.getId()) {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，id为空！");
				return resp;
			}
			boolean flag = appClusterService.delete(ap);
			if (flag) {
			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("删除应用组失败{0}", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppGropDelete response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 删除环境
	 * 
	 * @param ap
	 * @return
	 *//*

	*/
/*@RequestMapping(value = "/delete_env", method = RequestMethod.POST)
	public RespBase<?> deleteEnv(Environment ap) {
		if (log.isInfoEnabled()) {
			log.info("EnvironmentDelete request ... {}", ap);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			if (null == ap.getId()) {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，id为空！");
				return resp;
			}
			boolean flag = appClusterService.deleteEnv(ap);
			if (flag) {

			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("删除应用组失败{0}", e);
		}
		if (log.isInfoEnabled()) {
			log.info("EnvironmentDelete response. {}", resp);
		}
		return resp;
	}*//*


	*/
/**
	 * 修改应用组
	 * 
	 * @param ap
	 * @return
	 *//*

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public RespBase<?> update(AppCluster ap) {
		if (log.isInfoEnabled()) {
			log.info("AppGroupUpdate request ... {}", ap);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			boolean flag = appClusterService.update(ap);
			if (flag) {
			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("修改应用组失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppGroupUpdate response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 查询应用组
	 * 
	 * @param id
	 * @return
	 *//*

	@RequestMapping(value = "/select", method = { RequestMethod.POST, RequestMethod.GET })
	public RespBase<?> select(AppCluster ap) {
		if (log.isInfoEnabled()) {
			log.info("AppGroupSelect request ... {}", ap);
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			InstanceOfGroup iof = appClusterService.select(ap);
			if (null != iof) {
				resp.getData().put("iof", iof);
			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("查询应用组失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppGroupSelect response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 查询环境下的应用组
	 * 
	 * @param id
	 * @return
	 *//*

	*/
/*@RequestMapping(value = "/env_list", method = { RequestMethod.POST, RequestMethod.GET })
	public RespBase<?> selectEnv(AppCluster ap) {
		if (log.isInfoEnabled()) {
			log.info("AppGroupEnvList request ... {}", ap);
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			InstanceOfGroup iof = appClusterService.selectEnv(ap);
			if (null != iof) {
				resp.getData().put("iof", iof);
			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("查询应用组失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppGroupEnvList response. {}", resp);
		}
		return resp;
	}*//*


	*/
/**
	 * 获取应用组实例管理列表
	 * 
	 * @param agl
	 * @return
	 *//*

	@RequestMapping(value = "/list", method = { RequestMethod.POST, RequestMethod.GET })
	public RespBase<?> list(AppGroupList agl, CustomPage customPage) {
		if (log.isInfoEnabled()) {
			log.info("AppGroupList request ... {}, {}", agl, customPage);
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
			Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
			Page<AppGroupList> page = PageHelper.startPage(pageNum, pageSize, true);
			List<AppGroupList> list = appClusterService.list(agl);
			customPage.setPageNum(pageNum);
			customPage.setPageSize(pageSize);
			customPage.setTotal(page.getTotal());
			resp.getData().put("page", customPage);
			resp.getData().put("list", list);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("获取应用组列表失败{0}", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppGroupList response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 获取应用组管理列表
	 * 
	 * @param agl
	 * @return
	 *//*

	@RequestMapping(value = "/group_envlist", method = { RequestMethod.POST, RequestMethod.GET })
	public RespBase<?> groupEnvlist(AppGroupList agl, CustomPage customPage) {
		if (log.isInfoEnabled()) {
			log.info("GroupEnvlist request ... {}, {}", agl, customPage);
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
			Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
			Page<AppGroupList> page = PageHelper.startPage(pageNum, pageSize, true);
			List<AppGroupList> list = appClusterService.groupEnvlist(agl);
			customPage.setPageNum(pageNum);
			customPage.setPageSize(pageSize);
			customPage.setTotal(page.getTotal());
			resp.getData().put("page", customPage);
			resp.getData().put("list", list);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("获取应用组列表失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("GroupEnvlist response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 添加组实例
	 * 
	 * @param id
	 * @return
	 *//*

	@RequestMapping(value = "/insertInstance", method = RequestMethod.POST)
	public RespBase<?> insertInstance(@RequestBody InstanceOfGroup iog) {
		if (log.isInfoEnabled()) {
			log.info("InstanceInsert request ... {}", iog);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			boolean flag = appClusterService.insertInstance(iog);
			if (flag) {
			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("添加组实例失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("InstanceInsert response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 删除组实例
	 * 
	 * @param ap
	 * @return
	 *//*

	@RequestMapping(value = "/deleteInstance", method = RequestMethod.POST)
	public RespBase<?> deleteInstance(AppInstance instance) {
		if (log.isInfoEnabled()) {
			log.info("AppInstanceDelete request ... {}", instance);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			boolean flag = appClusterService.deleteInstance(instance);
			if (flag) {

			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("删除组实例失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppInstanceDelete response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 修改组实例
	 * 
	 * @param ap
	 * @return
	 *//*

	@RequestMapping(value = "/updateInstance", method = RequestMethod.POST)
	public RespBase<?> updateInstance(AppInstance instance) {
		if (log.isInfoEnabled()) {
			log.info("AppInstanceUpdate request ... {}", instance);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			boolean flag = appClusterService.updateInstance(instance);
			if (flag) {

			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("修改组实例失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppInstanceUpdate response. {}", resp);
		}
		return resp;
	}

	public RespBase<?> updateEnvironment(Environment instance) {
		if (log.isInfoEnabled()) {
			log.info("EnvironmentInsert request ... {}", instance);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			boolean flag = appClusterService.updateEnvironment(instance);
			if (flag) {

			} else {
				resp.setCode(RetCode.SYS_ERR);
				log.error("请求失败，请确认请求参数！");
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("修改组实例失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("EnvironmentInsert response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 查询分组List
	 * 
	 * @param
	 * @return
	 *//*

	@RequestMapping(value = "/group_list", method = RequestMethod.POST)
	public RespBase<?> grouplist() {
		if (log.isInfoEnabled()) {
			log.info("GroupList request ...");
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			List<AppCluster> grouplist = appClusterService.grouplist();
			resp.getData().put("grouplist", grouplist);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("查询分组列表失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("GroupList response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 查询环境List
	 * 
	 * @param
	 * @return
	 *//*

	@RequestMapping(value = "/envir_list")
	public RespBase<?> environmentlist(String clusterId) {
		if (log.isInfoEnabled()) {
			log.info("EnvironmentList request ... {}", clusterId);
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			List<Environment> envlist = appClusterService.environmentlist(clusterId);
			resp.getData().put("envlist", envlist);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("查询分组列表失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("EnvironmentList response. {}", resp);
		}
		return resp;
	}

	*/
/**
	 * 查询实例List
	 * 
	 * @param
	 * @return
	 *//*

	@RequestMapping(value = "/instance_list", method = RequestMethod.POST)
	public RespBase<?> instancelist(@RequestBody AppInstance appInstance) {
		if (log.isInfoEnabled()) {
			log.info("AppInstanceList request ...");
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			List<AppInstance> instancelist = appClusterService.instancelist(appInstance);
			resp.getData().put("instancelist", instancelist);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("查询分组列表失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("AppInstanceList response. {}", resp);
		}
		return resp;
	}

}*/
