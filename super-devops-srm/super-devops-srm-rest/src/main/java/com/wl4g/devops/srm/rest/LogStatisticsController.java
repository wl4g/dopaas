/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.srm.rest;

import com.wl4g.devops.common.bean.srm.RequestBean;
import com.wl4g.devops.srm.service.LogStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class LogStatisticsController {
    @Autowired
    LogStatisticsService logStatisticsService;

    private final static Logger logger = LoggerFactory.getLogger(LogStatisticsController.class);

    @RequestMapping("/statisticsLog")
    @ResponseBody
    public Object statisticsLog(@RequestBody RequestBean requestBean) throws Exception{
        Map<String,Object> parm = new HashMap<>();
        try {
            Object result = logStatisticsService.statisticsLog(requestBean);
            parm.put("code",200);
            parm.put("data",result);
        } catch (Exception e) {
            parm.put("code",201);
            parm.put("message","调用接口异常");
            logger.info("requestBean:{}",requestBean);
        }
        return parm;
    }
}