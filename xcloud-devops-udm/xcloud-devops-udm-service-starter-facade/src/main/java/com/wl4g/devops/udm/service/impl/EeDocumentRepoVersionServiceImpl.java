// Generated by XCloud PaaS for Codegen, refer: http://dts.devops.wl4g.com

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

package com.wl4g.devops.udm.service.impl;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.context.RpcContextHolder;
import com.wl4g.devops.common.bean.udm.EeDocumentRepoVersion;
import com.wl4g.devops.udm.data.EeDocumentRepoVersionDao;
import com.wl4g.devops.udm.service.EeDocumentRepoVersionService;

/**
 * service implements of {@link EeDocumentRepoVersion}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date
 * @since v1.0
 */
@Service
public class EeDocumentRepoVersionServiceImpl implements EeDocumentRepoVersionService {

	@Autowired
	private EeDocumentRepoVersionDao eeDocumentRepoVersionDao;

	@Override
	public PageHolder<EeDocumentRepoVersion> page(EeDocumentRepoVersion eeDocumentRepoVersion) {
		PageHolder pm = RpcContextHolder.get().get("pm", PageHolder.class);
		pm.count().startPage();
		pm.setRecords(eeDocumentRepoVersionDao.list(eeDocumentRepoVersion));
		return pm;
	}

	@Override
	public int save(EeDocumentRepoVersion eeDocumentRepoVersion) {
		if (isNull(eeDocumentRepoVersion.getId())) {
			eeDocumentRepoVersion.preInsert();
			return eeDocumentRepoVersionDao.insertSelective(eeDocumentRepoVersion);
		} else {
			eeDocumentRepoVersion.preUpdate();
			return eeDocumentRepoVersionDao.updateByPrimaryKeySelective(eeDocumentRepoVersion);
		}
	}

	@Override
	public EeDocumentRepoVersion detail(Long id) {
		notNullOf(id, "id");
		return eeDocumentRepoVersionDao.selectByPrimaryKey(id);
	}

	@Override
	public int del(Long id) {
		notNullOf(id, "id");
		EeDocumentRepoVersion eeDocumentRepoVersion = new EeDocumentRepoVersion();
		eeDocumentRepoVersion.setId(id);
		eeDocumentRepoVersion.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		return eeDocumentRepoVersionDao.updateByPrimaryKeySelective(eeDocumentRepoVersion);
	}

}
