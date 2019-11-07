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
package com.wl4g.devops.scm.controller;

import com.wl4g.devops.common.utils.PropertySources;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.common.bean.scm.ConfigVersionList;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.scm.VersionContentBean;
import com.wl4g.devops.common.bean.scm.VersionOfDetail;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.scm.service.ConfigurationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 核心配置/版本发布管理Controller
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月2日
 * @since
 */
@RestController
@RequestMapping("/configGuration")
public class ConfigurationController extends BaseController {

	@Autowired
	private ConfigurationService configService;

	/**
	 * 新增版本 -- 同时增加配置
	 * 
	 * @return
	 */
	@RequestMapping(value = "config-set.json", method = { RequestMethod.POST })
	public RespBase<?> configure(@RequestBody VersionOfDetail vod) {
		if (log.isInfoEnabled()) {
			log.info("ConfigSet request ... {}", vod);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			this.configService.configure(vod);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("添加版本失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("ConfigSet response. {}", resp);
		}
		return resp;
	}

	/**
	 * 获取版本列表
	 * 
	 * @param agl
	 * @return
	 */
	@RequestMapping(value = "config-list.json", method = RequestMethod.POST)
	public RespBase<?> list(ConfigVersionList agl, CustomPage customPage) {
		if (log.isInfoEnabled()) {
			log.info("ConfigList request ... {}, {}", agl, customPage);
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
			Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
			Page<ConfigVersionList> page = PageHelper.startPage(pageNum, pageSize, true);
			List<ConfigVersionList> list = configService.list(agl);
			customPage.setPageNum(pageNum);
			customPage.setPageSize(pageSize);
			customPage.setTotal(page.getTotal());
			resp.forMap().put("page", customPage);
			resp.forMap().put("list", list);

		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("获取版本列表失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("ConfigList response. {}", resp);
		}
		return resp;
	}

	/**
	 * 查询版本详情
	 */
	@RequestMapping(value = "config-select.json", method = { RequestMethod.POST, RequestMethod.GET })
	public RespBase<?> selectVersion(int id) {
		if (log.isInfoEnabled()) {
			log.info("ConfigSelect request ... {}", id);
		}
		RespBase<Object> resp = new RespBase<>();
		try {
			List<VersionContentBean> configs = configService.selectVersion(id);
			if (null != configs) {
				resp.forMap().put("configVersions", configs);
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			log.error("查询版本详情失败", e);
		}
		if (log.isInfoEnabled()) {
			log.info("ConfigSelect response. {}", resp);
		}
		return resp;
	}

	/**
	 * 校验配置
	 */
	@RequestMapping(value = "config-check.json", method = { RequestMethod.POST, RequestMethod.GET })
	public RespBase<?> checkDetail(String content) {
		if (log.isInfoEnabled()) {
			log.info("CheckDetail request ... {}", content);
		}
		RespBase<?> resp = new RespBase<>();
		try {
			// Type.of(type);
			PropertySources.resolve(PropertySources.Type.YAML, content);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(ExceptionUtils.getRootCauseMessage(e));
			log.error("Edit file check failure. {}", resp.getMessage());
		}
		if (log.isInfoEnabled()) {
			log.info("CheckContent response. {}", resp);
		}
		return resp;
	}

}