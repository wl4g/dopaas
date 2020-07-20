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
package com.wl4g.devops.iam.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.iam.Dict;
import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import com.wl4g.devops.dao.iam.DictDao;
import com.wl4g.devops.iam.service.DictService;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.support.redis.jedis.JedisService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;
import static com.wl4g.devops.common.constants.ERMDevOpsConstants.CONFIG_DICT_CACHE_TIME_SECOND;
import static com.wl4g.devops.common.constants.ERMDevOpsConstants.KEY_CACHE_SYS_DICT_INIT_CACHE;

/**
 * @author vjay
 * @date 2019-08-13 09:51:00
 */
@Service
public class DictServiceImpl implements DictService {

	@Autowired
	private DictDao dictDao;

	@Autowired
	private JedisService jedisService;

	@Override
	public PageModel list(PageModel pm, String key, String label, String type, String description) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(dictDao.list(key, label, type, description, null));
		return pm;
	}

	@Override
	public void save(Dict dict, Boolean isEdit) {
		if (isEdit) {
			update(dict);
		} else {
			insert(dict);
		}
		jedisService.del(KEY_CACHE_SYS_DICT_INIT_CACHE);// when modify , remove cache from redis
	}

	public void insert(Dict dict) {
		checkBeforePersistence(dict);
		checkRepeat(dict);
		dict.preInsert();
		dictDao.insertSelective(dict);
	}

	public void update(Dict dict) {
		dict.preUpdate();
		dictDao.updateByPrimaryKeySelective(dict);
	}

	@Override
	public Dict detail(String key) {
		return dictDao.selectByPrimaryKey(key);
	}

	private void checkBeforePersistence(Dict dict) {
		Assert.notNull(dict, "dict is null");
		Assert.hasText(dict.getKey(), "key is null");
		Assert.hasText(dict.getType(), "key is null");
		Assert.hasText(dict.getLabel(), "key is null");
	}

	private void checkRepeat(Dict dict) {
		Dict dict1 = dictDao.selectByPrimaryKey(dict.getKey());
		Assert.isNull(dict1, "dict key is repeat");
	}

	@Override
	public void del(String key) {
		Assert.hasText(key, "id is null");
		Dict dict = new Dict();
		dict.setKey(key);
		dict.preUpdate();
		dict.setDelFlag(DEL_FLAG_DELETE);
		dictDao.updateByPrimaryKeySelective(dict);
	}

	@Override
	public List<Dict> getBytype(String type) {
		Assert.hasText(type, "type is blank");
		return dictDao.selectByType(type);
	}

	@Override
	public Dict getByKey(String key) {
		Assert.hasText(key, "key is blank");
		return dictDao.getByKey(key);
	}

	@Override
	public List<String> allType() {
		return dictDao.allType();
	}

	@Override
	public Map<String, Object> cache() {
		String s = jedisService.get(KEY_CACHE_SYS_DICT_INIT_CACHE);
		Map<String, Object> result;
		if (StringUtils.isNotBlank(s)) {
			result = JacksonUtils.parseJSON(s, new TypeReference<Map<String, Object>>() {
			});
		} else {
			result = new HashMap<>();
			List<Dict> dicts = dictDao.list(null, null, null, null, "1");
			Assert.notEmpty(dicts,"get dict from db is empty,Please check your db,table=sys_dict");
			Map<String, List<Dict>> dictList = new HashMap<>();
			Map<String, Map<String, Dict>> dictMap = new HashMap<>();
			for (Dict dict : dicts) {
				String type = dict.getType();
				List<Dict> list = dictList.get(type);
				Map<String, Dict> map = dictMap.get(type);
				if (null == list) {
					list = new ArrayList<>();
				}
				if (null == map) {
					map = new HashMap<>();
				}
				list.add(dict);
				map.put(dict.getValue(), dict);
				dictList.put(type, list);
				dictMap.put(type, map);
			}
			result.put("dictList", dictList);
			result.put("dictMap", dictMap);
			// cache to redis
			String s1 = JacksonUtils.toJSONString(result);
			jedisService.set(KEY_CACHE_SYS_DICT_INIT_CACHE, s1, CONFIG_DICT_CACHE_TIME_SECOND);
		}
		return result;
	}

}