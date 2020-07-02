package com.wl4g.devops.erm;

import com.wl4g.devops.ErmManager;
import com.wl4g.devops.support.redis.JedisService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-04-09 11:36:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ErmManager.class)
@FixMethodOrder(MethodSorters.JVM)
public class JedisTests {

    @Autowired
    private JedisService jedisService;

    @Test
    public void mapPutTest() {
        Map map = new HashMap();
        map.put("@","{\"a\":\"test\"}");
        jedisService.setMap("_dns:example.com.",map,0);

    }


    @Test
    public void mapDelTest() {
        jedisService.mapRemove("_dns:example.com.","www");
    }





}
