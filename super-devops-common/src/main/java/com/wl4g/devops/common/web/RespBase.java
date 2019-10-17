/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wl4g.devops.common.exception.restful.BizInvalidArgRestfulException;
import com.wl4g.devops.common.exception.restful.BizRuleRestrictRestfulException;
import com.wl4g.devops.common.exception.restful.ServiceUnavailableRestfulException;

/**
 * Generic Restful response base class
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年3月9日
 * @since
 */
public class RespBase<T extends Object> implements Serializable {
	final private static long serialVersionUID = 2647155468624590650L;

	/**
	 * Default status value.
	 */
	final public transient static String DEFAULT_STATUS = "normal";

	private RetCode code;
	private String status; // Response status.
	private String message;
	private DataMap<T> data = new DataMap<>();

	public RespBase() {
		this(RetCode.OK);
	}

	public RespBase(RetCode retCode) {
		this(retCode, null);
	}

	public RespBase(DataMap<T> data) {
		this(null, data);
	}

	public RespBase(RetCode retCode, DataMap<T> data) {
		this(retCode, null, data);
	}

	public RespBase(RetCode retCode, String message, DataMap<T> data) {
		this(retCode, null, message, data);
	}

	public RespBase(RetCode retCode, String status, String message, DataMap<T> data) {
		setCode(retCode);
		setStatus(isBlank(status) ? DEFAULT_STATUS : status);
		setMessage(message);
		setData(data);
	}

	public int getCode() {
		return code.getErrcode();
	}

	public RespBase<T> setCode(RetCode retCode) {
		this.code = retCode != null ? retCode : this.code;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public RespBase<T> setStatus(String status) {
		if (!isBlank(status)) {
			this.status = status;
		}
		return this;
	}

	public String getMessage() {
		return message == null ? code.getErrmsg() : message;
	}

	public RespBase<T> setMessage(String message) {
		this.message = !isBlank(message) ? message : this.message;
		return this;
	}

	/**
	 * Setting exception messages only does not set response status code.
	 * 
	 * @param th
	 * @return
	 */
	public RespBase<T> setThrowable(Throwable th) {
		this.message = getRootCausesString(th);
		return this;
	}

	/**
	 * Handle API exceptions, setting exception messages and corresponding
	 * response status code.
	 * 
	 * @param th
	 * @return
	 */
	public RespBase<T> handleError(Throwable th) {
		this.message = getRootCausesString(th);
		if (th instanceof BizRuleRestrictRestfulException) {
			this.code = ((BizRuleRestrictRestfulException) th).getCode();
		} else if (th instanceof BizInvalidArgRestfulException) {
			this.code = ((BizInvalidArgRestfulException) th).getCode();
		} else if (th instanceof ServiceUnavailableRestfulException) {
			this.code = ((ServiceUnavailableRestfulException) th).getCode();
		}
		return this;
	}

	public DataMap<T> getData() {
		return data;
	}

	public RespBase<T> setData(Map<String, T> data) {
		if (!isEmpty(data)) {
			this.data.putAll(data);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public DataMap<Object> build(String name) {
		Assert.hasText(name, "Build datamap name must not be empty");
		DataMap<Object> node = new DataMap<>();
		getData().put(name, (T) node);
		return node;
	}

	@Override
	public String toString() {
		return "{" + (code != null ? "code=" + code + ", " : "") + (message != null ? "message=" + message + ", " : "")
				+ (data != null ? "data=" + data : "") + "}";
	}

	public static <T> RespBase<T> create() {
		return create(null);
	}

	public static <T> RespBase<T> create(String status) {
		return new RespBase<T>().setStatus(status);
	}

	public static boolean isSuccess(RespBase<?> resp) {
		return resp != null && RetCode.OK.getErrcode() == resp.getCode();
	}

	public static boolean eq(RespBase<?> resp, RetCode retCode) {
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
	public static class DataMap<V> extends LinkedHashMap<String, V> {
		private static final long serialVersionUID = 741193108777950437L;

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
	 * Response code definition
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
		SECOND_UNAUTH(HttpStatus.PRECONDITION_FAILED.value(), "Second Uncertified"),

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
		 * Custom code type definition.
		 */
		$CUSTOM$(-1, "Unknown error");

		/**
		 * Create custom status code, refer to {@link #$CUSTOM$} and
		 * {@link #create(int, String)}
		 */
		final private static ThreadLocal<Object[]> customStore = new InheritableThreadLocal<>();

		private int errcode;
		private String errmsg;

		private RetCode(int code, String msg) {
			Assert.hasText(msg, "Result message definition must not be empty.");
			this.errcode = code;
			this.errmsg = msg;
		}

		/**
		 * Get error code.</br>
		 * If custom status code ({@link #$CUSTOM$}) is used, it takes
		 * precedence.
		 * 
		 * @return
		 */
		public int getErrcode() {
			if (this == $CUSTOM$) {
				Object errcode = customStore.get()[0];
				Assert.notNull(errcode, "");
				return (int) errcode;
			}
			return errcode;
		}

		/**
		 * Get error message.</br>
		 * If custom status error message ({@link #$CUSTOM$}) is used, it takes
		 * precedence.
		 * 
		 * @return
		 */
		public String getErrmsg() {
			if (this == $CUSTOM$) {
				Object errmsg = customStore.get()[1];
				Assert.notNull(errmsg, "");
				return (String) errmsg;
			}
			return errmsg;
		}

		/**
		 * JACKSON映射枚举大小写不敏感处理
		 * 
		 * https://www.cnblogs.com/chyu/p/9177140.htmlhttps://www.cnblogs.com/chyu/p/9177140.html
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
		 * Create custom status code, refer to {@link #$CUSTOM$}
		 * 
		 * @param errcode
		 * @param errmsg
		 * @return
		 */
		final public static RetCode create(int errcode, String errmsg) {
			Assert.hasText(errmsg, "Result errmsg definition must not be empty.");
			customStore.set(new Object[] { errcode, errmsg });
			return $CUSTOM$;
		}

	}

}