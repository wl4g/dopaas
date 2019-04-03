package com.wl4g.devops.common.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DevOps SRM Constants.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
public abstract class SRMDevOpsConstants extends DevOpsConstants {

	/** Logging level names define. */
	final public static List<String> LOG_LEVEL = Collections
			.unmodifiableList(Arrays.asList("TRACE", "DEBUG", "INFO", "WARN", "ERROR"));

	/** Elastic search default message name define. */
	final public static String KEY_DEFAULT_MSG = "message";

}
