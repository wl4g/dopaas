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
package com.wl4g.devops.iam.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.iam.Contact;
import com.wl4g.devops.common.bean.iam.ContactGroupRef;
import com.wl4g.devops.common.bean.iam.ContactChannel;
import com.wl4g.devops.dao.iam.ContactDao;
import com.wl4g.devops.dao.iam.ContactGroupRefDao;
import com.wl4g.devops.dao.iam.ContactChannelDao;
import com.wl4g.devops.iam.service.ContactService;
import com.wl4g.devops.page.PageModel;
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
	private ContactDao contactDao;

	@Autowired
	private ContactGroupRefDao contactGroupRefDao;

	@Autowired
	private ContactChannelDao contactChannelDao;


	@Override
	@Transactional
	public void save(Contact contact) {
		if (null != contact.getId() && contact.getId() > 0) {
			contact.preUpdate();
			contactGroupRefDao.deleteByContactId(contact.getId());
			contactDao.updateByPrimaryKeySelective(contact);
		} else {
			contact.preInsert();
			contact.setDelFlag(DEL_FLAG_NORMAL);
			contact.setEnable(ENABLED);
			contactDao.insertSelective(contact);
		}
		Integer[] groups = contact.getGroups();
		if (null != groups) {
			for (Integer group : groups) {
				ContactGroupRef contactGroupRef = new ContactGroupRef();
				contactGroupRef.preInsert();
				contactGroupRef.setContactGroupId(group);
				contactGroupRef.setContactId(contact.getId());
				contactGroupRefDao.insertSelective(contactGroupRef);
			}
		}

		//TODO add 0313
		contactChannelDao.deleteByContactId(contact.getId());
		List<ContactChannel> contactChannels = contact.getContactChannels();
		for(ContactChannel contactChannel : contactChannels){
			contactChannel.preInsert();
			contactChannel.setContactId(contact.getId());
			contactChannelDao.insertSelective(contactChannel);
		}

	}

	@Override
	public Contact detail(Integer id) {
		Assert.notNull(id, "id can not be null");
		Contact contact = contactDao.selectByPrimaryKey(id);
		List<ContactGroupRef> contactGroupRefs = contactGroupRefDao.selectByContactId(id);
		if (CollectionUtils.isNotEmpty(contactGroupRefs)) {
			Integer[] groups = new Integer[contactGroupRefs.size()];
			for (int i = 0; i < contactGroupRefs.size(); i++) {
				groups[i] = contactGroupRefs.get(i).getContactGroupId();
			}
			contact.setGroups(groups);
		} else {
			contact.setGroups(new Integer[0]);
		}
		return contact;
	}

	@Override
	public void del(Integer id) {
		Assert.notNull(id, "id can not be null");
		Contact contact = new Contact();
		contact.preUpdate();
		contact.setId(id);
		contact.setDelFlag(1);
		contactDao.updateByPrimaryKeySelective(contact);
	}

	@Override
	public PageModel list(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(contactDao.list(name));
		return pm;
	}



}