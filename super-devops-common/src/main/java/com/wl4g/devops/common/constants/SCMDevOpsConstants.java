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

	// Common and Base.
	/** Initial application configuration version. */
	final public static String CONF_DISCOVERY_ROOT = ENV.getOrDefault("spring.cloud.devops.scm.client.discovery.root",
			"/apps-config");

	/** Get server release source response data key. */
	final public static String KEY_RELEASE = "release-source";
	/** Get client environment source response data key. */
	final public static String KEY_ENV_SOURCES = "environment-sources";

	// Server.
	/** Service web root URI. */
	final public static String URI_S_BASE = "/scm-server";
	/** Get property source URI. */
	final public static String URI_S_SOURCE_GET = "source.json";
	/** Report configuration result URI. */
	final public static String URI_S_REPORT_POST = "report.json";

	// Client.
	/** Service web root URI. */
	final public static String URI_C_BASE = "/scm-client";
	/** Refresh URI. */
	final public static String URI_C_REFRESH = "refresh";
	/** Get latest configuration URI. */
	final public static String URI_C_LATEST = "latest";

}
