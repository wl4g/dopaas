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
