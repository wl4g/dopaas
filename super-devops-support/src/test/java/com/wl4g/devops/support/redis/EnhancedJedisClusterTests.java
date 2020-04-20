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
package com.wl4g.devops.support.redis;

import static java.util.Arrays.asList;

import com.wl4g.devops.support.redis.EnhancedJedisCluster.RedisFormatUtils;

public class EnhancedJedisClusterTests {

	public static void main(String[] args) {
		System.out.println("-----11-----");
		RedisFormatUtils.checkArgumentsSpecification(asList("safecloud_support_appinfo_admin"));

		System.out.println("-----22-----");
		RedisFormatUtils.checkArgumentsSpecification(asList("3342701404111872&&800492841ab644dc8ea01c683a809255BELONGANNUPXIN"));

		try {
			System.out.println("-----33-----");
			RedisFormatUtils
					.checkArgumentsSpecification(asList("3342701404111872-800492841ab644dc8ea01c683a809255BELONGANNUPXIN"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("-----44-----");
		System.out.println(RedisFormatUtils.keyFormat("3342701404111872-800492841ab644dc8ea01c683a809255BELONGANNUPXIN"));

	}

}
