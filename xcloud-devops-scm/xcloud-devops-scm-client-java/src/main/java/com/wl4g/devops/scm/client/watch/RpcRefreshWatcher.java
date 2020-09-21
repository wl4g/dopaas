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
package com.wl4g.devops.scm.client.watch;

import java.util.Collection;

import com.wl4g.components.common.annotation.Reserved;
import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.repository.RefreshRecordsRepository;
import com.wl4g.devops.scm.common.model.ReportChangedRequest.ChangedRecord;

/**
 * {@link RpcRefreshWatcher}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-19
 * @since
 */
@Reserved
public class RpcRefreshWatcher extends GenericRefreshWatcher {

	public RpcRefreshWatcher(ScmClientProperties<?> config, RefreshRecordsRepository repository,
			ConfigEventListener... listeners) {
		super(new RunnerProperties(true), config, repository, listeners);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean doReporting(Collection<ChangedRecord> records) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException();
	}

}
