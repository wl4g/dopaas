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
package com.wl4g.devops.support.notification;

import com.wl4g.devops.tool.common.log.SmartLogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Validator;

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;

import java.lang.reflect.Method;

/**
 * {@link AbstractMessageNotifier}
 *
 * @param <C>
 * @param <T>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public abstract class AbstractMessageNotifier<C extends NotifyProperties> implements MessageNotifier, InitializingBean {
	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Notify properties.
	 */
	final protected C config;

	@Autowired
	protected Validator validator;

	public AbstractMessageNotifier(C config) {
		notNullOf(config, "config");
		this.config = config;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public boolean preHandle(Method method, Object[] args) {
		if (!isNull(args)) {
			for (Object arg : args) {
				validator.validate(arg);
			}
		}

		// Check notify message templateKey
		if (config instanceof AbstractNotifyProperties) {
			AbstractNotifyProperties conf = (AbstractNotifyProperties) config;
			for (Object arg : args) {
				if (arg instanceof GenericNotifyMessage) {
					GenericNotifyMessage msg = (GenericNotifyMessage) arg;
					// No such templateKey?
					if (!conf.hasTemplateKey(msg.getTemplateKey())) {
						log.warn("No such notification template key of: {}", msg.getTemplateKey());
						return false;
					}
					break;
				}
			}
		}

		return true;
	}

}
