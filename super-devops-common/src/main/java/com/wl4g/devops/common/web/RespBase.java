/*
 * Copyright 2015 the original author or authors.
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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wl4g.devops.common.utils.StringUtils2;

/**
 * Generic Restful response base class
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年3月9日
 * @since
 */
public class RespBase<T> implements Serializable {
	final private static long serialVersionUID = 2647155468624590650L;

	/**
	 * Default status value.
	 */
	final public static String DEFAULT_STATUS = "normal";

	private RetCode code;
	private String status; // Response status
	private String message;
	private Map<String, T> data = new LinkedHashMap<>(4);

	public RespBase() {
		this(RetCode.OK);
	}

	public RespBase(RetCode retCode) {
		this(retCode, null);
	}

	public RespBase(Map<String, T> data) {
		this(null, data);
	}

	public RespBase(RetCode retCode, Map<String, T> data) {
		this(retCode, null, data);
	}

	public RespBase(RetCode retCode, String message, Map<String, T> data) {
		this(retCode, null, message, data);
	}

	public RespBase(RetCode retCode, String status, String message, Map<String, T> data) {
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

	public Map<String, T> getData() {
		return data;
	}

	public RespBase<T> setData(Map<String, T> data) {
		data = data != null ? data : this.data;
		if (this.data != null) {
			this.data.putAll(data);
		}
		this.data = data;
		return this;
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