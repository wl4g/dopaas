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
package com.wl4g.devops.common.utils.lang;

/**
 * 类型字节转换器
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2016年9月26日
 * @since
 */
public class TypeConverts {

	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_LOW = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 字节数组转换成整型。(网络字节序，高字节在前)
	 * 
	 * @param b
	 *            字节数组。
	 * @param offset
	 *            待转换字节开始的位置。
	 * @return 整数形式。
	 */
	public static int byte2int(byte[] b, int offset) {
		return (b[offset + 3] & 0xff) | ((b[offset + 2] & 0xff) << 8) | ((b[offset + 1] & 0xff) << 16)
				| ((b[offset] & 0xff) << 24);
	}

	/**
	 * 字节数组转换成整型。(网络字节序，高字节在前)
	 * 
	 * @param b
	 *            字节数组。
	 * @return 整数形式。
	 */
	public static int byte2int(byte[] b) {
		return (b[3] & 0xff) | ((b[2] & 0xff) << 8) | ((b[1] & 0xff) << 16) | ((b[0] & 0xff) << 24);
	}

	/**
	 * 字节数组转换成长整型。(网络字节序，高字节在前)
	 * 
	 * @param b
	 *            字节数组。
	 * @return 长整数形式。
	 */
	public static long byte2long(byte[] b) {
		return ((long) b[7] & 0xff) | (((long) b[6] & 0xff) << 8) | (((long) b[5] & 0xff) << 16) | (((long) b[4] & 0xff) << 24)
				| (((long) b[3] & 0xff) << 32) | (((long) b[2] & 0xff) << 40) | (((long) b[1] & 0xff) << 48)
				| ((long) b[0] << 56);
	}

	/**
	 * 字节数组转换成长整型。(网络字节序，高字节在前)
	 * 
	 * @param b
	 *            字节数组。
	 * @return 长整数形式。
	 */
	public static long byte2long(byte[] b, int offset) {
		return ((long) b[offset + 7] & 0xff) | (((long) b[offset + 6] & 0xff) << 8) | (((long) b[offset + 5] & 0xff) << 16)
				| (((long) b[offset + 4] & 0xff) << 24) | (((long) b[offset + 3] & 0xff) << 32)
				| (((long) b[offset + 2] & 0xff) << 40) | (((long) b[offset + 1] & 0xff) << 48) | ((long) b[offset] << 56);
	}

	/**
	 * 整型转换成字节。(网络字节序，高字节在前)
	 * 
	 * @param n
	 *            整数。
	 * @return 长度为4的字节数组。
	 */
	public static byte[] int2byte(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n >> 24);
		b[1] = (byte) (n >> 16);
		b[2] = (byte) (n >> 8);
		b[3] = (byte) n;
		return b;
	}

	/**
	 * 整型转换成字节。(网络字节序，高字节在前)
	 * 
	 * @param n
	 *            整数。
	 * @param buf
	 *            存放转换结果的字节数组。
	 * @param offset
	 *            存放位置的偏移地址。
	 */
	public static void int2byte(int n, byte[] buf, int offset) {
		buf[offset] = (byte) (n >> 24);
		buf[offset + 1] = (byte) (n >> 16);
		buf[offset + 2] = (byte) (n >> 8);
		buf[offset + 3] = (byte) n;
	}

	/**
	 * 短整型转换成字节。(网络字节序，高字节在前)
	 * 
	 * @param n
	 *            整数。
	 * @return 长度为4的字节数组。
	 */
	public static byte[] short2byte(int n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n >> 8);
		b[1] = (byte) n;
		return b;
	}

	/**
	 * 短整型转换成字节。(网络字节序，高字节在前)
	 * 
	 * @param n
	 *            整数。
	 * @param buf
	 *            存放转换结果的字节数组。
	 * @param offset
	 *            存放位置的偏移地址。
	 */
	public static void short2byte(int n, byte[] buf, int offset) {
		buf[offset] = (byte) (n >> 8);
		buf[offset + 1] = (byte) n;
	}

	/**
	 * 长整型转换成字节。(网络字节序，高字节在前)
	 * 
	 * @param n
	 *            长整数。
	 * @return 长度为8的字节数组。
	 */
	public static byte[] long2byte(long n) {
		byte[] b = new byte[8];
		// b[0]=(byte)(n>>57); // comment by edong 20011203
		b[0] = (byte) (n >> 56);
		b[1] = (byte) (n >> 48);
		b[2] = (byte) (n >> 40);
		b[3] = (byte) (n >> 32);
		b[4] = (byte) (n >> 24);
		b[5] = (byte) (n >> 16);
		b[6] = (byte) (n >> 8);
		b[7] = (byte) n;
		return b;
	}

	/**
	 * 长整型转换成字节。(网络字节序，高字节在前)
	 * 
	 * @param n
	 *            长整数。
	 * @param buf
	 *            存放转换结果的字节数组。
	 * @param offset
	 *            存放位置的偏移地址。
	 */
	public static void long2byte(long n, byte[] buf, int offset) {
		// buf[offset]=(byte)(n>>57); // comment by edong 20011203
		buf[offset] = (byte) (n >> 56);
		buf[offset + 1] = (byte) (n >> 48);
		buf[offset + 2] = (byte) (n >> 40);
		buf[offset + 3] = (byte) (n >> 32);
		buf[offset + 4] = (byte) (n >> 24);
		buf[offset + 5] = (byte) (n >> 16);
		buf[offset + 6] = (byte) (n >> 8);
		buf[offset + 7] = (byte) n;
	}

	/**
	 * Converts an array of bytes into an array of characters representing the
	 * hexadecimal values of each byte in order. The returned array will be
	 * double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @return A char[] containing hexadecimal characters
	 * @since 1.4
	 */
	public static String encodeHex(final byte[] data) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS_LOW[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS_LOW[0x0F & data[i]];
		}
		return new String(out);
	}

}