/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.erm.service.impl;


import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.erm.IdcBean;
import com.wl4g.devops.erm.data.IdcDao;
import com.wl4g.devops.erm.service.IdcService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class IdcServiceImpl implements IdcService {

	@Autowired
	private IdcDao idcDao;

	@Override
	public PageHolder<IdcBean> page(PageHolder<IdcBean> pm, String name) {
		pm.startPage();
		pm.setRecords(idcDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public List<IdcBean> getForSelect() {
		return idcDao.list(getRequestOrganizationCodes(), null);
	}

	public void save(IdcBean idc) {
		if (isNull(idc.getId())) {
			idc.preInsert(getRequestOrganizationCode());
			insert(idc);
		} else {
			idc.preUpdate();
			update(idc);
		}
	}

	private void insert(IdcBean idc) {
		idcDao.insertSelective(idc);
	}

	private void update(IdcBean idc) {
		idcDao.updateByPrimaryKeySelective(idc);
	}

	public IdcBean detail(Long id) {
		Assert.notNull(id, "id is null");
		return idcDao.selectByPrimaryKey(id);
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		IdcBean idc = new IdcBean();
		idc.setId(id);
		idc.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		idcDao.updateByPrimaryKeySelective(idc);
	}

}