// Generated by DoPaaS for Codegen, refer: http://dts.devops.wl4g.com

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

package com.wl4g.dopaas.udm.service.impl;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.udm.EnterpriseApiProperties;
import com.wl4g.dopaas.udm.data.EnterpriseApiPropertiesDao;
import com.wl4g.dopaas.udm.service.EnterpriseApiPropertiesService;
import com.wl4g.dopaas.udm.service.model.EnterpriseApiPropertiesPageRequest;

/**
 * service implements of {@link EnterpriseApiProperties}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date
 * @since v1.0
 */
@Service
public class EnterpriseApiPropertiesServiceImpl implements EnterpriseApiPropertiesService {

	private @Autowired EnterpriseApiPropertiesDao enterpriseApiPropertiesDao;

	@Override
	public PageHolder<EnterpriseApiProperties> page(EnterpriseApiPropertiesPageRequest enterpriseApiPropertiesPageRequest) {
		PageHolder<EnterpriseApiProperties> pm = enterpriseApiPropertiesPageRequest.getPm();
		pm.useCount().bind();
		EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
		BeanUtils.copyProperties(enterpriseApiPropertiesPageRequest, enterpriseApiProperties);
		pm.setRecords(enterpriseApiPropertiesDao.list(enterpriseApiProperties));
		return pm;
	}

	@Override
	public int save(EnterpriseApiProperties enterpriseApiProperties) {
		if (isNull(enterpriseApiProperties.getId())) {
			enterpriseApiProperties.preInsert();
			return enterpriseApiPropertiesDao.insertSelective(enterpriseApiProperties);
		} else {
			enterpriseApiProperties.preUpdate();
			return enterpriseApiPropertiesDao.updateByPrimaryKeySelective(enterpriseApiProperties);
		}
	}

	@Override
	public EnterpriseApiProperties detail(Long id) {
		notNullOf(id, "id");
		return enterpriseApiPropertiesDao.selectByPrimaryKey(id);
	}

	@Override
	public int del(Long id) {
		notNullOf(id, "id");
		EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
		enterpriseApiProperties.setId(id);
		enterpriseApiProperties.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		return enterpriseApiPropertiesDao.updateByPrimaryKeySelective(enterpriseApiProperties);
	}

}
