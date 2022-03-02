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
package com.wl4g.dopaas.ucm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.wl4g.dopaas.ucm.common.BaseUcmProperties;

import static com.wl4g.dopaas.ucm.config.StandardUcmProperties.PREFIX;

/**
 * UCM client properties.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月3日
 * @since
 */
@ConfigurationProperties(PREFIX)
public class StandardUcmProperties extends BaseUcmProperties {
    private static final long serialVersionUID = -2133451846066162424L;

    /**
     * Prefix for UCM configuration properties.
     */
    final public static String PREFIX = "spring.cloud.devops.ucm";

    private long refreshProtectIntervalMs = 10_000L;

    public long getRefreshProtectIntervalMs() {
        return refreshProtectIntervalMs;
    }

    public void setRefreshProtectIntervalMs(long refreshProtectIntervalMs) {
        this.refreshProtectIntervalMs = refreshProtectIntervalMs;
    }
}