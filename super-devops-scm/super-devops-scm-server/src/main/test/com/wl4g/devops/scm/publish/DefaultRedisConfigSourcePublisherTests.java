package com.wl4g.devops.scm.publish;

import com.wl4g.devops.ScmServer;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.support.redis.JedisService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-04-09 11:36:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScmServer.class)
@FixMethodOrder(MethodSorters.JVM)
public class DefaultRedisConfigSourcePublisherTests {

    @Autowired
    private JedisService jedisServicel;
    @Test
    public void setObjectTTest(){
        DefaultRedisConfigSourcePublisher.PublishConfigWrapper wrapper = new DefaultRedisConfigSourcePublisher.PublishConfigWrapper();
        wrapper.setCluster("scm-example");
        PreRelease preRelease = new PreRelease();
        preRelease.setCluster("abc");

        User user = new User();
        user.setName("123");

        Map map = new HashMap();
        map.put("a","qwertyuiopsghjklzvbnm");
        map.put("b",1);


        String s = jedisServicel.setObjectT("scm_pub_config_scm-example", user, 0);
        System.out.println(s);
    }

    //@Test
    public void setObjectAsJsonTest(){
        DefaultRedisConfigSourcePublisher.PublishConfigWrapper wrapper = new DefaultRedisConfigSourcePublisher.PublishConfigWrapper();
        wrapper.setCluster("scm-example");
        jedisServicel.setObjectAsJson("scm_pub_config_scm-example",wrapper,0);
    }

    public static class User implements Serializable {
        private static final long serialVersionUID = 381411777614066880L;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }



}
