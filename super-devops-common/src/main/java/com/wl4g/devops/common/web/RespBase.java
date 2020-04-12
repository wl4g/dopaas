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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.restful.InvalidParamsRestfulException;
import com.wl4g.devops.common.exception.restful.BizRuleRestrictRestfulException;
import com.wl4g.devops.common.exception.restful.ServiceUnavailableRestfulException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static com.wl4g.devops.common.web.RespBase.RetCode.newCode;
import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.convertBean;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.state;

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
	private String requestId = DEFAULT_REQUESTID; // [Extensible]
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
	 * Gets response code value.
	 * 
	 * @return
	 */
	public int getCode() {
		return code.getErrcode();
	}

	/**
	 * Sets response code of {@link RetCode}.
	 * 
	 * @param retCode
	 * @return
	 */
	public RespBase<D> setCode(RetCode retCode) {
		if (nonNull(retCode)) {
			this.code = (RetCode) retCode;
		}
		return this;
	}

	/**
	 * Sets response code of int.
	 * 
	 * @param retCode
	 * @return
	 */
	public RespBase<D> setCode(int retCode) {
		this.code = newCode(retCode);
		return this;
	}

	/**
	 * Gets status
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets status.
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
	 * Gets current requestId.
	 * 
	 * @return
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets current requestId.
	 * 
	 * @param requestId
	 * @return
	 */

	public RespBase<D> setRequestId(String requestId) {
		this.requestId = requestId;
		return this;
	}

	/**
	 * Gets error message text.
	 * 
	 * @return
	 */
	public String getMessage() {
		return isBlank(message) ? code.getErrmsg() : message;
	}

	/**
	 * Sets error message text.
	 * 
	 * @return
	 */
	public RespBase<D> setMessage(String message) {
		this.message = ErrorPromptMessageBuilder.build(code, !isBlank(message) ? message : this.message);
		return this;
	}

	/**
	 * Gets response data node of {@link Object}.
	 * 
	 * @return
	 */
	public D getData() {
		return data;
	}

	// --- Expanded's. ---.

	/**
	 * Sets response bean to data.
	 * 
	 * @param data
	 * @return
	 */
	public RespBase<D> setData(D data) {
		if (isNull(data))
			return this;
		if (checkDataAvailable()) // Data already payLoad ?
			throw new IllegalStateException(format(
					"RespBase.data already payLoad, In order to set it successful the data node must be the initial value or empty. - %s",
					getData()));

		this.data = data;
		return this;
	}

	/**
	 * Sets error throwable, does not set response status code.
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
	public synchronized DataMap<Object> asMap() {
		if (isNull(getData()))
			return null;
		if (data instanceof Map) // Type of Map ?
			return (DataMap<Object>) getData();

		setData((D) convertBean(data, DataMap.class));
		return (DataMap<Object>) getData();
	}

	/**
	 * Build {@link DataMap} instance for response data body.(if
	 * {@link RespBase#getData()} is null)
	 * 
	 * @see {@link RespBase#getData()}
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@JsonIgnore
	public synchronized DataMap<Object> forMap() {
		if (!checkDataAvailable()) { // Data unalready ?
			data = (D) new DataMap<>(); // Init
		} else {
			// Convert to DataMap.
			/**
			 * ###[Note(scene): This logic is to solve the data analysis of, for
			 * example:{@link org.springframework.web.client.RestTemplate}.response]
			 */
			if (data instanceof Map) { // e.g:LinkedHashMap
				if (data instanceof DataMap) {
					this.data = (D) data;
				} else {
					this.data = (D) new DataMap<>((Map) data);
				}
			} else {
				String errmsg = format(
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
	public synchronized DataMap<Object> forMapNode(String nodeKey) {
		hasText(nodeKey, "RespBase build datamap nodeKey name can't be empty");
		DataMap<Object> data = forMap();
		DataMap<Object> nodeMap = (DataMap<Object>) data.get(nodeKey);
		if (isNull(nodeMap)) {
			data.put(nodeKey, (D) (nodeMap = new DataMap<>()));
		}
		return nodeMap;
	}

	/**
	 * Check whether the {@link RespBase#data} is available, for example, it
	 * will become available payload after {@link RespBase#setData(Object)} or
	 * {@link RespBase#forMap()} has been invoked.
	 * 
	 * @return
	 */
	private boolean checkDataAvailable() {
		return nonNull(getData()) && getData() != DEFAULT_DATA;
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
	 * @see {@link InvalidParamsRestfulException}
	 * @see {@link ServiceUnavailableRestfulException}
	 */
	public final static RetCode getRestfulCode(Throwable th, RetCode defaultCode) {
		if (nonNull(th)) {
			if (th instanceof BizRuleRestrictRestfulException) {
				return ((BizRuleRestrictRestfulException) th).getCode();
			} else if (th instanceof InvalidParamsRestfulException) {
				return ((InvalidParamsRestfulException) th).getCode();
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
		return !isNull(resp) && retCode.getErrcode() == resp.getCode();
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
	 * HTTP response code definitions. </br>
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月10日
	 * @since
	 * @see <a href="https://www.ietf.org/rfc/rfc2616.txt">RFC1216</a>
	 * @see <a href=
	 *      "https://tools.ietf.org/html/rfc2324#section-2.3.2">RFC2314</a>
	 */
	public abstract static class RetCode {

		/**
		 * Successful code </br>
		 * {@link HttpStatus.OK}
		 */
		final public static RetCode OK = new RetCode(HttpStatus.OK.value(), "Ok") {
		};

		/**
		 * Parameter error </br>
		 * {@link HttpStatus.BAD_REQUEST}
		 */
		final public static RetCode PARAM_ERR = new RetCode(BAD_REQUEST.value(), "Bad parameters") {
		};

		/**
		 * Unauthenticated </br>
		 * {@link HttpStatus.UNAUTHORIZED}
		 */
		final public static RetCode UNAUTHC = new RetCode(UNAUTHORIZED.value(), "Unauthenticated") {
		};

		/**
		 * Unauthorized </br>
		 * {@link HttpStatus.FORBIDDEN}
		 */
		final public static RetCode UNAUTHZ = new RetCode(FORBIDDEN.value(), "Unauthorized") {
		};

		/**
		 * Not found </br>
		 * {@link HttpStatus.NOT_FOUND}
		 */
		final public static RetCode NOT_FOUND_ERR = new RetCode(NOT_FOUND.value(), "Not found") {
		};

		/**
		 * Business constraints </br>
		 * {@link HttpStatus.NOT_IMPLEMENTED}
		 */
		final public static RetCode BIZ_ERR = new RetCode(EXPECTATION_FAILED.value(), "Business restricted") {
		};

		/**
		 * Business locked constraints </br>
		 * {@link HttpStatus.LOCKED}
		 */
		final public static RetCode LOCKD_ERR = new RetCode(LOCKED.value(), "Resources locked") {
		};

		/**
		 * Precondition limited </br>
		 * {@link HttpStatus.PRECONDITION_FAILED}
		 */
		final public static RetCode PRECONDITITE_LIMITED = new RetCode(PRECONDITION_FAILED.value(), "Precondition limited") {
		};

		/**
		 * Unsuppported </br>
		 * {@link HttpStatus.NOT_IMPLEMENTED}
		 */
		final public static RetCode UNSUPPORTED = new RetCode(NOT_IMPLEMENTED.value(), "Unsuppported") {
		};

		/**
		 * System abnormality </br>
		 * {@link HttpStatus.SERVICE_UNAVAILABLE}
		 */
		final public static RetCode SYS_ERR = new RetCode(SERVICE_UNAVAILABLE.value(),
				"Service unavailable, please try again later") {
		};

		/**
		 * Unavailable For Legal Reasons </br>
		 * {@link HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS}
		 */
		final public static RetCode LEGAL_ERR = new RetCode(UNAVAILABLE_FOR_LEGAL_REASONS.value(),
				"Not available for legal reasons") {
		};

		/**
		 * Name to {@link RetCode} value definitions.
		 */
		final private static Map<String, RetCode> nameValueDefinition;

		/**
		 * Code to {@link RetCode} value definitions.
		 */
		final private static Map<Integer, RetCode> codeValueDefinition;

		/**
		 * Errors code.
		 */
		final private int errcode;

		/**
		 * Errors message.
		 */
		final private String errmsg;

		private RetCode(int code, String msg) {
			// hasText(msg, "Result message can't empty.");
			this.errcode = code;
			this.errmsg = msg;
		}

		/**
		 * Get error code.</br>
		 * If custom status code ({@link #_$$}) is used, it takes precedence.
		 * 
		 * @return
		 */
		final public int getErrcode() {
			return errcode;
		}

		/**
		 * Get error message.</br>
		 * If custom status error message ({@link #_$$}) is used, it takes
		 * precedence.
		 * 
		 * @return
		 */
		final public String getErrmsg() {
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
		final public static RetCode of(String nameOrCode) {
			RetCode retCode = safeOf(nameOrCode);
			if (nonNull(retCode)) {
				return retCode;
			}
			throw new IllegalArgumentException(format("'%s'", nameOrCode));
		}

		/**
		 * Safe convert retCode.
		 * 
		 * @param nameOrCode
		 * @return
		 */
		final public static RetCode safeOf(Object nameOrCode) {
			if (isNull(nameOrCode)) {
				return null;
			}
			RetCode retCode = nameValueDefinition.get(nameOrCode);
			if (isNull(retCode)) {
				return codeValueDefinition.get(nameOrCode);
			}
			return retCode;
		}

		/**
		 * New create custom status code, refer to {@link #_$$}
		 * 
		 * @param errcode
		 * @return
		 */
		final public static RetCode newCode(int errcode) {
			return new RetCode(errcode, null) {
			};
		}

		/**
		 * New create custom status code, refer to {@link #_$$}
		 * 
		 * @param errcode
		 * @param errmsg
		 * @return
		 */
		final public static RetCode newCode(int errcode, String errmsg) {
			return new RetCode(errcode, errmsg) {
			};
		}

		/**
		 * Get internal default instance definitions.
		 * 
		 * @return
		 */
		static {
			try {
				final Map<String, RetCode> nameValueMap = new HashMap<>();
				final Map<Integer, RetCode> codeValueMap = new HashMap<>();
				for (Field f : RetCode.class.getDeclaredFields()) {
					if (isStatic(f.getModifiers()) && isFinal(f.getModifiers()) && f.getType() == RetCode.class) {
						AccessController.doPrivileged(new PrivilegedAction<Void>() {
							@Override
							public Void run() {
								f.setAccessible(true);
								return null;
							}
						});

						Object fObj = f.get(null);
						if (fObj instanceof RetCode) {
							RetCode retCode = (RetCode) f.get(null);
							state(isNull(nameValueMap.putIfAbsent(f.getName(), retCode)), "");
							state(isNull(codeValueMap.putIfAbsent(retCode.getErrcode(), retCode)), "");
						}
					}
				}
				nameValueDefinition = unmodifiableMap(nameValueMap);
				codeValueDefinition = unmodifiableMap(codeValueMap);
			} catch (Exception ex) {
				throw new IllegalStateException("", ex);
			}
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
	final public static class ErrorPromptMessageBuilder {

		/** Errors code and message separator. */
		final private static String CODE_PROMPT_SEPAR = ": ";

		/**
		 * Errors prefix definition.
		 * 
		 * @see {@link com.wl4g.devops.common.web.RespBase#globalErrPrefix()}
		 */
		private static String errorPromptString = getProperty("spring.cloud.devops.global.respbase.error-prompt", "API");

		/**
		 * Building error message with prefix.
		 * 
		 * @param retCode
		 * @param errmsg
		 * @return
		 */
		final static String build(RetCode retCode, String errmsg) {
			if (isBlank(errmsg)) {
				return errmsg;
			}
			String prefixString = format("%s-%s%s", errorPromptString, retCode.getErrcode(), CODE_PROMPT_SEPAR);
			int index = errmsg.indexOf(CODE_PROMPT_SEPAR);
			if (index >= 0) {
				return (prefixString + errmsg.substring(index + CODE_PROMPT_SEPAR.length()));
			}
			return (prefixString + errmsg);
		}

		/**
		 * Setup global error message prefix.
		 * 
		 * @param errorPrompt
		 */
		final public synchronized static void setPrompt(String errorPrompt) {
			// hasText(errorPrompt, "Global error prompt can't be empty.");
			if (!isBlank(errorPrompt)) {
				errorPromptString = errorPrompt.replaceAll("-", "").toUpperCase(Locale.US);
			}
		}

	}

	/**
	 * Default status value.
	 */
	final public static String DEFAULT_STATUS = "Normal";

	/**
	 * Default requestId value.
	 */
	final public static String DEFAULT_REQUESTID = null;

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