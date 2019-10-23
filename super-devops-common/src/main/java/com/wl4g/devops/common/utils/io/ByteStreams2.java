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
package com.wl4g.devops.common.utils.io;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

/**
 * Byte stream utility. </br>
 * {@link ByteStreams}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月23日
 * @since
 */
public abstract class ByteStreams2 {

	/**
	 * Read all data from the input stream and turn it into a string. Note: the
	 * stream will not be closed after reading. Warning: the data size should be
	 * estimated before use. If the data volume is too large, there may be a
	 * risk of memory leakage.
	 * 
	 * @param in
	 * @return
	 */
	public static String unsafeReadFullyToString(InputStream in) {
		return unsafeReadFullyToString(in, "UTF-8");
	}

	/**
	 * Read all data from the input stream and turn it into a string. Note: the
	 * stream will not be closed after reading. Warning: the data size should be
	 * estimated before use. If the data volume is too large, there may be a
	 * risk of memory leakage.
	 * 
	 * @param in
	 * @param charset
	 * @return
	 * @throws IllegalStateException
	 */
	public static String unsafeReadFullyToString(InputStream in, String charset) throws IllegalStateException {
		try {
			byte[] buf = new byte[in.available()];
			ByteStreams.readFully(in, buf);
			return new String(buf, charset);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
