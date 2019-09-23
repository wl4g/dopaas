package com.wl4g.devops.umc.service;

import com.wl4g.devops.common.bean.umc.AlarmTemplate;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
public interface TemplateService {

	void save(AlarmTemplate alarmTemplate);

	AlarmTemplate detail(Integer id);

	void del(Integer id);

}
