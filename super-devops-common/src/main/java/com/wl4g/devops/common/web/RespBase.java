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
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.convertBean;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@SuppressWarnings("unchecked")
	private D data = (D) DEFAULT_DATA;

	public RespBase() {
		this(null);
	}

	public RespBase(RetCode retCode) {
		this(retCode, null);
	}

	public RespBase(D data, String status) {
		this(null, data);
	}

	public RespBase(RetCode retCode, D data) {
		this(retCode, null, data);
	}

	public RespBase(RetCode retCode, String message, D data) {
		this(retCode, null, message, data);
	}

	public RespBase(RetCode retCode, String status, String message, D data) {
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
	 * Get response data node of {@link Object}.
	 * 
	 * @return
	 */
	public D getData() {
		return data;
	}

	// --- Expanded's. ---.

	/**
	 * Setup response bean to data.
	 * 
	 * @param data
	 * @return
	 */
	public RespBase<D> setData(D data) {
		if (isNull(data)) {
			return this;
		}
		if (isAvailablePayload()) { // Data already payLoad ?
			throw new IllegalStateException(String.format(
					"RespBase.data already payLoad, In order to set it successful the data node must be the initial value or empty. - %s",
					getData()));
		}
		this.data = data;
		return this;
	}

	/**
	 * Setup error throwable, does not set response status code.
	 * 
	 * @param th
	 * @return
	 */
	@JsonIgnore
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
	@JsonIgnore
	public RespBase<D> handleError(Throwable th) {
		setCode(getRestfulCode(th, RetCode.SYS_ERR));
		setMessage(this.message = getRootCausesString(th));
		return this;
	}

	/**
	 * As {@link RespBase#data} convert to {@link DataMap}.
	 * 
	 * @see {@link RespBase#getData()}
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	@JsonIgnore
	public DataMap<Object> asMap() {
		if (data instanceof Map) { // type of Map ?
			return (DataMap<Object>) data;
		}
		return convertBean(data, DataMap.class);
	}

	/**
	 * Build for response data node of {@link DataMap}.
	 * 
	 * @see {@link RespBase#getData()}
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@JsonIgnore
	public synchronized DataMap<Object> buildMap() {
		if (!isAvailablePayload()) { // Data unalready ?
			data = (D) new DataMap<>(); // Init
		} else {
			// Convert to DataMap.
			/**
			 * ###[Note(scene): This logic is to solve the data analysis of, for
			 * example:{@link org.springframework.web.client.RestTemplate}.response]
			 */
			if (data instanceof Map) { // e.g.LinkedHashMap
				if (data instanceof DataMap) {
					this.data = (D) data;
				} else {
					this.data = (D) new DataMap<>((Map) data);
				}
			} else {
				String errmsg = String.format(
						"Illegal type compatible operation, because RespBase.data has initialized the available data, class type is: %s, and forMap() requires RespBase.data to be uninitialized or the initialized data type is must an instance of Map",
						data.getClass());
				throw new UnsupportedOperationException(errmsg);
			}
		}
		return (DataMap<Object>) data;
	}

	/**
	 * Build child node data map.
	 * 
	 * @param nodeKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@JsonIgnore
	public DataMap<Object> buildNode(String nodeKey) {
		hasText(nodeKey, "RespBase build datamap nodeKey name can't be empty");
		DataMap<Object> nodeMap = new DataMap<>();
		buildMap().put(nodeKey, (D) nodeMap);
		return nodeMap;
	}

	/**
	 * Check whether the {@link RespBase#data} is available, for example, it
	 * will become available payload after {@link RespBase#setData(Object)} or
	 * {@link RespBase#buildMap()} has been invoked.
	 * 
	 * @return
	 */
	private boolean isAvailablePayload() {
		return nonNull(data) && data != DEFAULT_DATA;
	}

	/**
	 * As convert {@link RespBase} to JSON string.
	 * 
	 * @return
	 */
	public String asJson() {
		return toJSONString(this);
	}

	@Override
	public String toString() {
		return "RespBase [code=" + getCode() + ", status=" + getStatus() + ", message=" + getMessage() + ", data=" + getData()
				+ "]";
	}

	// --- Function tool's. ---

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

		/**
		 * Constructs an insertion-ordered <tt>LinkedHashMap</tt> instance with
		 * the same mappings as the specified map. The <tt>LinkedHashMap</tt>
		 * instance is created with a default load factor (0.75) and an initial
		 * capacity sufficient to hold the mappings in the specified map.
		 *
		 * @param m
		 *            the map whose mappings are to be placed in this map
		 * @throws NullPointerException
		 *             if the specified map is null
		 */
		public DataMap(Map<String, V> m) {
			super(m);
		}

		/**
		 * Constructs an empty <tt>LinkedHashMap</tt> instance with the
		 * specified initial capacity, load factor and ordering mode.
		 *
		 * @param initialCapacity
		 *            the initial capacity
		 * @param loadFactor
		 *            the load factor
		 * @param accessOrder
		 *            the ordering mode - <tt>true</tt> for access-order,
		 *            <tt>false</tt> for insertion-order
		 * @throws IllegalArgumentException
		 *             if the initial capacity is negative or the load factor is
		 *             nonpositive
		 */
		public DataMap(int initialCapacity, float loadFactor, boolean accessOrder) {
			super(initialCapacity, loadFactor, accessOrder);
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
		OK(HttpStatus.OK.value(), "Ok"),

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
			hasText(msg, "Result message definition must not be empty.");
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
			hasText(errmsg, "Result errmsg definition must not be empty.");
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
	final public static String DEFAULT_STATUS = "Normal";

	/**
	 * Default data value.</br>
	 * <font color=red>Note: can't be {@link DEFAULT_DATA} = new Object(),
	 * otherwise jackson serialization will have the following error,
	 * e.g.:</font>
	 * 
	 * <pre>
	 *JsonMappingException: No serializer found for class java.lang.Object and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: com.wl4g.devops.common.web.RespBase["data"])
	 * </pre>
	 */
	final public static Object DEFAULT_DATA = emptyMap();

}