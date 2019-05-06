package com.wl4g.devops.shell.cli;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.utils.Assert;
import static com.wl4g.devops.shell.utils.LineUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Internal commands
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public abstract class InternalCommand {

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

	final private static List<String> CMDS = new ArrayList<>();

	static {
		Field[] fields = InternalCommand.class.getDeclaredFields();
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
		Assert.hasText(defineKey, "defineKey must not be emtpy");
		return ShellOption.GNU_CMD_LONG + defineKey;
	}

	/**
	 * Get GNU short option
	 * 
	 * @param defineKey
	 * @return
	 */
	final public static String getGNUShort(String defineKey) {
		Assert.hasText(defineKey, "defineKey must not be emtpy");
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
