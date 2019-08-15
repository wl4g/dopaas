package com.wl4g.devops.umc.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.umc.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/config")
public class ConfigController extends BaseController {

    @Autowired
    private AlarmConfigDao alarmConfigDao;

    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "/list")
    public RespBase<?> list(Integer templateId,Integer contactGroupId, CustomPage customPage) {
        log.info("into ConfigController.list prarms::"+ "templateId = {} , contactGroupId = {} , customPage = {} ", templateId, contactGroupId, customPage );
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page page = PageHelper.startPage(pageNum, pageSize, true);
        List<AlarmConfig> list = alarmConfigDao.list(templateId, contactGroupId);
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody AlarmConfig alarmConfig) {
        RespBase<Object> resp = RespBase.create();
        configService.save(alarmConfig);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        AlarmConfig alarmConfig = configService.detail(id);
        resp.getData().put("alarmConfig",alarmConfig);
        return resp;
    }


    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        configService.del(id);
        return resp;
    }





}
