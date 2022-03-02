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
package com.wl4g.dopaas.ucm.common;

import com.wl4g.dopaas.common.constant.DoPaaSConstants;

/**
 * UCM Constants.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-11-12
 * @since
 */
public abstract class UCMConstants extends DoPaaSConstants {

    // --- Common. ---

    /** Cipher property value prefix. */
    public static final String KEY_CIPHER_PREFIX = "{cipher}";

    /** Server pre-release source response data key. */
    public static final String KEY_PRE_RELEASE = "pre-release-source";
    /** Client environment source response data key. */
    public static final String KEY_USED_SOURCES = "used-source";

    /** Configuration modified status. */
    public static final int WATCH_CHANGED = 200;
    /** Configuration report check status. */
    public static final int WATCH_CHECKPOINT = 103;
    /** Configuration non-modified status. */
    public static final int WATCH_NOT_MODIFIED = 304;

    // --- Server. ---

    /** Service web root URI. */
    public static final String URI_S_BASE = "/ucm-server";
    /** Initialization handshake URI. */
    public static final String URI_S_HANDSHAKE = "handshake";
    /** Long-polling watching URI. */
    public static final String URI_S_SOURCE_WATCH = "watching";
    /** Report configuration result URI. */
    public static final String URI_S_REFRESHED_REPORT = "report";

    /** UCM session cache. */
    public static final String CACHE_SESSIONS = "ucm:session:";
    /** UCM publisher group. */
    public static final String CACHE_PUB_GROUPS = "ucm:publish:groups:";
    /** UCM publisher config prefix. */
    public static final String KEY_PUB_PREFIX = "ucm:publish:config:";

    // --- Client. ---

    /** Key of configuration prefix. */
    public static final String KEY_UCM_BASE_PREFIX = KEY_DOPAAS_BASE_PREFIX + ".ucm";
    /** Key of client configuration. */
    public static final String KEY_UCM_CLIENT = KEY_UCM_BASE_PREFIX + ".client";
    /** Service web root URI. */
    public static final String URI_C_BASE = "/ucm-client";
    /** Refresh URI. */
    public static final String URI_C_REFRESH = "refresh";

}