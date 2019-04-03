package com.wl4g.devops.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

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
	public static Throwable getRootCauses(final Throwable thw) {
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

}
