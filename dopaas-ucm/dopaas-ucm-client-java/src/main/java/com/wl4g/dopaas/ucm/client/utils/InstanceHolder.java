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
package com.wl4g.dopaas.ucm.client.utils;

import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;

import com.wl4g.dopaas.common.bean.ucm.model.BaseConfigInfo.ConfigInstance;
import com.wl4g.dopaas.common.utils.InetHolder;
import com.wl4g.dopaas.ucm.client.internal.AbstractUcmClientConfig;
import com.wl4g.infra.common.log.SmartLogger;

/**
 * Instance node holder.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月1日
 * @since
 */
public class InstanceHolder {
    protected final SmartLogger log = getLogger(getClass());

    /** That application config instance node. */
    private final ConfigInstance configInstance;

    public InstanceHolder(AbstractUcmClientConfig<?> config) {
        String localAddress = new InetHolder(config.getInet()).getFirstNonLoopbackHostInfo().getIpAddress();
        this.configInstance = new ConfigInstance(localAddress, config.getServiceId());
    }

    /**
     * Gets {@link ConfigInstance}
     * 
     * @return
     */
    public ConfigInstance getConfigInstance() {
        return configInstance;
    }

}