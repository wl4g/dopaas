package com.wl4g.devops.umc.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.model.StatusMessage;
import com.wl4g.devops.umc.notify.AbstractAdvancedNotifier;

/**
 * Dashboard Service
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
@Service
public class DashboardHandle {
	final protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JedisService jedisService;

	public StatusMessage findStatusInfo(String msgId) {
		String msg = this.jedisService.get(AbstractAdvancedNotifier.INFO_PREFIX + msgId);
		StatusMessage info = JacksonUtils.parseJSON(msg, StatusMessage.class);
		if (info == null) {
			throw new IllegalArgumentException("Getting the `" + msgId + "` corresponding state message is null.");
		}
		return info;
	}

}
