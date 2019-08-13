package com.wl4g.devops.share.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.share.service.DictService;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.share.Dict;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.DictDao;
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
@RequestMapping("/dict")
public class DictController extends BaseController {

	@Autowired
	private DictDao dictDao;

	@Autowired
	private DictService dictService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(CustomPage customPage,String key,String label,String type,String description) {
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page page = PageHelper.startPage(pageNum, pageSize, true);
		List<Dict> list = dictDao.list(key,label,type,description);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(Dict dict) {
		RespBase<Object> resp = RespBase.create();
		dictService.save(dict);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		Dict dict = dictDao.selectByPrimaryKey(id);
		resp.getData().put("dict",dict);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		dictService.del(id);
		return resp;
	}


	@RequestMapping(value = "/getByType")
	public RespBase<?> getByType(String type) {
		RespBase<Object> resp = RespBase.create();
		List<Dict> list = dictService.getBytype(type);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/getByKey")
	public RespBase<?> getByKey(String key) {
		RespBase<Object> resp = RespBase.create();
		Dict dict = dictService.getByKey(key);
		resp.getData().put("dict", dict);
		return resp;
	}

	@RequestMapping(value = "/allType")
	public RespBase<?> allType() {
		RespBase<Object> resp = RespBase.create();
		List<String> list = dictService.allType();
		resp.getData().put("list", list);
		return resp;
	}

}
