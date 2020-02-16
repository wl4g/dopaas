package com.wl4g.devops.doc.controller;


import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doc")
public class ShareDocController {

    @CrossOrigin
    @RequestMapping(value = "/read")
    public RespBase<?> list(PageModel pm, String name, String lang, Integer labelId) {
        RespBase<Object> resp = RespBase.create();
        resp.setData("# test");
        return resp;
    }

}
