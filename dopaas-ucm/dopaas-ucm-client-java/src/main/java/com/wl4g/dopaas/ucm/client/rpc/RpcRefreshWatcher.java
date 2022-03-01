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
package com.wl4g.dopaas.ucm.client.rpc;

import java.util.Collection;

import com.wl4g.dopaas.common.bean.ucm.model.ReportChangedRequest.ChangedRecord;
import com.wl4g.dopaas.ucm.client.event.ConfigEventListener;
import com.wl4g.dopaas.ucm.client.internal.AbstractRefreshWatcher;
import com.wl4g.dopaas.ucm.client.recorder.ChangeRecorder;
import com.wl4g.infra.common.annotation.Reserved;
import com.wl4g.infra.common.task.RunnerProperties;
import com.wl4g.infra.common.task.RunnerProperties.StartupMode;

/**
 * {@link RpcRefreshWatcher}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-19
 * @since
 */
@Reserved
public class RpcRefreshWatcher extends AbstractRefreshWatcher<RpcUcmClientConfig> {

    public RpcRefreshWatcher(RpcUcmClientConfig config, ChangeRecorder recorder, ConfigEventListener... listeners) {
        super(new RunnerProperties(StartupMode.ASYNC), config, recorder, listeners);
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