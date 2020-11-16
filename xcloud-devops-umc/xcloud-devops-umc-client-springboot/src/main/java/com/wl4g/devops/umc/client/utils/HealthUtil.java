/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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