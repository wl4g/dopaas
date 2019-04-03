package com.wl4g.devops.umc.handle;

import java.util.List;

/**
 * SMS handler interface.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月28日
 * @since
 */
public interface SmsNotificationHandle {

	boolean send(List<String> numbers, String appInfo, String fromStatus, String toStatus, String content);

}
