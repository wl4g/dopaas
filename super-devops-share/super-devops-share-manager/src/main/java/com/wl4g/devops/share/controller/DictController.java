/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.share.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.PageModel;
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

import static com.wl4g.devops.common.constants.ShareDevOpsConstants.KEY_CACHE_SYS_DICT_ALL;

/**
 * Dictionaries controller
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
	public RespBase<?> list(PageModel pm, String key, String label, String type, String description) {
		RespBase<Object> resp = RespBase.create();

		Page<Dict> page = PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true);
		List<Dict> list = dictDao.list(key, label, type, description);

		pm.setTotal(page.getTotal());
		resp.buildMap().put("page", pm);
		resp.buildMap().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(Dict dict, Boolean isEdit) {
		RespBase<Object> resp = RespBase.create();
		if (isEdit) {
			dictService.update(dict);
		} else {
			dictService.insert(dict);
		}
		jedisService.del(KEY_CACHE_SYS_DICT_ALL);// when modify , remove cache
													// from redis
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(String key) {
		RespBase<Object> resp = RespBase.create();
		Dict dict = dictDao.selectByPrimaryKey(key);
		resp.buildMap().put("dict", dict);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(String key) {
		RespBase<Object> resp = RespBase.create();
		dictService.del(key);
		jedisService.del(KEY_CACHE_SYS_DICT_ALL);// when modify , remove cache
													// from redis
		return resp;
	}

	@RequestMapping(value = "/getByType")
	public RespBase<?> getByType(String type) {
		RespBase<Object> resp = RespBase.create();
		List<Dict> list = dictService.getBytype(type);
		resp.buildMap().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/getByKey")
	public RespBase<?> getByKey(String key) {
		RespBase<Object> resp = RespBase.create();
		Dict dict = dictService.getByKey(key);
		resp.buildMap().put("dict", dict);
		return resp;
	}

	@RequestMapping(value = "/allType")
	public RespBase<?> allType() {
		RespBase<Object> resp = RespBase.create();
		List<String> list = dictService.allType();
		resp.buildMap().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/cache")
	public RespBase<?> cache() {
		RespBase<Object> resp = RespBase.create();
		// get from redis first , not found then find from db
		String s = jedisService.get(KEY_CACHE_SYS_DICT_ALL);
		Map<String, Object> result;
		if (StringUtils.isNotBlank(s)) {
			result = JacksonUtils.parseJSON(s, new TypeReference<Map<String, Object>>() {
			});
		} else {
			result = dictService.cache();
			// cache to redis
			String s1 = JacksonUtils.toJSONString(result);
			jedisService.set(KEY_CACHE_SYS_DICT_ALL, s1, 0);
		}
		resp.setData(result);
		return resp;
	}

}