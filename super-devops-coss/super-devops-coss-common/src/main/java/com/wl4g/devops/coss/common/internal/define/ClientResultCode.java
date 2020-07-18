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
package com.wl4g.devops.coss.common.internal.define;

/**
 * {@link ClientResultCode}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public interface ClientResultCode {

	/**
	 * Unknown error. This means the error is not expected.
	 */
	final public static String UNKNOWN = "Unknown";

	/**
	 * Unknown host. This error is returned when a
	 * {@link java.net.UnknownHostException} is thrown.
	 */
	final public static String UNKNOWN_HOST = "UnknownHost";

	/**
	 * connection times out.
	 */
	final public static String CONNECTION_TIMEOUT = "ConnectionTimeout";

	/**
	 * Socket times out
	 */
	final public static String SOCKET_TIMEOUT = "SocketTimeout";

	/**
	 * Socket exception
	 */
	final public static String SOCKET_EXCEPTION = "SocketException";

	/**
	 * Connection is refused by server side.
	 */
	final public static String CONNECTION_REFUSED = "ConnectionRefused";

	/**
	 * The input stream is not repeatable for reading.
	 */
	final public static String NONREPEATABLE_REQUEST = "NonRepeatableRequest";

}