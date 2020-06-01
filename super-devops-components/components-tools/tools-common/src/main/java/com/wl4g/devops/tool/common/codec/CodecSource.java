/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.wl4g.devops.tool.common.codec;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * Codec cryptical hash IO data source.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月8日
 * @since
 */
public class CodecSource implements Serializable {
	private static final long serialVersionUID = 9127527471762896518L;

	/**
	 * Origin data bytes.
	 */
	final byte[] bytes;

	/**
	 * Transient cache hex string.
	 */
	private transient String cachedHex;

	/**
	 * Transient cache base64 string.
	 */
	private transient String cachedBase64;

	/**
	 * Transient cache base58 string.
	 */
	private transient String cachedBase58;

	public CodecSource(final byte[] plainArray) {
		notNullOf(plainArray, "plainArray");
		this.bytes = plainArray;
	}

	/**
	 * Creates an instance by converting the characters to a byte array (assumes
	 * UTF-8 encoding).
	 *
	 * @param plainChars
	 *            the source characters to use to create the underlying byte
	 *            array.
	 * @since 1.1
	 */
	public CodecSource(final char[] plainChars) {
		notNullOf(plainChars, "plainChars");
		this.bytes = new String(plainChars).getBytes(UTF_8);
	}

	/**
	 * Creates an instance by converting the String to a byte array (assumes
	 * UTF-8 encoding).
	 *
	 * @param plaintext
	 *            the source string to convert to a byte array (assumes UTF-8
	 *            encoding).
	 * @since 1.1
	 */
	public CodecSource(final String plaintext) {
		hasTextOf(plaintext, "plaintext");
		this.bytes = plaintext.getBytes(UTF_8);
	}

	public byte[] getBytes() {
		return bytes;
	}

	public boolean isEmpty() {
		return isNull(bytes) || bytes.length == 0;
	}

	public synchronized String toHex() {
		if (isNull(cachedHex)) {
			cachedHex = Hex.encodeHexString(getBytes());
		}
		return cachedHex;
	}

	public synchronized String toBase64() {
		if (isNull(cachedBase64)) {
			cachedBase64 = Base64.encodeBase64String(getBytes());
		}
		return cachedBase64;
	}

	public synchronized String toBase58() {
		if (isNull(cachedBase58)) {
			cachedBase58 = Base58.encode(getBytes());
		}
		return cachedBase58;
	}

	public boolean equals(final CodecSource o) {
		if (o == this) {
			return true;
		}
		return Arrays.equals(getBytes(), o.getBytes());
	}

	public int hashCode() {
		if (isNull(bytes) || bytes.length == 0) {
			return 0;
		}
		return Arrays.hashCode(bytes);
	}

	public String toString() {
		return new String(getBytes(), UTF_8);
	}

	/**
	 * Create {@link CodecSource} with hex chars.
	 * 
	 * @param hexString
	 * @return
	 */
	public static CodecSource fromHex(final String hexString) {
		try {
			return new CodecSource(Hex.decodeHex(hexString.toCharArray()));
		} catch (DecoderException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Create {@link CodecSource} with hex bytes.
	 * 
	 * @return
	 */
	public static CodecSource fromHex(final byte[] hexArray) {
		try {
			return new CodecSource(Hex.decodeHex(new String(hexArray, UTF_8).toCharArray()));
		} catch (DecoderException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Create {@link CodecSource} with base64 string.
	 * 
	 * @param base64
	 * @return
	 */
	public static CodecSource fromBase64(final String base64) {
		return new CodecSource(Base64.decodeBase64(base64));
	}

	/**
	 * Create {@link CodecSource} with base64 byte array.
	 * 
	 * @param base64Array
	 * @return
	 */
	public static CodecSource fromBase64(final byte[] base64Array) {
		return new CodecSource(Base64.decodeBase64(base64Array));
	}

	/**
	 * Create {@link CodecSource} with base58 string.
	 * 
	 * @param base58
	 * @return
	 */
	public static CodecSource fromBase58(final String base58) {
		return new CodecSource(Base58.decode(base58));
	}

	/**
	 * Create {@link CodecSource} with base58 byte array.
	 * 
	 * @param base58Array
	 * @return
	 */
	public static CodecSource fromBase58(final byte[] base58Array) {
		return new CodecSource(Base58.decode(new String(base58Array, UTF_8)));
	}

}
