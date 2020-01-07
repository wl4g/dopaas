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
package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.VcsService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.dao.ci.VcsDao;
import com.wl4g.devops.page.PageModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class VcsServcieImpl implements VcsService {

	@Autowired
	private VcsDao vcsDao;

	@Override
	public PageModel list(PageModel pm, String name, String providerKind, Integer authType) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(vcsDao.list(name, providerKind, authType));
		return pm;
	}

	@Override
	public void save(Vcs vcs) {
		if (vcs.getId() == null) {
			vcs.preInsert();
			insert(vcs);
		} else {
			vcs.preUpdate();
			update(vcs);
		}
	}

	private void insert(Vcs vcs) {
		vcsDao.insertSelective(vcs);
	}

	private void update(Vcs vcs) {
		vcsDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public void del(Integer id) {
		Vcs vcs = new Vcs();
		vcs.setId(id);
		vcs.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		vcsDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public Vcs detail(Integer id) {
		return vcsDao.selectByPrimaryKey(id);
	}

	@Override
	public List<Vcs> all() {
		return vcsDao.list(null, null, null);
	}

}