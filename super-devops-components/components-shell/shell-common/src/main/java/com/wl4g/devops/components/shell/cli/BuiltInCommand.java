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
package com.wl4g.devops.components.shell.cli;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.components.shell.annotation.ShellOption;

import static org.apache.commons.lang3.StringUtils.*;
import static com.wl4g.devops.components.shell.utils.LineUtils.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.*;

/**
 * Internal built-in commands
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public abstract class BuiltInCommand {

	/**
	 * Default long help method suffix.
	 */
	final public static String INTERNAL_HELP = "help";

	/**
	 * Default short help method suffix.
	 */
	final public static String INTERNAL_HE = "he";

	/**
	 * Default long exit method suffix.
	 */
	final public static String INTERNAL_EXIT = "exit";

	/**
	 * Default short quit method suffix.
	 */
	final public static String INTERNAL_QUIT = "quit";

	/**
	 * Default short quit method suffix.
	 */
	final public static String INTERNAL_QU = "qu";

	/**
	 * Default short exit method suffix.
	 */
	final public static String INTERNAL_EX = "ex";

	/**
	 * Default long history method suffix.
	 */
	final public static String INTERNAL_HISTORY = "history";

	/**
	 * Default short history method suffix.
	 */
	final public static String INTERNAL_HIS = "his";

	/**
	 * Default long clear method suffix.
	 */
	final public static String INTERNAL_CLEAR = "clear";

	/**
	 * Default short clear method suffix.
	 */
	final public static String INTERNAL_CLS = "cls";

	/**
	 * Default long stacktrace method suffix.
	 */
	final public static String INTERNAL_STACKTRACE = "stacktrace";

	/**
	 * Default short stacktrace method suffix.
	 */
	final public static String INTERNAL_ST = "st";

	final private static List<String> CMDS = new ArrayList<>();

	static {
		Field[] fields = BuiltInCommand.class.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			if (startsWithAny(f.getName(), "INTERNAL")) {
				try {
					CMDS.add((String) f.get(null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Check contains defineKey whether it is naming conflict with built-in
	 * commands
	 * 
	 * @param defineKey
	 * @return
	 */
	final public static boolean contains(String... defineKeys) {
		for (String key : defineKeys) {
			if (CMDS.contains(key) || CMDS.contains(clean(key))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get GNU long option
	 * 
	 * @param defineKey
	 * @return
	 */
	final public static String getGNULong(String defineKey) {
		hasText(defineKey, "defineKey must not be emtpy");
		return ShellOption.GNU_CMD_LONG + defineKey;
	}

	/**
	 * Get GNU short option
	 * 
	 * @param defineKey
	 * @return
	 */
	final public static String getGNUShort(String defineKey) {
		hasText(defineKey, "defineKey must not be emtpy");
		return ShellOption.GNU_CMD_SHORT + defineKey;
	}

	/**
	 * To internal command option all.
	 * 
	 * @param defineKey
	 * @return
	 */
	final public static String asCmdsString() {
		StringBuffer cmds = new StringBuffer();
		for (String cmd : CMDS) {
			cmds.append(cmd);
			cmds.append(", ");
		}
		return cmds.toString();
	}

}