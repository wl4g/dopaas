package com.wl4g.devops.common.constants;

import java.util.Collections;
import java.util.Map;

public abstract class DevOpsConstants {

	// System environment map cache.
	final protected static Map<String, String> ENV = Collections.unmodifiableMap(System.getenv());

	/**
	 * Controlling Spring-enabled Unified Exception Handling Stack Information
	 */
	final public static String PARAM_STACK_TRACE = ENV.getOrDefault("spring.error.stack.trace", "_stacktrace");

}
