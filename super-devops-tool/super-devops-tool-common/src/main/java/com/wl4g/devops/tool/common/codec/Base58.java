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
package com.wl4g.devops.tool.common.codec;

import java.io.UnsupportedEncodingException;

/**
 * Base58 codec utility. {@link Base58}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月25日 v1.0.0
 * @see
 */
public abstract class Base58 {

	/**
	 * Encodes the given bytes in base58. No checksum is appended.
	 * 
	 * @param input
	 * @return
	 */
	public static String encode(byte[] input) {
		if (input.length == 0) {
			return "";
		}
		input = copyOfRange(input, 0, input.length);
		// Count leading zeroes.
		int zeroCount = 0;
		while (zeroCount < input.length && input[zeroCount] == 0) {
			++zeroCount;
		}
		// The actual encoding.
		byte[] temp = new byte[input.length * 2];
		int j = temp.length;

		int startAt = zeroCount;
		while (startAt < input.length) {
			byte mod = divmod58(input, startAt);
			if (input[startAt] == 0) {
				++startAt;
			}
			temp[--j] = (byte) ALPHABET[mod];
		}

		// Strip extra '1' if there are some after decoding.
		while (j < temp.length && temp[j] == ALPHABET[0]) {
			++j;
		}
		// Add as many leading '1' as there were leading zeros.
		while (--zeroCount >= 0) {
			temp[--j] = (byte) ALPHABET[0];
		}

		byte[] output = copyOfRange(temp, j, temp.length);
		try {
			return new String(output, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e); // Cannot happen.
		}
	}

	/**
	 * Decode to base58 string.
	 * 
	 * @param input
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static byte[] decode(String input) throws IllegalArgumentException {
		if (input.length() == 0) {
			return new byte[0];
		}
		byte[] input58 = new byte[input.length()];
		// Transform the String to a base58 byte sequence
		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			int digit58 = -1;
			if (c >= 0 && c < 128) {
				digit58 = INDEXES[c];
			}
			if (digit58 < 0) {
				throw new IllegalArgumentException("Illegal character " + c + " at " + i);
			}
			input58[i] = (byte) digit58;
		}
		// Count leading zeroes
		int zeroCount = 0;
		while (zeroCount < input58.length && input58[zeroCount] == 0) {
			++zeroCount;
		}
		// The encoding
		byte[] temp = new byte[input.length()];
		int j = temp.length;

		int startAt = zeroCount;
		while (startAt < input58.length) {
			byte mod = divmod256(input58, startAt);
			if (input58[startAt] == 0) {
				++startAt;
			}

			temp[--j] = mod;
		}
		// Do no add extra leading zeroes, move j to first non null byte.
		while (j < temp.length && temp[j] == 0) {
			++j;
		}

		return copyOfRange(temp, j - zeroCount, temp.length);
	}

	/**
	 * number -> number / 58, returns number % 58
	 * 
	 * @param number
	 * @param startAt
	 * @return
	 */
	private static byte divmod58(byte[] number, int startAt) {
		int remainder = 0;
		for (int i = startAt; i < number.length; i++) {
			int digit256 = (int) number[i] & 0xFF;
			int temp = remainder * 256 + digit256;

			number[i] = (byte) (temp / 58);

			remainder = temp % 58;
		}

		return (byte) remainder;
	}

	/**
	 * number -> number / 256, returns number % 256
	 * 
	 * @param number58
	 * @param startAt
	 * @return
	 */
	private static byte divmod256(byte[] number58, int startAt) {
		int remainder = 0;
		for (int i = startAt; i < number58.length; i++) {
			int digit58 = (int) number58[i] & 0xFF;
			int temp = remainder * 58 + digit58;

			number58[i] = (byte) (temp / 256);

			remainder = temp % 256;
		}

		return (byte) remainder;
	}

	/**
	 * copyOfRange
	 * 
	 * @param source
	 * @param from
	 * @param to
	 * @return
	 */
	private static byte[] copyOfRange(byte[] source, int from, int to) {
		byte[] range = new byte[to - from];
		System.arraycopy(source, from, range, 0, range.length);

		return range;
	}

	final private static char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
	final private static int[] INDEXES = new int[128];

	static {
		for (int i = 0; i < INDEXES.length; i++) {
			INDEXES[i] = -1;
		}
		for (int i = 0; i < ALPHABET.length; i++) {
			INDEXES[ALPHABET[i]] = i;
		}
	}

}