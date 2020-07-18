package com.wl4g.devops.erm;

import com.wl4g.devops.ErmManager;
import com.wl4g.devops.support.redis.jedis.JedisService;

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
