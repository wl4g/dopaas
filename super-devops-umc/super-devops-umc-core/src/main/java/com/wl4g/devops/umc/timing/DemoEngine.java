package com.wl4g.devops.umc.timing;

import com.wl4g.devops.common.bean.umc.CustomEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author vjay
 * @date 2020-04-03 16:45:00
 */
public class DemoEngine {

    @Autowired
    private CodeExecutor codeExecutor;


    public void executeCode(JdbcTemplate jdbcTemplate, CustomEngine customEngine){


            //TODO this code will get from db
            Integer count = jdbcTemplate.queryForObject("select count(1) from sys_dict", Integer.class);
            if(count>10){
                //...
                codeExecutor.saveAlarmEvent(customEngine,".......");
            }else{
                //...
            }



    }


}
