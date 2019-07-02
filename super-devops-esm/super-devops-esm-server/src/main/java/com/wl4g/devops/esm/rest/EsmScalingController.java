package com.wl4g.devops.esm.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;

@Controller
@RequestMapping("/api/")
public class EsmScalingController extends BaseController {

	@RequestMapping("scaling")
	public RespBase<?> scaling() {
		RespBase<?> resp = RespBase.create();
		// TODO
		//
		return resp;
	}

}
