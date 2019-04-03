package com.wl4g.devops.common.kit.jvm;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * JVM runtime kit utility
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月12日
 * @since
 */
public class JVMRuntimeKit {

	/**
	 * Whether current JVM runtime debuging mode
	 * 
	 * @return
	 */
	public static boolean isJVMDebuging() {
		List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		boolean debuging = false;
		for (String str : arguments) {
			if (str.startsWith("-agentlib")) {
				debuging = true;
			}
		}
		return debuging;
	}

}
