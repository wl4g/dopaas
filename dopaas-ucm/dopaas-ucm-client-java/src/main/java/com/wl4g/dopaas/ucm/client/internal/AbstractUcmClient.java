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
package com.wl4g.dopaas.ucm.client.internal;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;

import java.io.IOException;

import com.wl4g.infra.common.log.SmartLogger;

import lombok.Getter;

/**
 * {@link AbstractUcmClient}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-10
 * @since
 */
@Getter
public abstract class AbstractUcmClient implements UcmClient {

    protected final SmartLogger log = getLogger(getClass());

    /** {@link AbstractRefreshWatcher} */
    protected final RefreshWatcher watcher;

    public AbstractUcmClient(RefreshWatcher watcher) {
        notNullOf(watcher, "watcher");
        this.watcher = watcher;
    }

    @Override
    public void close() throws IOException {
        this.watcher.close();
    }

    /**
     * Gets {@link RefreshWatcher}
     * 
     * @return
     */
    public RefreshWatcher getWatcher() {
        return watcher;
    }

}