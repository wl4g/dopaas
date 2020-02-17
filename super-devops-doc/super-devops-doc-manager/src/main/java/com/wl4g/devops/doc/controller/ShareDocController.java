package com.wl4g.devops.doc.controller;


import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.doc.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doc")
public class ShareDocController {

    @Autowired
    private FileService fileService;

    @CrossOrigin
    @RequestMapping(value = "/read")
    public RespBase<?> list(String code, String passwd) {
        RespBase<Object> resp = RespBase.create();
        FileChanges lastByFileCode = fileService.getLastByFileCode(code);
        if(StringUtils.isNotBlank(lastByFileCode.getPasswd()) && !lastByFileCode.getPasswd().equalsIgnoreCase(passwd)){
            resp.setCode(RespBase.RetCode.UNAUTHC);
            return resp;
        }
        resp.setData(lastByFileCode.getContent());
        return resp;
    }

}
