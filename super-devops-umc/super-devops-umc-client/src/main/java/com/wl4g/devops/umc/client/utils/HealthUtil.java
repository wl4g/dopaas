package com.wl4g.devops.umc.client.utils;

import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

/**
 * Health tools
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年6月3日
 * @since
 */
public class HealthUtil {

	public static Builder up(Builder builder, String desc) {
		return build(builder, Status.UP.getCode(), desc);
	}

	public static Builder down(Builder builder, String desc) {
		return build(builder, Status.DOWN.getCode(), desc);
	}

	public static Builder build(Builder builder, String statusCode, String desc) {
		return builder.status(new Status(statusCode, desc));
	}

}
