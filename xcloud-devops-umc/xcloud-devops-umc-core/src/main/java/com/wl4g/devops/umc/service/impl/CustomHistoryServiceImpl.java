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
package com.wl4g.devops.umc.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.umc.CustomHistory;
import com.wl4g.devops.dao.umc.CustomHistoryDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.umc.service.CustomHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class CustomHistoryServiceImpl implements CustomHistoryService {

	@Autowired
	private CustomHistoryDao customHistoryDao;

	@Override
	public PageModel list(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		List<CustomHistory> list = customHistoryDao.list(name);
		/*
		 * for(CustomHistory customHistory : list){
		 * if(Objects.nonNull(customHistory.getEndTime())&&
		 * Objects.nonNull(customHistory.getStartTime())){
		 * customHistory.setCostTime(customHistory.getEndTime().getTime()-
		 * customHistory.getStartTime().getTime()); } }
		 */
		pm.setRecords(list);
		return pm;
	}

	@Override
	public CustomHistory detal(Integer id) {
		CustomHistory customHistory = customHistoryDao.selectByPrimaryKey(id);
		return customHistory;
	}

	@Override
	public void save(CustomHistory customHistory) {

		if (customHistory.getId() != null) {
			customHistory.preUpdate();
			customHistoryDao.updateByPrimaryKeySelective(customHistory);
		} else {
			customHistory.preInsert();
			customHistoryDao.insertSelective(customHistory);
		}
	}

	@Override
	public void del(Integer id) {
		CustomHistory customHistory = new CustomHistory();
		customHistory.setId(id);
		customHistory.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		customHistory.preUpdate();
		customHistoryDao.updateByPrimaryKeySelective(customHistory);
	}

}