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
package com.wl4g.dopaas.umc.service.impl;


import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.umc.CustomHistory;
import com.wl4g.dopaas.umc.data.CustomHistoryDao;
import com.wl4g.dopaas.umc.service.CustomHistoryService;
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
	public PageHolder<CustomHistory> list(PageHolder<CustomHistory> pm, String name) {
		pm.bindPage();
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
	public CustomHistory detail(Long id) {
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
	public void del(Long id) {
		CustomHistory customHistory = new CustomHistory();
		customHistory.setId(id);
		customHistory.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		customHistory.preUpdate();
		customHistoryDao.updateByPrimaryKeySelective(customHistory);
	}

}