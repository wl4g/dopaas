package com.wl4g.devops.umc.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.bean.umc.AlarmContactGroup;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmContactDao;
import com.wl4g.devops.dao.umc.AlarmContactGroupDao;
import com.wl4g.devops.umc.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/contact")
public class ContactController extends BaseController {

    @Autowired
    private AlarmContactDao alarmContactDao;

    @Autowired
    private ContactService contactService;

    @Autowired
    private AlarmContactGroupDao alarmContactGroupDao;

    @RequestMapping(value = "/list")
    public RespBase<?> list(String name, String email,String phone, CustomPage customPage) {
        log.info("into ContactController.list prarms::"+ "name = {} , email = {} , phone = {} , customPage = {} ", name, email, phone, customPage );
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page page = PageHelper.startPage(pageNum, pageSize, true);
        List<AlarmContact> list = alarmContactDao.list(name, email,phone);
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody AlarmContact alarmContact) {
        log.info("into ProjectController.save prarms::" + "alarmContact = {} ", alarmContact);
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(alarmContact,"contact is null");
        Assert.hasText(alarmContact.getName(),"name is null");
        Assert.hasText(alarmContact.getEmail(),"email is null");
        Assert.notEmpty(alarmContact.getGroups(),"contactGroup is null");
        contactService.save(alarmContact);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        log.info("into ContactController.detail prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        AlarmContact contact = contactService.detail(id);
        resp.getData().put("contact",contact);
        return resp;
    }


    @RequestMapping(value = "/groupList")
    public RespBase<?> groupList() {
        RespBase<Object> resp = RespBase.create();
        List<AlarmContactGroup> alarmContactGroups = alarmContactGroupDao.list(null);
        resp.getData().put("list",alarmContactGroups);
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        log.info("into ContactController.del prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        contactService.del(id);
        return resp;
    }





}
