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
package com.wl4g.devops.iam.captcha.gif;

import java.util.Random;

/**
 * <p>
 * 随机工具类
 * </p>
 *
 * @author: wuhongjun
 * @version:1.0
 */
class Randoms {

	private static final Random RANDOM = new Random();

	// 定义验证码字符.去除了O和I等容易混淆的字母
	public static final char ALPHA[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'G', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 产生两个数之间的随机数
	 * 
	 * @param min
	 *            小数
	 * @param max
	 *            比min大的数
	 * @return int 随机数字
	 */
	public static int num(int min, int max) {
		return min + RANDOM.nextInt(max - min);
	}

	/**
	 * 产生0--num的随机数,不包括num
	 * 
	 * @param num
	 *            数字
	 * @return int 随机数字
	 */
	public static int num(int num) {
		return RANDOM.nextInt(num);
	}

	public static char alpha() {
		return ALPHA[num(0, ALPHA.length)];
	}
}