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
package com.wl4g.devops.components.shell.utils;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * Shell command line tools
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public abstract class LineUtils {

	final public static Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Resolve source commands
	 * 
	 * @param args
	 * @return
	 */
	public static LinkedList<String> parse(String line) {
		if (isBlank(line)) {
			return new LinkedList<>();
		}
		String[] args = split(repairLineSpace(line), " ");
		return parse(args);
	}

	/**
	 * Resolve source commands
	 * 
	 * @param args
	 * @return
	 */
	public static LinkedList<String> parse(String[] args) {
		LinkedList<String> commands = new LinkedList<>();
		if (args == null || args.length == 0) {
			return commands;
		}

		if (args != null && args.length > 0) {
			commands.add(args[0]); // Main opt
			for (int i = 1; i < args.length; i++) {
				commands.add(args[i].trim());
				if (i < (args.length - 1)) {
					String value = args[i + 1].trim();
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
	 * Execution shell commands
	 * 
	 * @param line
	 * @return
	 */
	public static String execAsString(String line) {
		return execAsString(line, 30 * 1000, "UTF-8");
	}

	/**
	 * Execution shell commands
	 * 
	 * @param line
	 * @param timeout
	 * @param charset
	 * @return
	 */
	public static String execAsString(String line, long timeout, String charset) {
		// Standard output
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// Error output
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		CommandLine commandline = CommandLine.parse(line);
		DefaultExecutor exec = new DefaultExecutor();
		exec.setExitValues(null);
		// Timeout
		ExecuteWatchdog watch = new ExecuteWatchdog(timeout);
		exec.setWatchdog(watch);
		PumpStreamHandler handler = new PumpStreamHandler(out, err);
		exec.setStreamHandler(handler);
		try {
			exec.execute(commandline);
			// Different operating systems should pay attention to coding,
			// otherwise the results will be scrambled.
			String error = err.toString(charset);
			if (isNotBlank(error)) {
				throw new IllegalStateException(error.toString());
			}
			return out.toString(charset);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Clean opt.</br>
	 * '--arg1' or '-x' => x
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

	/**
	 * Repair input line.</br>
	 * 
	 * <pre>
	 * e.g. arg1 -l x1, x2 -m a1=b1, a2 = b2 -p aa1=bb1,aa2= bb2 -s x3, , x4 </br>
	 * =>  arg1 -l x1,x2 -m a1=b1,a2=b2 -p aa1=bb1,aa2=bb2 -s x3,,x4
	 * </pre>
	 * 
	 * @param line
	 * @return
	 */
	private static String repairLineSpace(String line) {
		line = trimToEmpty(line);
		StringBuffer newLine = new StringBuffer();
		String args[] = split(line, " ");

		// e.g. arg1 -l x1, x2 -m a1=b1, a2 = b2 -p aa1=bb1,aa2= bb2 -s x3, , x4
		if (args.length > 2) {
			for (String arg : args) {
				if (startsWith(arg, "-")) {
					newLine.append(" ");
					newLine.append(arg);
					newLine.append(" ");
				} else {
					newLine.append(arg);
				}
			}
		}
		// e.g. help mycmd/mycmd --help
		else {
			newLine.append(line);
		}

		return newLine.toString();
	}

	public static void main(String[] args) {
		// System.out.println(parse("add1 -a 11 -b "));
		// System.out.println(parse(" ").size());
		// System.out.println(execAsString("cmd.exe /p /h C:\\Document"));
		String s = "arg1 -l x1, x2 -m a1=b1, a2 = b2 -p aa1=bb1,aa2= bb2 -s x3, , x4 ";
		System.out.println(repairLineSpace(s));
		System.out.println(parse(s));
	}

}