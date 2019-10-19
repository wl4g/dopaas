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
package com.wl4g.devops.srm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-09-12 11:01:00
 */
@RestController
public class LogPrintfExampleController implements ApplicationRunner  {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> f = args.getOptionValues("f");
        System.out.println("fq="+f);
        long fq = Integer.valueOf(f.get(0));
        int i = 0;
        while (true) {
            logger.warn("Processing. addr=11111119, order={}", i);
            logger.debug("Processing. addr=11111119, order={}", i);
            logger.info("Processing. addr=11111119, order={}", i);
            logger.error("Processing. addr=11111119, order={}", i);
            try {
                Thread.sleep(fq);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}