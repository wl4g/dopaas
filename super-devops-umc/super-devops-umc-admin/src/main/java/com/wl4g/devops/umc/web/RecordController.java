package com.wl4g.devops.umc.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.AlarmRecord;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.AlarmRecordDao;
import com.wl4g.devops.umc.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/record")
public class RecordController extends BaseController {

	@Autowired
	private AlarmRecordDao alarmRecordDao;

	@Autowired
	private RecordService recordService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, CustomPage customPage, String startDate, String endDate) {
		log.info("into RecordController.list prarms::" + "name = {} , customPage = {} , startDate = {} , endDate = {} ", name,
				customPage, startDate, endDate);
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
		Page<CustomPage> page = PageHelper.startPage(pageNum, pageSize, true);

		List<AlarmRecord> list = alarmRecordDao.list(name, startDate, endDate);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		log.info("into CollectorController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		AlarmRecord alarmRecord = recordService.detail(id);
		resp.getData().put("alarmRecord", alarmRecord);
		return resp;
	}

}
