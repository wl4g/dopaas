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
package com.wl4g.devops;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.log.SmartLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-07-08 11:46:00
 */
@RestController
@RequestMapping("test")
public class GatewayTestController {

    SmartLogger log = SmartLoggerFactory.getLogger(getClass());

    @Autowired
    HttpServletRequest request;

    @RequestMapping("hello")
    public String helloworld(){
        String token = request.getHeader("token");
        log.info("token={}",token);

        Map<String, String[]> parameterMap = request.getParameterMap();
        for(Map.Entry<String, String[]> entry : parameterMap.entrySet()){
            String mapKey = entry.getKey();
            String[] mapValue = entry.getValue();
            StringBuilder out = new StringBuilder();
            for(String s : mapValue){
                out.append(s);
            }
            log.info(mapKey+":"+out);
        }
        return "success";
    }
}