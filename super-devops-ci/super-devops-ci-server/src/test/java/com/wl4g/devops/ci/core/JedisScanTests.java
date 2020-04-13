package com.wl4g.devops.ci.core;

import com.wl4g.devops.CiServer;
import com.wl4g.devops.ci.bean.RunModel;
import com.wl4g.devops.support.redis.JedisService;
import com.wl4g.devops.support.redis.ScanCursor;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.wl4g.devops.ci.flow.FlowManager.REDIS_CI_RUN_PRE;
import static com.wl4g.devops.ci.flow.FlowManager.REDIS_CI_RUN_SCAN_BATCH;

/**
 * @author vjay
 * @date 2020-04-09 11:36:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CiServer.class)
@FixMethodOrder(MethodSorters.JVM)
public class JedisScanTests {

    @Autowired
    private JedisService jedisService;

    @Test
    public void setObjectTTest() {
        ScanCursor<RunModel> scan = jedisService.scan(REDIS_CI_RUN_PRE+"*", REDIS_CI_RUN_SCAN_BATCH, RunModel.class);

        while (scan.hasNext()){
            RunModel next = scan.next();
            System.out.println(next);
        }
        System.out.println("finish");

    }




}
