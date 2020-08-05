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
package com.wl4g.devops.erm;

import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.ErmManager;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link JedisDnsZoneStoreTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2020-04-09 11:36:00
 * @see
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ErmManager.class)
@FixMethodOrder(MethodSorters.JVM)
public class JedisDnsZoneStoreTests {

	@Autowired
	private JedisService jedisService;

	@Test
	public void mapPutTest() {
		Map<String, String> map = new HashMap<>();
		map.put("@", "{\"a\":\"example\"}");
		jedisService.setMap("coredns:dns:example.com.", map, 0);
	}

	@Test
	public void mapDelTest() {
		jedisService.mapRemove("coredns:dns:example.com.", "www");
	}

	@Test
	public void addListTest() {
		Set<String> set = new HashSet<>();
		set.add("cde");
		set.add("fgh");
		jedisService.setSet("coredns:dns:blockList", set, 0);
	}

	@Test
	public void delListTest() {
		jedisService.delSetMember("blockListTest", "abc");
	}

}