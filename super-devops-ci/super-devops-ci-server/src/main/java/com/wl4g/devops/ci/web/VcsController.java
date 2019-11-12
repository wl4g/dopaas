package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.service.VcsService;
import com.wl4g.devops.common.bean.ci.Vcs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;

/**
 * @author vjay
 * @date 2019-11-12 11:03:00
 */
@RestController
@RequestMapping("/vcs")
public class VcsController extends BaseController {

	@Autowired
	private VcsService vcsService;

	@RequestMapping("/list")
	public RespBase<?> list(PageModel pm) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(vcsService.list(pm));
		return resp;
	}

	@RequestMapping("/save")
	public RespBase<?> save(Vcs vcs) {
		RespBase<Object> resp = RespBase.create();
		vcsService.save(vcs);
		return resp;
	}

	@RequestMapping("/del")
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		vcsService.del(id);
		return resp;
	}

	@RequestMapping("/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		Vcs vcs = vcsService.detail(id);
		resp.setData(vcs);
		return resp;
	}

}
