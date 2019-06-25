package com.wl4g.devops.scm.controller;

import com.wl4g.devops.common.bean.share.Dict;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.DictDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/dict")
public class DictController extends BaseController {

    @Autowired
    private DictDao dictDao;

    @RequestMapping(value = "/getByType")
    public RespBase<?> getByType(String type){
        RespBase<List<Dict>> resp = new RespBase<List<Dict>>();
        List<Dict> dicts =  dictDao.selectByType(type);
        resp.getData().put("dicts", dicts);
        return resp;
    }

}
