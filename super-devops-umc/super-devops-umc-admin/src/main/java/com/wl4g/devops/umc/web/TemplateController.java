package com.wl4g.devops.umc.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.umc.service.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/template")
public class TemplateController extends BaseController {

    @Autowired
    private AlarmTemplateDao alarmTemplateDao;

    @Autowired
    private TemplateService templateService;

    @RequestMapping(value = "/list")
    public RespBase<?> list(String name,String metric,String classify, CustomPage customPage) {
        log.info("into TemplateController.list prarms::"+ "name = {} , metric = {} , classify = {} , customPage = {} ", name, metric, classify, customPage );
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page page = PageHelper.startPage(pageNum, pageSize, true);
        List<AlarmTemplate> list = alarmTemplateDao.list(name, metric,classify);
        for(AlarmTemplate alarmTemplate : list){
            String tags = alarmTemplate.getTags();
            if(StringUtils.isNotBlank(tags)){
                alarmTemplate.setTagMap(parseJSON(tags, List.class));
            }
        }
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody AlarmTemplate alarmTemplate) {
        log.info("into TemplateController.save prarms::"+ "alarmTemplate = {} ", alarmTemplate );
        RespBase<Object> resp = RespBase.create();
        templateService.save(alarmTemplate);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        log.info("into TemplateController.detail prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        AlarmTemplate alarmTemplate = templateService.detail(id);
        resp.getData().put("alarmTemplate",alarmTemplate);
        return resp;
    }


    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        log.info("into TemplateController.del prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        templateService.del(id);

        return resp;
    }





}
