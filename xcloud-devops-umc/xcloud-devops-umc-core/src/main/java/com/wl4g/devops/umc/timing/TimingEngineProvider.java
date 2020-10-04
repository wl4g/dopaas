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
package com.wl4g.devops.umc.timing;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.components.core.bean.umc.CustomEngine;
import com.wl4g.devops.dao.umc.CustomEngineDao;

import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.umc.timing.EngineTaskScheduler.RUNNING;
import static com.wl4g.devops.umc.timing.EngineTaskScheduler.WAIT;

/**
 * Timing scheduling composite pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-08-19 10:41:00
 */
public class TimingEngineProvider implements Runnable {

	final protected SmartLogger log = SmartLoggerFactory.getLogger(getClass());

	private CustomEngine customEngine;

	@Autowired
	private CustomEngineDao customEngineDao;

	@Autowired
	private CodeExecutor codeExecutor;

	public TimingEngineProvider(CustomEngine customEngine) {
		this.customEngine = customEngine;
	}

	@Override
	public void run() {
		log.info("Timing customEngine... customEngineId:{}", customEngine.getId());
		try {
			updateStatus(customEngine.getId(), RUNNING);
			// TODO execute code
			codeExecutor.executeCode(customEngine);

		} catch (Exception e) {
			log.error("", e);
		} finally {
			updateStatus(customEngine.getId(), WAIT);
		}
	}

	private void updateStatus(Long id, int status) {
		CustomEngine customEngine = new CustomEngine();
		customEngine.setId(id);
		customEngine.setStatus(status);
		customEngineDao.updateByPrimaryKeySelective(customEngine);
	}

}