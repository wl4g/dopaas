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
package com.wl4g.devops.common.utils.codec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author vjay
 * @date 2019-07-19 17:16:00
 */
public class FileCodec {

	public static String getFileSha1(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] buffer = new byte[1024 * 1024 * 10];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				digest.update(buffer, 0, len);
			}
			String sha1 = new BigInteger(1, digest.digest()).toString(16);
			int length = 40 - sha1.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					sha1 = "0" + sha1;
				}
			}
			return sha1;
		} catch (IOException e) {
			System.out.println(e);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return null;
	}

	public static String getFileMD5(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024 * 1024 * 10];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				digest.update(buffer, 0, len);
			}
			String md5 = new BigInteger(1, digest.digest()).toString(16);
			int length = 32 - md5.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					md5 = "0" + md5;
				}
			}
			return md5;
		} catch (IOException e) {
			System.out.println(e);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return null;
	}

}