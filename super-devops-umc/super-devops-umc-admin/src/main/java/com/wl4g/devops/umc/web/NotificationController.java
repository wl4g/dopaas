package com.wl4g.devops.umc.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmNotification;
import com.wl4g.devops.common.bean.umc.AlarmNotificationContact;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmNotificationContactDao;
import com.wl4g.devops.dao.umc.AlarmNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/notification")
public class NotificationController extends BaseController {

    @Autowired
    private AlarmNotificationDao alarmNotificationDao;

    @Autowired
    private AlarmNotificationContactDao alarmNotificationContactDao;

    @RequestMapping(value = "/list")
    public RespBase<?> list(String startDate, String endDate, CustomPage customPage) {
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page page = PageHelper.startPage(pageNum, pageSize, true);
        List<AlarmNotification> list = alarmNotificationDao.list(startDate, endDate);
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        AlarmNotification notification = alarmNotificationDao.selectByPrimaryKey(id);
        List<AlarmNotificationContact> notificationContacts = alarmNotificationContactDao.getByNotificationId(id);
        resp.getData().put("metricTemplate",notification);
        resp.getData().put("notificationContacts",notificationContacts);
        return resp;
    }






}
