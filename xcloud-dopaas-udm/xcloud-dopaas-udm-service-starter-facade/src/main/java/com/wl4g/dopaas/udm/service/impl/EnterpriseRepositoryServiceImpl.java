// Generated by XCloud DoPaaS for Codegen, refer: http://dts.devops.wl4g.com

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
import com.wl4g.dopaas.common.bean.udm.EnterpriseRepository;
import com.wl4g.dopaas.udm.data.EnterpriseRepositoryDao;
import com.wl4g.dopaas.udm.service.EnterpriseRepositoryService;
import com.wl4g.dopaas.udm.service.model.EnterpriseRepositoryPageRequest;

/**
 * service implements of {@link EnterpriseRepository}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date
 * @since v1.0
 */
@Service
public class EnterpriseRepositoryServiceImpl implements EnterpriseRepositoryService {

	private @Autowired EnterpriseRepositoryDao enterpriseRepositoryDao;

	@Override
	public PageHolder<EnterpriseRepository> page(EnterpriseRepositoryPageRequest enterpriseRepositoryPageRequest) {
		PageHolder<EnterpriseRepository> pm = enterpriseRepositoryPageRequest.getPm();
		pm.useCount().bind();
		EnterpriseRepository enterpriseRepository = new EnterpriseRepository();
		BeanUtils.copyProperties(enterpriseRepositoryPageRequest, enterpriseRepository);
		pm.setRecords(enterpriseRepositoryDao.list(enterpriseRepository));
		return pm;
	}

	@Override
	public int save(EnterpriseRepository enterpriseRepository) {
		if (isNull(enterpriseRepository.getId())) {
			enterpriseRepository.preInsert();
			return enterpriseRepositoryDao.insertSelective(enterpriseRepository);
		} else {
			enterpriseRepository.preUpdate();
			return enterpriseRepositoryDao.updateByPrimaryKeySelective(enterpriseRepository);
		}
	}

	@Override
	public EnterpriseRepository detail(Long id) {
		notNullOf(id, "id");
		return enterpriseRepositoryDao.selectByPrimaryKey(id);
	}

	@Override
	public int del(Long id) {
		notNullOf(id, "id");
		EnterpriseRepository enterpriseRepository = new EnterpriseRepository();
		enterpriseRepository.setId(id);
		enterpriseRepository.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		return enterpriseRepositoryDao.updateByPrimaryKeySelective(enterpriseRepository);
	}

}
