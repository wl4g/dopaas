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
package com.wl4g.devops.tool.common.log;

import static java.util.Objects.isNull;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.wl4g.devops.tool.common.lang.Assert2;

/**
 * Enhanced logger for intelligent/dynamic/humanized wrapper.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-13
 * @since
 */
public class SmartLogger implements Logger {

	/**
	 * Origin logger. {@link Logger}
	 */
	final private Logger orig;

	public SmartLogger(Logger orig) {
		Assert2.notNull(orig, "Origin logger can't null.");
		this.orig = orig;
	}

	protected Logger getOrig() {
		return orig;
	}

	@Override
	public String getName() {
		return orig.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return orig.isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		if (orig.isTraceEnabled()) {
			orig.trace(msg);
		}
	}

	@Override
	public void trace(String format, Object arg) {
		if (orig.isTraceEnabled()) {
			orig.trace(format, arg);
		}
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (orig.isTraceEnabled()) {
			orig.trace(format, arg1, arg2);
		}
	}

	@Override
	public void trace(String format, Object... arguments) {
		if (orig.isTraceEnabled()) {
			orig.trace(format, arguments);
		}
	}

	@SuppressWarnings("unchecked")
	public void trace(String format, Callable<Object>... call) {
		if (orig.isTraceEnabled()) {
			if (!isNull(call)) {
				Object[] args = new Object[call.length];
				for (int i = 0; i < call.length; i++) {
					try {
						args[i] = call[i].call();
					} catch (Exception e) {
						error("", e);
					}
				}
				orig.trace(format, args);
			}
		}
	}

	@Override
	public void trace(String msg, Throwable t) {
		if (orig.isTraceEnabled()) {
			orig.trace(msg, t);
		}
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return orig.isTraceEnabled(marker);
	}

	@Override
	public void trace(Marker marker, String msg) {
		if (orig.isTraceEnabled()) {
			orig.trace(marker, msg);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		if (orig.isTraceEnabled()) {
			orig.trace(marker, format, arg);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		if (orig.isTraceEnabled()) {
			orig.trace(format, arg1, arg2);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		if (orig.isTraceEnabled()) {
			orig.trace(marker, format, argArray);
		}
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		if (orig.isTraceEnabled()) {
			orig.trace(marker, msg, t);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return orig.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		if (orig.isDebugEnabled()) {
			orig.debug(msg);
		}
	}

	@Override
	public void debug(String format, Object arg) {
		if (orig.isDebugEnabled()) {
			orig.debug(format, arg);
		}
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		if (orig.isDebugEnabled()) {
			orig.debug(format, arg1, arg2);
		}
	}

	@Override
	public void debug(String format, Object... arguments) {
		if (orig.isDebugEnabled()) {
			orig.debug(format, arguments);
		}
	}

	@SuppressWarnings("unchecked")
	public void debug(String format, Callable<Object>... calls) {
		if (orig.isDebugEnabled()) {
			if (!isNull(calls)) {
				Object[] args = new Object[calls.length];
				for (int i = 0; i < calls.length; i++) {
					try {
						args[i] = calls[i].call();
					} catch (Exception e) {
						error("", e);
					}
				}
				orig.debug(format, args);
			}
		}
	}

	@Override
	public void debug(String msg, Throwable t) {
		if (orig.isDebugEnabled()) {
			orig.debug(msg, t);
		}
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return orig.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String msg) {
		if (orig.isDebugEnabled()) {
			orig.debug(marker, msg);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		if (orig.isDebugEnabled()) {
			orig.debug(marker, format, arg);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		if (orig.isDebugEnabled()) {
			orig.debug(format, arg1, arg2);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object... argArray) {
		if (orig.isDebugEnabled()) {
			orig.debug(marker, format, argArray);
		}
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		if (orig.isDebugEnabled()) {
			orig.debug(marker, msg, t);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return orig.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		if (orig.isInfoEnabled()) {
			orig.info(msg);
		}
	}

	@Override
	public void info(String format, Object arg) {
		if (orig.isInfoEnabled()) {
			orig.info(format, arg);
		}
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		if (orig.isInfoEnabled()) {
			orig.info(format, arg1, arg2);
		}
	}

	@Override
	public void info(String format, Object... arguments) {
		if (orig.isInfoEnabled()) {
			orig.info(format, arguments);
		}
	}

	@SuppressWarnings("unchecked")
	public void info(String format, Callable<Object>... calls) {
		if (orig.isInfoEnabled()) {
			if (!isNull(calls)) {
				Object[] args = new Object[calls.length];
				for (int i = 0; i < calls.length; i++) {
					try {
						args[i] = calls[i].call();
					} catch (Exception e) {
						error("", e);
					}
				}
				orig.info(format, args);
			}
		}
	}

	@Override
	public void info(String msg, Throwable t) {
		if (orig.isInfoEnabled()) {
			orig.info(msg, t);
		}
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return orig.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String msg) {
		if (orig.isInfoEnabled()) {
			orig.info(marker, msg);
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		if (orig.isInfoEnabled()) {
			orig.info(marker, format, arg);
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		if (orig.isInfoEnabled()) {
			orig.info(format, arg1, arg2);
		}
	}

	@Override
	public void info(Marker marker, String format, Object... argArray) {
		if (orig.isInfoEnabled()) {
			orig.info(marker, format, argArray);
		}
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		if (orig.isInfoEnabled()) {
			orig.info(marker, msg, t);
		}
	}

	@Override
	public boolean isWarnEnabled() {
		return orig.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		if (orig.isWarnEnabled()) {
			orig.warn(msg);
		}
	}

	@Override
	public void warn(String format, Object arg) {
		if (orig.isWarnEnabled()) {
			orig.warn(format, arg);
		}
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		if (orig.isWarnEnabled()) {
			orig.warn(format, arg1, arg2);
		}
	}

	@Override
	public void warn(String format, Object... arguments) {
		if (orig.isWarnEnabled()) {
			orig.warn(format, arguments);
		}
	}

	@SuppressWarnings("unchecked")
	public void warn(String format, Callable<Object>... calls) {
		if (orig.isWarnEnabled()) {
			if (!isNull(calls)) {
				Object[] args = new Object[calls.length];
				for (int i = 0; i < calls.length; i++) {
					try {
						args[i] = calls[i].call();
					} catch (Exception e) {
						error("", e);
					}
				}
				orig.warn(format, args);
			}
		}
	}

	@Override
	public void warn(String msg, Throwable t) {
		if (orig.isWarnEnabled()) {
			orig.warn(msg, t);
		}
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return orig.isWarnEnabled(marker);
	}

	@Override
	public void warn(Marker marker, String msg) {
		if (orig.isWarnEnabled()) {
			orig.warn(marker, msg);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		if (orig.isWarnEnabled()) {
			orig.warn(marker, format, arg);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		if (orig.isWarnEnabled()) {
			orig.warn(format, arg1, arg2);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object... argArray) {
		if (orig.isWarnEnabled()) {
			orig.warn(marker, format, argArray);
		}
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		if (orig.isWarnEnabled()) {
			orig.warn(marker, msg, t);
		}
	}

	@Override
	public boolean isErrorEnabled() {
		return orig.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		if (orig.isErrorEnabled()) {
			orig.error(msg);
		}
	}

	@Override
	public void error(String format, Object arg) {
		if (orig.isErrorEnabled()) {
			orig.error(format, arg);
		}
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		if (orig.isErrorEnabled()) {
			orig.error(format, arg1, arg2);
		}
	}

	@Override
	public void error(String format, Object... arguments) {
		if (orig.isErrorEnabled()) {
			orig.error(format, arguments);
		}
	}

	@SuppressWarnings("unchecked")
	public void error(String format, Callable<Object>... calls) {
		if (orig.isErrorEnabled()) {
			if (!isNull(calls)) {
				Object[] args = new Object[calls.length];
				for (int i = 0; i < calls.length; i++) {
					try {
						args[i] = calls[i].call();
					} catch (Exception e) {
						error("", e);
					}
				}
				orig.error(format, args);
			}
		}
	}

	@Override
	public void error(String msg, Throwable t) {
		if (orig.isErrorEnabled()) {
			orig.error(msg, t);
		}
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return orig.isErrorEnabled(marker);
	}

	@Override
	public void error(Marker marker, String msg) {
		if (orig.isErrorEnabled()) {
			orig.error(marker, msg);
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		if (orig.isErrorEnabled()) {
			orig.error(marker, format, arg);
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		if (orig.isErrorEnabled()) {
			orig.error(format, arg1, arg2);
		}
	}

	@Override
	public void error(Marker marker, String format, Object... argArray) {
		if (orig.isErrorEnabled()) {
			orig.error(marker, format, argArray);
		}
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		if (orig.isErrorEnabled()) {
			orig.error(marker, msg, t);
		}
	}

}