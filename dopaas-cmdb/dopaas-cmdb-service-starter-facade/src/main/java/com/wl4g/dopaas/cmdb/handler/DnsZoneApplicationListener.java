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
package com.wl4g.dopaas.cmdb.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.wl4g.dopaas.cmdb.service.DnsPrivateBlacklistService;
import com.wl4g.dopaas.cmdb.service.DnsPrivateZoneService;

/**
 * {@link DnsZoneApplicationListener}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2019-04-01 17:51:00
 * @since
 */
@Service
public class DnsZoneApplicationListener implements ApplicationRunner {

	private @Autowired DnsPrivateZoneService privateZoneService;

	private @Autowired DnsPrivateBlacklistService privateBWlistService;

	@Override
	public void run(ApplicationArguments var) throws Exception {
		try {
			privateZoneService.loadDnsAtStart();
			privateBWlistService.loadBlacklistAtStart();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}