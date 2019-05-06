package com.wl4g.devops.shell.utils;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Shell command line tools
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public abstract class LineUtils {

	/**
	 * Resolve source commands
	 * 
	 * @param args
	 * @return
	 */
	public static List<String> parse(String line) {
		List<String> commands = new ArrayList<>();
		if (isBlank(line)) {
			return commands;
		}

		String[] arr = String.valueOf(line).trim().split(" ");
		if (arr != null && arr.length > 0) {
			commands.add(arr[0]); // Main opt
			for (int i = 1; i < arr.length; i++) {
				commands.add(arr[i].trim());
				if (i < (arr.length - 1)) {
					String value = arr[i + 1].trim();
					if (!startsWith(value, "-")) {
						commands.add(value);
						++i;
					} else { // Example(-b): $> add -a 10 -b -c
						commands.add(EMPTY);
					}
				} else { // Example(-c): $> add -a 10 -b -c
					commands.add(EMPTY);
				}
			}
		}

		return commands;
	}

	/**
	 * Clean opt `--argname` or `-x` to argname(x)
	 * 
	 * @param argname
	 * @return
	 */
	public static String clean(String argname) {
		if (startsWith(argname, "-")) {
			return argname.substring(argname.lastIndexOf("-") + 1);
		}
		return argname;
	}

	public static void main(String[] args) {
		System.out.println(parse("add1 -a 11 -b "));
		System.out.println(parse(" ").size());
	}

}
