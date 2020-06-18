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

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * File Fingerprint codec utility tools.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-07-19 17:16:00
 */
public abstract class FingerprintUtils {

	/** Reader buffer size. */
	final public static int READ_BUF_SIZE = 1024 * 8;

	/**
	 * Read the file and calculate the binary SHA1 fingerprint.
	 * 
	 * @param file
	 * @return SHA1 string of file.
	 */
	public static String getSha1Fingerprint(File file) {
		try (FileInputStream in = new FileInputStream(file);) {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] buf = new byte[READ_BUF_SIZE];
			int len = 0;
			while ((len = in.read(buf)) > 0) {
				digest.update(buf, 0, len);
			}
			String sha1 = new BigInteger(1, digest.digest()).toString(16);
			int length = 40 - sha1.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					sha1 = "0" + sha1;
				}
			}
			return sha1;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Read the file and calculate the binary MD5 fingerprint.
	 * 
	 * @param file
	 * @return md5 string of file.
	 */
	public static String getMd5Fingerprint(File file) {
		try (FileInputStream in = new FileInputStream(file);) {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buf = new byte[READ_BUF_SIZE];
			int len = 0;
			while ((len = in.read(buf)) > 0) {
				digest.update(buf, 0, len);
			}
			String md5 = new BigInteger(1, digest.digest()).toString(16);
			int length = 32 - md5.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					md5 = "0" + md5;
				}
			}
			return md5;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}