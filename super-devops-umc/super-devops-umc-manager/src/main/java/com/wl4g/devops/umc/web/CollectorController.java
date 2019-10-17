package com.wl4g.devops.umc.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmCollector;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmCollectorDao;
import com.wl4g.devops.umc.service.CollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/collector")
public class CollectorController extends BaseController {

	@Autowired
	private AlarmCollectorDao alarmCollectorDao;

	@Autowired
	private CollectorService collectorService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, String addr, CustomPage customPage) {
		log.info("into ContactGroupController.list prarms::" + "name = {} , customPage = {} ", name, customPage);
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<CustomPage> page = PageHelper.startPage(pageNum, pageSize, true);
		List<AlarmCollector> list = alarmCollectorDao.list(name, addr);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(AlarmCollector alarmCollector) {
		log.info("into CollectorController.save prarms::" + "alarmCollector = {} ", alarmCollector);
		RespBase<Object> resp = RespBase.create();
		collectorService.save(alarmCollector);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		log.info("into CollectorController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		AlarmCollector alarmCollector = alarmCollectorDao.selectByPrimaryKey(id);
		resp.getData().put("alarmCollector", alarmCollector);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into CollectorController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		collectorService.del(id);
		return resp;
	}

}
