package com.wl4g.devops.share.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.share.Dict;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.DictDao;
import com.wl4g.devops.share.service.DictService;
import com.wl4g.devops.support.cache.JedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.constants.ShareDevOpsConstants.REDIS_DICTS_CACHE;

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

	@Autowired
	private JedisService jedisService;

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
	public RespBase<?> save(Dict dict,Boolean isEdit) {
		RespBase<Object> resp = RespBase.create();
		if(isEdit){
			dictService.update(dict);
		}else{
			dictService.insert(dict);
		}
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(String key) {
		RespBase<Object> resp = RespBase.create();
		Dict dict = dictDao.selectByPrimaryKey(key);
		resp.getData().put("dict",dict);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(String key) {
		RespBase<Object> resp = RespBase.create();
		dictService.del(key);
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

	@RequestMapping(value = "/cache")
	public RespBase<?> cache() {
		RespBase<Object> resp = RespBase.create();
		//get from redis first , not found then find from db
		String s = jedisService.get(REDIS_DICTS_CACHE);
		Map<String, Object> result;
		if(StringUtils.isNotBlank(s)){
			result = JacksonUtils.parseJSON(s, new TypeReference<Map<String, Object>>() {});
		}else{
			result = dictService.cache();
			//cache to redis
			String s1 = JacksonUtils.toJSONString(result);
			jedisService.set(REDIS_DICTS_CACHE,s1,0);
		}
		resp.setData(result);
		return resp;
	}


}
