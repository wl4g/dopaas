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
package com.wl4g.devops.common.web;

import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.restful.BizInvalidArgRestfulException;
import com.wl4g.devops.common.exception.restful.BizRuleRestrictRestfulException;
import com.wl4g.devops.common.exception.restful.ServiceUnavailableRestfulException;

/**
 * Generic restful response base model wrapper.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年3月9日
 * @since
 */
@Beta
public class RespBase<D> implements Serializable {
	final private static long serialVersionUID = 2647155468624590650L;

	private RetCode code = RetCode.OK;
	private String status = DEFAULT_STATUS; // [Extensible]
	private String message = EMPTY;
	private Object data = DEFAULT_DATA;

	public RespBase() {
		this(null);
	}

	public RespBase(RetCode retCode) {
		this(retCode, null);
	}

	public RespBase(DataMap<D> data, String status) {
		this(null, data);
	}

	public RespBase(RetCode retCode, DataMap<D> data) {
		this(retCode, null, data);
	}

	public RespBase(RetCode retCode, String message, DataMap<D> data) {
		this(retCode, null, message, data);
	}

	public RespBase(RetCode retCode, String status, String message, DataMap<D> data) {
		setCode(retCode);
		setStatus(status);
		setMessage(message);
		setData(data);
	}

	/**
	 * Get response code value.
	 * 
	 * @return
	 */
	public int getCode() {
		return code.getErrcode();
	}

	/**
	 * Setup response code of {@link RetCode}.
	 * 
	 * @param retCode
	 * @return
	 */
	public RespBase<D> setCode(RetCode retCode) {
		this.code = retCode != null ? retCode : this.code;
		return this;
	}

	/**
	 * Get status
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Setup status.
	 * 
	 * @param status
	 * @return
	 */
	public RespBase<D> setStatus(String status) {
		if (!isBlank(status)) {
			this.status = status;
		}
		return this;
	}

	/**
	 * Get error message text.
	 * 
	 * @return
	 */
	public String getMessage() {
		return isBlank(message) ? code.getErrmsg() : message;
	}

	/**
	 * Setup error message text.
	 * 
	 * @return
	 */
	public RespBase<D> setMessage(String message) {
		this.message = ErrorMessagePrefixBuilder.build(code, !isBlank(message) ? message : this.message);
		return this;
	}

	/**
	 * Setup error throwable, does not set response status code.
	 * 
	 * @param th
	 * @return
	 */
	public RespBase<D> setThrowable(Throwable th) {
		return setMessage(getRootCausesString(th));
	}

	/**
	 * Handle exceptions, at the same time, the restful API compatible error
	 * status code is automatically set. If there is no match, the default value
	 * of {@link RetCode.SYS_ERR} is used
	 * 
	 * @param th
	 * @return
	 */
	public RespBase<D> handleError(Throwable th) {
		setCode(getRestfulCode(th, RetCode.SYS_ERR));
		setMessage(this.message = getRootCausesString(th));
		return this;
	}

	/**
	 * Get for response data node of {@link DataMap}.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DataMap<D> forMap() {
		if (!isInstanceOfDataMap()) {
			this.data = new DataMap<>();
		}
		return (DataMap<D>) data;
	}

	/**
	 * Get response data node of {@link Object}.
	 * 
	 * @return
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Setup response data.
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public RespBase<D> setData(Object data) {
		if (nonNull(data)) {
			// Check.
			if (isInstanceOfDataMap() && !isEmpty((Map) this.data)) {
				throw new IllegalStateException(
						String.format("RespBase.data already elements, setData() requires data to be empty. - %s", this.data));
			}
			this.data = data;
		}
		return this;
	}

	/**
	 * Create child node data map.
	 * 
	 * @param nodeKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DataMap<Object> buildNodeMap(String nodeKey) {
		hasText(nodeKey, "RespBase build datamap nodeKey name can't be empty");
		DataMap<Object> nodeMap = new DataMap<>();
		forMap().put(nodeKey, (D) nodeMap);
		return nodeMap;
	}

	/**
	 * Check whether the current data node belongs to the instance of
	 * {@link DataMap}
	 * 
	 * @return
	 */
	private boolean isInstanceOfDataMap() {
		return nonNull(data) && (data instanceof DataMap);
	}

	@Override
	public String toString() {
		return "{" + (code != null ? "code=" + code + ", " : "") + (message != null ? "message=" + message + ", " : "")
				+ (data != null ? "data=" + data : "") + "}";
	}

	// --- Function's. ---

	/**
	 * Get restful exceptions and corresponding response status code.
	 * 
	 * @param th
	 * @return
	 */
	public final static RetCode getRestfulCode(Throwable th) {
		return getRestfulCode(th, null);
	}

	/**
	 * Get restful exceptions and corresponding response status code.
	 * 
	 * @param th
	 * @param defaultCode
	 *            default status code
	 * @return
	 * @see {@link RESTfulException}
	 * @see {@link BizRuleRestrictRestfulException}
	 * @see {@link BizInvalidArgRestfulException}
	 * @see {@link ServiceUnavailableRestfulException}
	 */
	public final static RetCode getRestfulCode(Throwable th, RetCode defaultCode) {
		if (nonNull(th)) {
			if (th instanceof BizRuleRestrictRestfulException) {
				return ((BizRuleRestrictRestfulException) th).getCode();
			} else if (th instanceof BizInvalidArgRestfulException) {
				return ((BizInvalidArgRestfulException) th).getCode();
			} else if (th instanceof ServiceUnavailableRestfulException) {
				return ((ServiceUnavailableRestfulException) th).getCode();
			}
		}
		return defaultCode;
	}

	/**
	 * New create {@link RespBase} instance.
	 * 
	 * @return
	 */
	public final static <T> RespBase<T> create() {
		return create(null);
	}

	/**
	 * New create {@link RespBase} instance.
	 * 
	 * @param status
	 * @return
	 */
	public final static <T> RespBase<T> create(String status) {
		return new RespBase<T>().setStatus(status);
	}

	/**
	 * Checking the response status code for success.
	 * 
	 * @param resp
	 * @return
	 */
	public final static boolean isSuccess(RespBase<?> resp) {
		return resp != null && RetCode.OK.getErrcode() == resp.getCode();
	}

	/**
	 * Check whether the {@link RespBase} status code is the expected value
	 * 
	 * @param resp
	 * @param retCode
	 * @return
	 */
	public final static boolean eq(RespBase<?> resp, RetCode retCode) {
		return resp != null && retCode.getErrcode() == resp.getCode();
	}

	/**
	 * Response data model
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月22日
	 * @since
	 * @param <V>
	 */
	@Beta
	public static class DataMap<V> extends LinkedHashMap<String, V> {
		private static final long serialVersionUID = 741193108777950437L;

		/**
		 * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
		 * with the specified initial capacity and a default load factor (0.75).
		 *
		 * @throws IllegalArgumentException
		 *             if the initial capacity is negative
		 */
		public DataMap() {
		}

		/**
		 * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
		 * with the specified initial capacity and a default load factor (0.75).
		 *
		 * @param initialCapacity
		 *            the initial capacity
		 * @throws IllegalArgumentException
		 *             if the initial capacity is negative
		 */
		public DataMap(int initialCapacity) {
			super(initialCapacity);
		}

		/**
		 * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
		 * with the specified initial capacity and load factor.
		 *
		 * @param initialCapacity
		 *            the initial capacity
		 * @param loadFactor
		 *            the load factor
		 * @throws IllegalArgumentException
		 *             if the initial capacity is negative or the load factor is
		 *             nonpositive
		 */
		public DataMap(int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
		}

		@Override
		public V put(String key, V value) {
			if (isNotBlank(key) && value != null) {
				return super.put(key, value);
			}
			return null;
		}

		@Override
		public V putIfAbsent(String key, V value) {
			if (isNotBlank(key) && value != null) {
				return super.putIfAbsent(key, value);
			}
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends V> m) {
			if (!CollectionUtils.isEmpty(m)) {
				super.putAll(m);
			}
		}

		public DataMap<V> andPut(String key, V value) {
			put(key, value);
			return this;
		}

		public DataMap<V> andPutIfAbsent(String key, V value) {
			putIfAbsent(key, value);
			return this;
		}

		public DataMap<V> andPutAll(Map<? extends String, ? extends V> m) {
			putAll(m);
			return this;
		}

	}

	/**
	 * Response code definitions
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月10日
	 * @since
	 */
	public static enum RetCode {

		/**
		 * Successful code<br/>
		 * {@link HttpStatus.OK}
		 */
		OK(HttpStatus.OK.value(), "ok"),

		/**
		 * Parameter error<br/>
		 * {@link HttpStatus.BAD_REQUEST}
		 */
		PARAM_ERR(HttpStatus.BAD_REQUEST.value(), "Parameter error"),

		/**
		 * Business constraints<br/>
		 * {@link HttpStatus.NOT_IMPLEMENTED}
		 */
		BIZ_ERR(HttpStatus.EXPECTATION_FAILED.value(), "Business restricted"),

		/**
		 * Business locked constraints<br/>
		 * {@link HttpStatus.LOCKED}
		 */
		LOCKD_ERR(HttpStatus.LOCKED.value(), "Locked"),

		/**
		 * Unauthenticated<br/>
		 * {@link HttpStatus.UNAUTHORIZED}
		 */
		UNAUTHC(HttpStatus.UNAUTHORIZED.value(), "Unauthenticated"),

		/**
		 * Second Uncertified<br/>
		 * {@link HttpStatus.PRECONDITION_FAILED}
		 */
		SECOND_UNAUTH(HttpStatus.PRECONDITION_FAILED.value(), "Second uncertified"),

		/**
		 * Unauthorized<br/>
		 * {@link HttpStatus.FORBIDDEN}
		 */
		UNAUTHZ(HttpStatus.FORBIDDEN.value(), "Unauthorized"),

		/**
		 * System abnormality<br/>
		 * {@link HttpStatus.SERVICE_UNAVAILABLE}
		 */
		SYS_ERR(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable, please try again later"),

		/**
		 * Internal dynamic status code customizer.</br>
		 * 
		 * @see {@link com.wl4g.devops.common.web.RespBase.RetCode#create}
		 * @see {@link com.wl4g.devops.common.web.RespBase.RetCode#customizerLocal}
		 */
		_$$(-1, "Unknown error");

		/**
		 * Dynamic status code store customizer.</br>
		 * 
		 * @see {@link #_$$}
		 * @see {@link #create(int, String)}
		 */
		final private static ThreadLocal<Object[]> customizerLocal = new InheritableThreadLocal<>();

		private int errcode;
		private String errmsg;

		private RetCode(int code, String msg) {
			Assert.hasText(msg, "Result message definition must not be empty.");
			this.errcode = code;
			this.errmsg = msg;
		}

		/**
		 * Get error code.</br>
		 * If custom status code ({@link #_$$}) is used, it takes precedence.
		 * 
		 * @return
		 */
		public int getErrcode() {
			if (this == _$$) {
				Object errcode = customizerLocal.get()[0];
				notNull(errcode, "Respbase customizer errcode must not be null.");
				return (int) errcode;
			}
			return errcode;
		}

		/**
		 * Get error message.</br>
		 * If custom status error message ({@link #_$$}) is used, it takes
		 * precedence.
		 * 
		 * @return
		 */
		public String getErrmsg() {
			if (this == _$$) {
				Object errmsg = customizerLocal.get()[1];
				notNull(errmsg, "Respbase customizer errmsg must not be null.");
				return (String) errmsg;
			}
			return errmsg;
		}

		/**
		 * Case insensitive handling of JACKSON mapping enumeration.
		 * 
		 * <a href=
		 * "https://www.cnblogs.com/chyu/p/9177140.htmlhttps://www.cnblogs.com/chyu/p/9177140.html">See</a>
		 * 
		 * @param value
		 * @return
		 */
		@JsonCreator
		final public static RetCode get(String nameOrCode) {
			RetCode code = safeOf(nameOrCode);
			if (Objects.nonNull(code)) {
				return code;
			}
			throw new IllegalArgumentException(String.format("'%s'", nameOrCode));
		}

		/**
		 * Safe convert retCode.
		 * 
		 * @param nameOrCode
		 * @return
		 */
		final public static RetCode safeOf(String nameOrCode) {
			String tmp = String.valueOf(nameOrCode);
			for (RetCode v : values()) {
				if (v.name().equalsIgnoreCase(tmp) || String.valueOf(v.getErrcode()).equalsIgnoreCase(tmp)) {
					return v;
				}
			}
			return null;
		}

		/**
		 * Create custom status code, refer to {@link #_$$}
		 * 
		 * @param errcode
		 * @param errmsg
		 * @return
		 */
		final public static RetCode create(int errcode, String errmsg) {
			Assert.hasText(errmsg, "Result errmsg definition must not be empty.");
			customizerLocal.set(new Object[] { errcode, errmsg });
			return _$$;
		}

	}

	/**
	 *
	 * Global errors code message prefix builder.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月7日
	 * @since
	 */
	final static class ErrorMessagePrefixBuilder {

		/**
		 * Errors prefix definition.
		 * 
		 * @see {@link com.wl4g.devops.common.web.RespBase#globalErrPrefix()}
		 */
		private static String ErrorPrefixString = "api"; // by-default.

		/**
		 * Building error message with prefix.
		 * 
		 * @param retCode
		 * @param errmsg
		 * @return
		 */
		final public static String build(RetCode retCode, String errmsg) {
			if (!isBlank(errmsg)) {
				String prefixString = String.format("[%s-%s] ", ErrorPrefixString, retCode.getErrcode());
				return contains(errmsg, prefixString) ? errmsg : (prefixString + errmsg);
			}
			return errmsg;
		}

		/**
		 * Setup global error message prefix.
		 * 
		 * @param errorPrefix
		 */
		final public static void setup(String errorPrefix) {
			hasText(errorPrefix, "Global errors prefix can't be empty.");
			ErrorPrefixString = errorPrefix;
		}

	}

	/**
	 * Default status value.
	 */
	final public static String DEFAULT_STATUS = "normal";

	/**
	 * Default status data value.
	 */
	final public static Object DEFAULT_DATA = new Object();

}