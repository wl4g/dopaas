/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.gateway.server.route;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.log.SmartLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Mono;

/**
 * {@link RouteAlterHandler}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-21
 * @since
 */
public class RouteAlterHandler implements ApplicationListener<RefreshRoutesEvent> {

	private final SmartLogger log = SmartLoggerFactory.getLogger(getClass());

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private ApplicationContext applicationContext;

	public Mono<Void> refresh(NotifyType notifyType) {
		if (notifyType != null) {
			this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(notifyType));
		}
		return Mono.empty();
	}

	@Override
	public void onApplicationEvent(RefreshRoutesEvent refreshRoutesEvent) {
		try {
			log.debug(String.format("receive event %s source %s", "refreshRoutesEvent", refreshRoutesEvent.getSource().toString()));
			if (refreshRoutesEvent.getSource() instanceof GatewayControllerEndpoint || NotifyType.permanent.equals(refreshRoutesEvent.getSource())) {
				applicationContext.getBean(IRouteCacheRefresh.class).flushRoutesPermanentToMemery();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}