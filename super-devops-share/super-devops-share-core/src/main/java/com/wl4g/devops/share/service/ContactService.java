package com.wl4g.devops.share.service;

import com.wl4g.devops.common.bean.umc.AlarmContact;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
public interface ContactService {

	void save(AlarmContact alarmContact);

	AlarmContact detail(Integer id);

	void del(Integer id);

}
