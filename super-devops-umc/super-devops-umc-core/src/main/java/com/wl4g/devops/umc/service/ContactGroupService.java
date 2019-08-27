package com.wl4g.devops.umc.service;

import com.wl4g.devops.common.bean.umc.AlarmContactGroup;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
public interface ContactGroupService {

	void save(AlarmContactGroup alarmContactGroup);

	void del(Integer id);

}
