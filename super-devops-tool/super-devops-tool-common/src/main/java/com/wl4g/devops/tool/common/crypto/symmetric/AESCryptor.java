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
package com.wl4g.devops.tool.common.crypto.symmetric;

/**
 * <p>
 * <a href=
 * "http://blog.csdn.net/qq_26188423/article/details/60579773">AES加密算法（解决windows/Linux下加解密不一致问题</a>
 * </p>
 * 
 * <p>
 * <a href="http://tool.chacuo.net/cryptaes">在线加解密工具</a>
 * </p>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年3月15日
 * @since
 */
public class AESCryptor extends AbstractSymmetricCryptor {

	@Override
	public String getAlgorithmPrimary() {
		return "AES";
	}

	/**
	 * 算法模式： ECB（Electronic Code Book，电子密码本）模式</br>
	 * CBC（Cipher Block Chaining，加密块链）模式</br>
	 * CFB（Cipher FeedBack Mode，加密反馈）模式</br>
	 * OFB（Output FeedBack，输出反馈）模式</br>
	 * 补码方式：</br>
	 * AES/CBC/NoPadding (128)</br>
	 * AES/CBC/PKCS5Padding (128)</br>
	 * AES/ECB/NoPadding (128) </br>
	 * AES/ECB/PKCS5Padding (128)</br>
	 * DES/CBC/NoPadding (56)</br>
	 * DES/CBC/PKCS5Padding (56) </br>
	 * DES/ECB/NoPadding (56) </br>
	 * DES/ECB/PKCS5Padding (56)</br>
	 * DESede/CBC/NoPadding (168) </br>
	 * DESede/CBC/PKCS5Padding (168)</br>
	 * DESede/ECB/NoPadding (168)</br>
	 * DESede/ECB/PKCS5Padding (168) </br>
	 * RSA/ECB/PKCS1Padding (1024, 2048) </br>
	 * RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048) </br>
	 * RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)</br>
	 * 参考：https://zhidao.baidu.com/question/1765750919758817420.html</br>
	 * http://blog.csdn.net/qq_16371729/article/details/50015481</br>
	 * http://blog.csdn.net/qq_35973977/article/details/77711669</br>
	 */
	@Override
	public String getPadAlgorithm() {
		return "AES/ECB/PKCS5Padding"; // 默认
	}

	@Override
	public int getKeyBit() {
		return 128;
	}

}