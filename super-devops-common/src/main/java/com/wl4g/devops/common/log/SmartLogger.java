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
package com.wl4g.devops.common.log;

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.wl4g.devops.common.annotation.Unused;
import com.wl4g.devops.tool.common.lang.Assert;

/**
 * More intelligent/dynamic/humanized logger wrapper.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-13
 * @since
 */
@Unused
public class SmartLogger implements Logger {

	final private Logger orig;

	public SmartLogger(Logger orig) {
		Assert.notNull(orig, "Origin logger can't null.");
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
		orig.trace(msg);
	}

	@Override
	public void trace(String format, Object arg) {
		orig.trace(format, arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		orig.trace(format, arg1, arg2);
	}

	@Override
	public void trace(String format, Object... arguments) {
		orig.trace(format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		orig.trace(msg, t);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return orig.isTraceEnabled(marker);
	}

	@Override
	public void trace(Marker marker, String msg) {
		orig.trace(marker, msg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		orig.trace(marker, format, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		orig.trace(format, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		orig.trace(marker, format, argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		orig.trace(marker, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return orig.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		orig.debug(msg);
	}

	@Override
	public void debug(String format, Object arg) {
		orig.debug(format, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		orig.debug(format, arg1, arg2);
	}

	@Override
	public void debug(String format, Object... arguments) {
		orig.debug(format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		orig.debug(msg, t);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return orig.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String msg) {
		orig.debug(marker, msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		orig.debug(marker, format, arg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		orig.debug(format, arg1, arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... argArray) {
		orig.debug(marker, format, argArray);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		orig.debug(marker, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return orig.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		orig.info(msg);
	}

	@Override
	public void info(String format, Object arg) {
		orig.info(format, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		orig.info(format, arg1, arg2);
	}

	@Override
	public void info(String format, Object... arguments) {
		orig.info(format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		orig.info(msg, t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return orig.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String msg) {
		orig.info(marker, msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		orig.info(marker, format, arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		orig.info(format, arg1, arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... argArray) {
		orig.info(marker, format, argArray);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		orig.info(marker, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return orig.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		orig.warn(msg);
	}

	@Override
	public void warn(String format, Object arg) {
		orig.warn(format, arg);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		orig.warn(format, arg1, arg2);
	}

	@Override
	public void warn(String format, Object... arguments) {
		orig.warn(format, arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		orig.warn(msg, t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return orig.isWarnEnabled(marker);
	}

	@Override
	public void warn(Marker marker, String msg) {
		orig.warn(marker, msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		orig.warn(marker, format, arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		orig.warn(format, arg1, arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... argArray) {
		orig.warn(marker, format, argArray);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		orig.warn(marker, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return orig.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		orig.error(msg);
	}

	@Override
	public void error(String format, Object arg) {
		orig.error(format, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		orig.error(format, arg1, arg2);
	}

	@Override
	public void error(String format, Object... arguments) {
		orig.error(format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		orig.error(msg, t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return orig.isErrorEnabled(marker);
	}

	@Override
	public void error(Marker marker, String msg) {
		orig.error(marker, msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		orig.error(marker, format, arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		orig.error(format, arg1, arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... argArray) {
		orig.error(marker, format, argArray);
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		orig.error(marker, msg, t);
	}

}
