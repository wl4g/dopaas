package com.wl4g.devops.dts.codegen.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.service.GenConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link GenConfigurationController}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2020-09-08
 * @version v1.0 2020-09-10
 * @since
 */
@RestController
@RequestMapping("/database") // TODO rename
public class GenConfigurationController extends BaseController {

	@Autowired
	private GenConfigurationService genDatabaseService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genDatabaseService.page(pm, name));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody GenDataSource gen) {
		RespBase<Object> resp = RespBase.create();
		genDatabaseService.save(gen);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genDatabaseService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		genDatabaseService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getForSelect")
	public RespBase<?> getForSelect() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genDatabaseService.getForSelect());
		return resp;
	}
}
