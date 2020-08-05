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
package com.wl4g.devops.umc.timing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.components.core.bean.umc.CustomEngine;

/**
 * @author vjay
 * @date 2020-04-03 16:45:00
 */
public class DemoEngine {

    @Autowired
    private CodeExecutor codeExecutor;


    public void executeCode(JdbcTemplate jdbcTemplate, CustomEngine customEngine) {


        //TODO this code will get from db
        String sql = "select count(1) from sys_dict";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        if (count > 10) {
            //...
            codeExecutor.saveAlarmEvent(customEngine, ".......");
        } else {
            //...
        }


    }


}