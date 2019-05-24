package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.ci.TriggerDetail;
import com.wl4g.devops.common.bean.scm.CustomPage;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:04:00
 */
public interface TriggerService {

	int insert(Trigger trigger, Integer[] instanceIds);

	int update(Trigger trigger, Integer[] instanceIds);

	int delete(Integer id);

	void enable(Integer id);

	void disable(Integer id);

	List<Trigger> list(CustomPage customPage);

	List<TriggerDetail> getDetailByTriggerId(Integer triggerId);

}
