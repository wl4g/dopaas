package com.wl4g.devops.umc.service;

import com.wl4g.devops.common.bean.umc.AlarmConfig;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
public interface ConfigService {

	void save(AlarmConfig alarmConfig);

	void del(Integer id);

	AlarmConfig detail(Integer id);

}
