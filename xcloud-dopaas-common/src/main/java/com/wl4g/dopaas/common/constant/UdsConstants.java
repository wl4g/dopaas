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
package com.wl4g.dopaas.common.constant;

/**
 * UDS(Unified Distributed Scheduler) constants
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-01-29
 * @sine v1.0
 * @see
 */
public abstract class UdsConstants extends DoPaaSConstants {

	public static final String KEY_UDS_PREFIX = KEY_DOPAAS_BASE_PREFIX + ".uds";

	public static final String KEY_UDS_ELASTICJOBCLOUD_TRACE_PREFIX = KEY_UDS_PREFIX + ".elasticjobcloud.trace";

	public static final String KEY_UDS_ELASTICJOBCLOUD_ZK_PREFIX = KEY_UDS_PREFIX + ".elasticjobcloud.zookeeper";

	public static final String KEY_UDS_ELASTICJOBCLOUD_JOBSTATE_PREFIX = KEY_UDS_PREFIX + ".elasticjobcloud.jobstate";

}