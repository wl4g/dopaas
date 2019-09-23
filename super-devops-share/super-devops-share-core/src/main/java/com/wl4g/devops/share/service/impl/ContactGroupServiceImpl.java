package com.wl4g.devops.share.service.impl;

import com.wl4g.devops.common.bean.umc.AlarmContactGroup;
import com.wl4g.devops.dao.umc.AlarmContactGroupDao;
import com.wl4g.devops.share.service.ContactGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;

/**
 * @author vjay
 * @date 2019-08-05 18:16:00
 */
@Service
public class ContactGroupServiceImpl implements ContactGroupService {

	@Autowired
	private AlarmContactGroupDao alarmContactGroupDao;

	@Override
	public void save(AlarmContactGroup alarmContactGroup) {
		Assert.notNull(alarmContactGroup, "alarmContactGroup is null");
		if (alarmContactGroup.getId() != null) {
			alarmContactGroup.preUpdate();
			alarmContactGroupDao.updateByPrimaryKeySelective(alarmContactGroup);
		} else {
			alarmContactGroup.preInsert();
			alarmContactGroup.setDelFlag(DEL_FLAG_NORMAL);
			alarmContactGroup.setEnable(ENABLED);
			alarmContactGroupDao.insertSelective(alarmContactGroup);
		}
	}

	@Override
	public void del(Integer id) {
		AlarmContactGroup alarmContactGroup = new AlarmContactGroup();
		alarmContactGroup.preUpdate();
		alarmContactGroup.setId(id);
		alarmContactGroup.setDelFlag(1);
		alarmContactGroupDao.updateByPrimaryKeySelective(alarmContactGroup);
	}
}
