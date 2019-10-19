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
package com.wl4g.devops.common.constants;

/**
 * DevOps SCM Constants.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
public abstract class SCMDevOpsConstants extends DevOpsConstants {

	//
	// Common and Based.
	//

	/** Server pre-release source response data key. */
	final public static String KEY_PRE_RELEASE = "pre-release-source";
	/** Server release source response data key. */
	final public static String KEY_RELEASE = "release-source";
	/** Client environment source response data key. */
	final public static String KEY_USED_SOURCES = "used-source";

	//
	// Server.
	//

	/** Service web root URI. */
	final public static String URI_S_BASE = "/endpoint";
	/** Get property source URI. */
	final public static String URI_S_SOURCE_GET = "source";
	/** Report configuration result URI. */
	final public static String URI_S_REPORT_POST = "report";
	/** Long-polling watching URI. */
	final public static String URI_S_WATCH_GET = "watch";

	/** SCM published group. */
	final public static String CACHE_PUB_GROUPS = "scm_pub_groups";
	/** SCM published CONFIG prefix. */
	final public static String KEY_PUB_PREFIX = "scm_pub_config_";

	//
	// Client.
	//

	/** Service web root URI. */
	final public static String URI_C_BASE = "/scm-client";
	/** Refresh URI. */
	final public static String URI_C_REFRESH = "refresh";

}