package com.wl4g.devops.umc.service.impl;

import com.wl4g.devops.common.bean.umc.AlarmCollector;
import com.wl4g.devops.dao.umc.AlarmCollectorDao;
import com.wl4g.devops.umc.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;
import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;

/**
 * @author vjay
 * @date 2019-08-06 14:15:00
 */
@Service
public class CollectorServiceImpl implements CollectorService {

	@Autowired
	private AlarmCollectorDao alarmCollectorDao;

	@Override
	public void save(AlarmCollector alarmCollector) {
		Assert.notNull(alarmCollector, "alarmCollector is null");
		if (null == alarmCollector.getId()) {// insert
			alarmCollector.preInsert();
			alarmCollector.setDelFlag(DEL_FLAG_NORMAL);
			alarmCollector.setEnable(ENABLED);
			alarmCollectorDao.insertSelective(alarmCollector);
		} else {// update
			alarmCollector.preUpdate();
			alarmCollectorDao.updateByPrimaryKeySelective(alarmCollector);
		}
	}

	@Override
	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		AlarmCollector alarmCollector = new AlarmCollector();
		alarmCollector.setId(id);
		alarmCollector.setDelFlag(DEL_FLAG_DELETE);
		alarmCollector.preUpdate();
		alarmCollectorDao.updateByPrimaryKeySelective(alarmCollector);
	}
}
