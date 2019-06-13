package com.wl4g.devops.umc.endpoint;

import com.wl4g.devops.common.bean.umc.temple.basics.BasicsTemple;
import com.wl4g.devops.common.constants.UMCDevOpsConstants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vjay
 * @date 2019-06-11 21:29:00
 */
@RestController
@RequestMapping(UMCDevOpsConstants.URI_GATHER)
public class GatherController {

    @RequestMapping(UMCDevOpsConstants.URI_BASIC)
    public void basic(@RequestBody BasicsTemple basicsTemple){
        System.out.println(basicsTemple);
    }


}
