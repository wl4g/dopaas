package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.ci.TriggerDetail;
import com.wl4g.devops.common.bean.scm.BaseBean;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.dao.ci.TriggerDao;
import com.wl4g.devops.dao.ci.TriggerDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:07:00
 */
@Service
public class TriggerServiceImpl implements TriggerService {

	@Autowired
	private TriggerDao triggerDao;
	@Autowired
	private TriggerDetailDao triggerDetailDao;

	@Override
	@Transactional
	public int insert(Trigger trigger, Integer[] instanceIds) {
		trigger.preInsert();
		int result = triggerDao.insert(trigger);
		int triggerId = trigger.getId();
		if (null == instanceIds) {
			throw new RuntimeException("instance can not be null");
		}

		for (Integer instanceId : instanceIds) {
			TriggerDetail triggerDetail = new TriggerDetail();
			triggerDetail.setInstanceId(instanceId);
			triggerDetail.preInsert();
			triggerDetail.setTriggerId(triggerId);
			triggerDetailDao.insert(triggerDetail);
		}

		return result;
	}

	@Override
	@Transactional
	public int update(Trigger trigger, Integer[] instanceIds) {
		trigger.preUpdate();
		int result = triggerDao.updateByPrimaryKeySelective(trigger);
		int triggerId = trigger.getId();
		if (null == instanceIds) {
			return result;
		}

		triggerDetailDao.deleteByTriggerId(triggerId);
		for (Integer instanceId : instanceIds) {
			TriggerDetail triggerDetail = new TriggerDetail();
			triggerDetail.setInstanceId(instanceId);
			triggerDetail.preInsert();
			triggerDetail.setTriggerId(triggerId);
			triggerDetailDao.insert(triggerDetail);
		}
		return result;
	}

	@Override
	@Transactional
	public int delete(Integer id) {
		triggerDetailDao.deleteByTriggerId(id);
		return triggerDao.deleteByPrimaryKey(id);
	}

	@Override
	public void enable(Integer id) {
		Trigger trigger = new Trigger();
		trigger.setId(id);
		trigger.preUpdate();
		trigger.setEnable(BaseBean.ENABLED);
		triggerDao.updateByPrimaryKeySelective(trigger);
	}

	@Override
	public void disable(Integer id) {
		Trigger trigger = new Trigger();
		trigger.setId(id);
		trigger.preUpdate();
		trigger.setEnable(BaseBean.DISABLED);
		triggerDao.updateByPrimaryKeySelective(trigger);
	}

	@Override
	public List<Trigger> list(CustomPage customPage) {
		return triggerDao.list(customPage);
	}

	@Override
	public List<TriggerDetail> getDetailByTriggerId(Integer triggerId) {
		return triggerDetailDao.getDetailByTriggerId(triggerId);
	}

}
