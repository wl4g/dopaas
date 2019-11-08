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
package com.wl4g.devops.share.service.impl;

import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.bean.umc.AlarmContactGroupRef;
import com.wl4g.devops.dao.umc.AlarmContactDao;
import com.wl4g.devops.dao.umc.AlarmContactGroupRefDao;
import com.wl4g.devops.share.service.ContactService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;

/**
 * @author vjay
 * @date 2019-08-05 16:02:00
 */
@Service
public class ContactServiceImpl implements ContactService {

	@Autowired
	private AlarmContactDao alarmContactDao;

	@Autowired
	private AlarmContactGroupRefDao alarmContactGroupRefDao;

	@Override
	@Transactional
	public void save(AlarmContact alarmContact) {
		if (null != alarmContact.getId() && alarmContact.getId() > 0) {
			alarmContact.preUpdate();
			alarmContactGroupRefDao.deleteByContactId(alarmContact.getId());
			alarmContactDao.updateByPrimaryKeySelective(alarmContact);
		} else {
			alarmContact.preInsert();
			alarmContact.setDelFlag(DEL_FLAG_NORMAL);
			alarmContact.setEnable(ENABLED);
			alarmContactDao.insertSelective(alarmContact);
		}
		Integer[] groups = alarmContact.getGroups();
		if (null != groups) {
			for (Integer group : groups) {
				AlarmContactGroupRef alarmContactGroupRef = new AlarmContactGroupRef();
				alarmContactGroupRef.setContactGroupId(group);
				alarmContactGroupRef.setContactId(alarmContact.getId());
				alarmContactGroupRefDao.insertSelective(alarmContactGroupRef);
			}
		}

	}

	@Override
	public AlarmContact detail(Integer id) {
		Assert.notNull(id, "id can not be null");
		AlarmContact alarmContact = alarmContactDao.selectByPrimaryKey(id);
		List<AlarmContactGroupRef> alarmContactGroupRefs = alarmContactGroupRefDao.selectByContactId(id);
		if (CollectionUtils.isNotEmpty(alarmContactGroupRefs)) {
			Integer[] groups = new Integer[alarmContactGroupRefs.size()];
			for (int i = 0; i < alarmContactGroupRefs.size(); i++) {
				groups[i] = alarmContactGroupRefs.get(i).getContactGroupId();
			}
			alarmContact.setGroups(groups);
		} else {
			alarmContact.setGroups(new Integer[0]);
		}
		return alarmContact;
	}

	@Override
	public void del(Integer id) {
		Assert.notNull(id, "id can not be null");
		AlarmContact alarmContact = new AlarmContact();
		alarmContact.preUpdate();
		alarmContact.setId(id);
		alarmContact.setDelFlag(1);
		alarmContactDao.updateByPrimaryKeySelective(alarmContact);
	}

}