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
package com.wl4g.devops.components.tools.common.lang;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trim;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * About the tool class for exceptions.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年2月16日
 * @since
 */
public abstract class Exceptions extends ExceptionUtils {

	/**
	 * Convert CheckedException to UncheckedException.
	 * 
	 * @param e
	 * @return
	 */
	public static RuntimeException unchecked(Exception e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

	/**
	 * Convert ErrorStack to String.
	 * 
	 * @param e
	 * @return
	 */
	public static String getStackTraceAsString(Throwable e) {
		if (e == null) {
			return "";
		}
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	/**
	 * Degradation acquisition and abnormal causes
	 * 
	 * @param thw
	 * @return
	 */
	public static Throwable getRootCauses(Throwable thw) {
		if (thw == null) {
			return null;
		}
		Throwable root = getRootCause(thw);
		return root == null ? thw : root;
	}

	/**
	 * Determine whether anomalies are caused by some underlying anomalies.
	 * 
	 * @param ex
	 * @param causeExceptionClasses
	 * @return
	 */
	@SafeVarargs
	public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
		Throwable cause = ex.getCause();
		while (cause != null) {
			for (Class<? extends Exception> causeClass : causeExceptionClasses) {
				if (causeClass.isInstance(cause)) {
					return true;
				}
			}
			cause = cause.getCause();
		}
		return false;
	}

	/**
	 * Getting exception classes in request
	 * 
	 * @param request
	 * @return
	 */
	public static Throwable getThrowable(HttpServletRequest request) {
		Throwable ex = null;
		if (request.getAttribute("exception") != null) {
			ex = (Throwable) request.getAttribute("exception");
		} else if (request.getAttribute("javax.servlet.error.exception") != null) {
			ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
		}
		return ex;
	}

	/**
	 * Getting root causes string message
	 * 
	 * @param th
	 * @return
	 */
	public static String getRootCausesString(Throwable th) {
		return getRootCausesString(th, true);
	}

	/**
	 * Getting root causes string message
	 * 
	 * @param th
	 * @param extract
	 *            Whether to extract the root cause of anomalies
	 * @return
	 */
	public static String getRootCausesString(Throwable th, boolean extract) {
		if (Objects.isNull(th)) {
			return null;
		}
		String causes = getRootCauseMessage(th);
		String errmsg = isEmpty(causes) ? getMessage(th) : causes;
		if (extract) {
			int index = errmsg.indexOf(DEFAULT_CAUSE_EX_SEPARATE_SUFFIX);
			if (index > 0) {
				errmsg = errmsg.substring(index + DEFAULT_CAUSE_EX_SEPARATE_SUFFIX_LENGTH);
			}
		}
		return trim(errmsg);
	}

	final public static String DEFAULT_CAUSE_EX_SEPARATE_SUFFIX = "Exception: ";
	final public static int DEFAULT_CAUSE_EX_SEPARATE_SUFFIX_LENGTH = DEFAULT_CAUSE_EX_SEPARATE_SUFFIX.length();

}