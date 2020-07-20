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
package com.wl4g.devops.ci.core;

import com.wl4g.devops.CiServer;
import com.wl4g.devops.ci.bean.RunModel;
import com.wl4g.devops.support.redis.jedis.JedisService;
import com.wl4g.devops.support.redis.jedis.ScanCursor;

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