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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.iam.*;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.dao.iam.*;
import com.wl4g.devops.iam.authc.credential.secure.CredentialsSecurer;
import com.wl4g.devops.iam.authc.credential.secure.CredentialsToken;
import com.wl4g.devops.iam.handler.UserUtil;
import com.wl4g.devops.iam.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.devops.common.bean.BaseBean.ROOT_NAME;

/**
 * User service implements.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2019-10-28 16:38:00
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private MenuDao menuDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private RoleUserDao roleUserDao;

	@Autowired
	private GroupUserDao groupUserDao;

	@Autowired
	private CredentialsSecurer credentialsSecurer;

	@Autowired
	private UserUtil userUtil;

	@Override
	public Map<String, Object> list(CustomPage customPage, String userName, String displayName) {
		Map<String, Object> resp = new HashMap<>();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<Project> page = PageHelper.startPage(pageNum, pageSize, true);

		List<User> list = null;
		String currentLoginUsername = userUtil.getCurrentLoginUsername();
		if (ROOT_NAME.equals(currentLoginUsername)) {
			list = userDao.list(null, userName, displayName);
		} else {
			list = userDao.list(userUtil.getCurrentLoginUserId(), userName, displayName);
		}

		for (User user : list) {
			// group
			List<Group> groups = groupDao.selectByUserId(user.getId());
			user.setGroupNameStrs(groups2Str(groups));
			// roles
			List<Role> roles = roleDao.selectByUserId(user.getId());
			user.setRoleStrs(roles2Str(roles));
		}
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.put("page", customPage);
		resp.put("list", list);
		return resp;
	}

	@Override
	public void save(User user) {
		if (StringUtils.isNotBlank(user.getPassword())) {
			String signature = credentialsSecurer.signature(new CredentialsToken(user.getUserName(), user.getPassword()));
			user.setPassword(signature);
		}
		if (user.getId() != null) {
			update(user);
		} else {
			insert(user);
		}
	}

	private void insert(User user) {
		User user1 = userDao.selectByUserName(user.getUserName());
		Assert.isTrue(user1 == null, user.getUserName() + " is exist");
		user.preInsert();
		userDao.insertSelective(user);
		List<Integer> roleIds = user.getRoleIds();
		for (Integer roleId : roleIds) {
			RoleUser roleUser = new RoleUser();
			roleUser.preInsert();
			roleUser.setUserId(user.getId());
			roleUser.setRoleId(roleId);
			roleUserDao.insertSelective(roleUser);
		}
		List<Integer> groupIds = user.getGroupIds();
		for (Integer groupId : groupIds) {
			GroupUser groupUser = new GroupUser();
			groupUser.preInsert();
			groupUser.setGroupId(groupId);
			groupUser.setUserId(user.getId());
			groupUserDao.insertSelective(groupUser);
		}
	}

	private void update(User user) {
		user.preUpdate();
		userDao.updateByPrimaryKeySelective(user);
		roleUserDao.deleteByUserId(user.getId());
		List<Integer> roleIds = user.getRoleIds();
		for (Integer roleId : roleIds) {
			RoleUser roleUser = new RoleUser();
			roleUser.preInsert();
			roleUser.setUserId(user.getId());
			roleUser.setRoleId(roleId);
			roleUserDao.insertSelective(roleUser);
		}
		groupUserDao.deleteByUserId(user.getId());
		List<Integer> groupIds = user.getGroupIds();
		for (Integer groupId : groupIds) {
			GroupUser groupUser = new GroupUser();
			groupUser.preInsert();
			groupUser.setGroupId(groupId);
			groupUser.setUserId(user.getId());
			groupUserDao.insertSelective(groupUser);
		}
	}

	@Override
	public void del(Integer userId) {
		User user = new User();
		user.setId(userId);
		user.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		userDao.updateByPrimaryKeySelective(user);
	}

	@Override
	public User detail(Integer userId) {
		User user = userDao.selectByPrimaryKey(userId);
		if (user == null) {
			return null;
		}
		List<Integer> roleIds = roleUserDao.selectRoleIdByUserId(userId);
		List<Integer> groupIds = groupUserDao.selectGroupIdByUserId(userId);
		user.setRoleIds(roleIds);
		user.setGroupIds(groupIds);
		return user;
	}

	@Override
	public User getUserById(Integer id) {
		return userDao.selectByPrimaryKey(id);
	}

	@Override
	public Set<Menu> getMenusByUserId(Integer userId) {
		List<Menu> menus = menuDao.selectByUserId(userId);
		Set<Menu> set = new HashSet<>(menus);
		for (Menu menu : menus) {
			getMenusByParentId(menu.getId(), set);
		}
		return set;
	}

	private void getMenusByParentId(Integer parentId, Set<Menu> menuSet) {
		// TODO chche best
		List<Menu> menus = menuDao.selectByParentId(parentId);
		if (!CollectionUtils.isEmpty(menus)) {
			if (menuSet != null) {
				menuSet.addAll(menus);
			}
			for (Menu menu : menus) {
				getMenusByParentId(menu.getId(), menuSet);
			}
		}
	}

	private String roles2Str(List<Role> roles) {
		if (CollectionUtils.isEmpty(roles)) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < roles.size(); i++) {
			String displayName = roles.get(i).getDisplayName();
			if (i == roles.size() - 1) {
				stringBuilder.append(displayName);
			} else {
				stringBuilder.append(displayName).append(",");
			}
		}
		return stringBuilder.toString();
	}

	private String groups2Str(List<Group> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < groups.size(); i++) {
			String displayName = groups.get(i).getDisplayName();
			if (i == groups.size() - 1) {
				stringBuilder.append(displayName);
			} else {
				stringBuilder.append(displayName).append(",");
			}
		}
		return stringBuilder.toString();
	}

}