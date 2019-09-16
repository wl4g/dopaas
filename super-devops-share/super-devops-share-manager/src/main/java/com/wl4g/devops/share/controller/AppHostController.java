package com.wl4g.devops.share.controller;

import com.wl4g.devops.common.bean.share.AppHost;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.AppHostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/host")
public class AppHostController extends BaseController {

	@Autowired
	private AppHostDao appHostDao;

	@RequestMapping(value = "/all")
	public RespBase<?> allType() {
		RespBase<Object> resp = RespBase.create();
		List<AppHost> list = appHostDao.list(null, null, null);
		resp.getData().put("list", list);
		return resp;
	}

}
