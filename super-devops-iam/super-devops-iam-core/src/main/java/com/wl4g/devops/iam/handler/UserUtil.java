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
package com.wl4g.devops.iam.handler;

import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.common.bean.iam.Role;
import com.wl4g.devops.common.bean.iam.User;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.dao.iam.MenuDao;
import com.wl4g.devops.dao.iam.RoleDao;
import com.wl4g.devops.dao.iam.UserDao;
import com.wl4g.devops.iam.common.cache.UserCacheDto;
import com.wl4g.devops.support.cache.JedisService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-04 17:52:00
 */
@Service
public class UserUtil {

	private static final String cache_pre = "user_cache_";

	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private JedisService jedisService;

	public Integer getCurrentLoginUserId() {
		UserCacheDto currentLoginUser = getCurrentLoginUser();
		return currentLoginUser.getUserId();
	}

	public String getCurrentLoginUsername() {
		UserCacheDto currentLoginUser = getCurrentLoginUser();
		return currentLoginUser.getLoginName();
	}

	public String getCurrentLoginUserRoles() {
		UserCacheDto currentLoginUser = getCurrentLoginUser();
		return currentLoginUser.getRoles();
	}

	public String getCurrentLoginUserPermissions() {
		UserCacheDto currentLoginUser = getCurrentLoginUser();
		return currentLoginUser.getPermissions();
	}

	public UserCacheDto getCurrentLoginUser() {
		String principal = (String) SecurityUtils.getSubject().getPrincipal();
		Assert.hasText(principal, "can not get current login user");
		String s = jedisService.get(cache_pre + principal);
		UserCacheDto userCacheDto = null;
		if (StringUtils.isBlank(s)) {
			User user = userDao.selectByUserName(principal);
			Assert.notNull(user, "can not found user by username");
			userCacheDto = new UserCacheDto();
			// userId
			userCacheDto.setUserId(user.getId());
			// login name
			userCacheDto.setLoginName(principal);
			// role
			String roles = findRoles(user);
			userCacheDto.setRoles(roles);
			// permission
			String permissions = findPermissions(user);
			userCacheDto.setPermissions(permissions);

		} else {
			userCacheDto = JacksonUtils.parseJSON(s, UserCacheDto.class);
		}
		return userCacheDto;
	}

	public String findRoles(User user) {
		List<Role> list = roleDao.selectByUserId(user.getId());
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Role role = list.get(i);
			if (i == list.size() - 1) {
				sb.append(role.getRoleCode());
			} else {
				sb.append(role.getRoleCode()).append(",");
			}
		}
		return sb.toString();
	}

	public String findPermissions(User user) {
		List<Menu> list = menuDao.selectByUserId(user.getId());
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Menu menu = list.get(i);
			if (i == list.size() - 1) {
				sb.append(menu.getPermission());
			} else {
				sb.append(menu.getPermission()).append(",");
			}
		}
		return sb.toString();
	}

}