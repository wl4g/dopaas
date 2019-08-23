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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wl4g.devops.common.utils.lang.StringUtils2;

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
	final public static String DEFAULT_STATUS = "normal";

	private RetCode code;
	private String status; // Response status
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
		setStatus(StringUtils2.isEmpty(status) ? DEFAULT_STATUS : status);
		setMessage(message);
		setData(data);
	}

	public int getCode() {
		return code.getCode();
	}

	public RespBase<T> setCode(RetCode retCode) {
		this.code = retCode != null ? retCode : this.code;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public RespBase<T> setStatus(String status) {
		if (status != null) {
			this.status = status;
		}
		return this;
	}

	public String getMessage() {
		return message == null ? code.getMsg() : message;
	}

	public RespBase<T> setMessage(String message) {
		this.message = message != null ? message : this.message;
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
		return new RespBase<T>();
	}

	public static boolean isSuccess(RespBase<?> resp) {
		return resp != null && RetCode.OK.getCode() == resp.getCode();
	}

	public static boolean eq(RespBase<?> resp, RetCode retCode) {
		return resp != null && retCode.getCode() == resp.getCode();
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
		 * Successfully<br/>
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
		 * {@link HttpStatus.INTERNAL_SERVER_ERROR}
		 */
		SYS_ERR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Busy system, please try-again later");

		private int code;
		private String msg;

		private RetCode(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int getCode() {
			return code;
		}

		public String getMsg() {
			return msg;
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
		public static RetCode get(String nameOrCode) {
			String tmp = String.valueOf(nameOrCode);
			for (RetCode v : values()) {
				if (v.name().equalsIgnoreCase(tmp) || String.valueOf(v.getCode()).equalsIgnoreCase(tmp)) {
					return v;
				}
			}
			throw new IllegalArgumentException(String.format("'%s'", nameOrCode));
		}

	}

}