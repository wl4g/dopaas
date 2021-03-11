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
package com.wl4g.devops.udm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.udm.Label;
import com.wl4g.devops.udm.data.LabelDao;
import com.wl4g.devops.udm.service.LabelService;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class LabelServcieImpl implements LabelService {

	@Autowired
	private LabelDao labelDao;

	@Override
	public PageHolder<Label> list(PageHolder<Label> pm, String name) {
		pm.count().startPage();
		pm.setRecords(labelDao.list(name));
		return pm;
	}

	@Override
	public void save(Label label) {
		if (label.getId() == null) {
			label.preInsert();
			insert(label);
		} else {
			label.preUpdate();
			update(label);
		}
	}

	private void insert(Label label) {
		labelDao.insertSelective(label);
	}

	private void update(Label label) {
		labelDao.updateByPrimaryKeySelective(label);
	}

	@Override
	public void del(Long id) {
		Label label = new Label();
		label.setId(id);
		label.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		labelDao.updateByPrimaryKeySelective(label);
	}

	@Override
	public Label detail(Long id) {
		return labelDao.selectByPrimaryKey(id);
	}

	@Override
	public List<Label> allLabel() {
		return labelDao.selectAll();
	}

}