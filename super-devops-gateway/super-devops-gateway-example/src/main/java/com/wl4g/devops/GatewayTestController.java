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
