package com.wl4g.devops.umc.service;

import com.wl4g.devops.common.bean.umc.AlarmCollector;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
public interface CollectorService {

	void save(AlarmCollector alarmCollector);

	void del(Integer id);

}
