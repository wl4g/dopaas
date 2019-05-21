package com.wl4g.devops.rest.controller;

import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.common.web.RespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
//@RestController
@RequestMapping("/ci")
@Controller
public class CiController {

	@Autowired
	private CiService ciService;

	@RequestMapping(value = "/grouplist")
	@ResponseBody
	public RespBase<?> grouplist(){
		RespBase<List<AppGroup>> resp = RespBase.create();
		resp.getData().put("appGroups",ciService.grouplist());
		return resp;
	}


	@RequestMapping(value = "/environmentlist")
	@ResponseBody
	public RespBase<?> environmentlist(String groupId){
		RespBase<List<Environment>> resp = RespBase.create();
		List<Environment> environments = ciService.environmentlist(groupId);
		resp.getData().put("environments",environments);
		return resp;
	}


	@RequestMapping(value = "/instancelist")
	@ResponseBody
	public RespBase<?> instancelist(AppInstance appInstance){
		RespBase<List<AppInstance>> resp = RespBase.create();
		List<AppInstance> appInstances = ciService.instancelist(appInstance);
		resp.getData().put("appInstances",appInstances);
		return resp;
	}



}
