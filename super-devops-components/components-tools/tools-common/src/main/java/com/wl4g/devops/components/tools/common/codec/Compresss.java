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
package com.wl4g.devops.components.tools.common.codec;

import java.io.IOException;

import org.xerial.snappy.Snappy;

/**
 * Compressions utility.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月5日
 * @since
 */
public abstract class Compresss {

	/**
	 * Using snappy compress.
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] snappyCompress(byte[] data) {
		try {
			return Snappy.compress(data);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Using snappy uncompress.
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] snappyUnCompress(byte[] data) {
		try {
			return Snappy.uncompress(data);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}