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

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.iam.Group;
import com.wl4g.devops.common.bean.iam.GroupMenu;
import com.wl4g.devops.common.bean.iam.GroupRole;
import com.wl4g.devops.dao.iam.GroupDao;
import com.wl4g.devops.dao.iam.GroupMenuDao;
import com.wl4g.devops.dao.iam.GroupRoleDao;
import com.wl4g.devops.iam.handler.UserUtil;
import com.wl4g.devops.iam.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wl4g.devops.common.bean.BaseBean.DEFAULT_USER_ROOT;
import static com.wl4g.devops.common.utils.lang.Collections2.disDupCollection;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Group service implements.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2019-10-29 16:19:00
 */
@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private GroupMenuDao groupMenuDao;

	@Autowired
	private GroupRoleDao groupRoleDao;

	@Override
	public List<Group> getGroupsTree() {
		Set<Group> groupsSet = getGroupsSet();
		ArrayList<Group> groups = new ArrayList<>(groupsSet);
		return set2Tree(groups);
	}

	private List<Group> set2Tree(List<Group> groups) {
		List<Group> top = new ArrayList<>();
		for (Group group : groups) {
			Group parent = getParent(groups, group.getParentId());
			if (parent == null) {
				top.add(group);
			}
		}
		for (Group group : top) {
			List<Group> children = getChildren(groups, null, group.getId());
			if (!CollectionUtils.isEmpty(children)) {
				group.setChildren(children);
			}
		}
		return top;
	}

	private List<Group> getChildren(List<Group> groups, List<Group> children, Integer parentId) {
		if (children == null) {
			children = new ArrayList<>();
		}
		for (Group group : groups) {
			if (group.getParentId() != null && parentId != null && group.getParentId().intValue() == parentId.intValue()) {
				children.add(group);
			}
		}
		for (Group group : children) {
			List<Group> children1 = getChildren(groups, null, group.getId());
			if (!CollectionUtils.isEmpty(children1)) {
				group.setChildren(children1);
			}
		}
		return children;
	}

	public Group getParent(List<Group> groups, Integer parentId) {
		for (Group group : groups) {
			if (parentId != null && group.getId() != null && group.getId().intValue() == parentId.intValue()) {
				return group;
			}
		}
		return null;
	}

	@Override
	public Set<Group> getGroupsSet() {
		Integer currentLoginUserId = userUtil.getCurrentLoginUserId();
		List<Group> groups = null;

		String currentLoginUsername = userUtil.getCurrentLoginUsername();
		if (DEFAULT_USER_ROOT.equals(currentLoginUsername)) {
			groups = groupDao.selectByRoot();
		} else {
			groups = groupDao.selectByUserId(currentLoginUserId);
		}

		Set<Group> set = new HashSet<>();
		set.addAll(groups);
		for (Group group : groups) {
			getChildrensList(group.getId(), set);
		}
		return set;
	}

	private void getChildrensList(Integer parentId, Set<Group> set) {
		List<Group> childrens = groupDao.selectByParentId(parentId);
		set.addAll(childrens);
		for (Group group : childrens) {
			getChildrensList(group.getId(), set);
		}
	}

	@Override
	public void save(Group group) {
		if (!isEmpty(group.getMenuIds())) { // Menus repeat
			group.setMenuIds((List<Integer>) disDupCollection(group.getMenuIds()));
		}
		if (!isEmpty(group.getRoleIds())) { // Roles repeat
			group.setRoleIds((List<Integer>) disDupCollection(group.getRoleIds()));
		}
		if (nonNull(group.getId())) {
			update(group);
		} else {
			insert(group);
		}
	}

	private void insert(Group group) {
		group.preInsert();
		groupDao.insertSelective(group);
		// menu
		List<GroupMenu> groupMenus = new ArrayList<>();
		for (Integer menuId : group.getMenuIds()) {
			GroupMenu groupMenu = new GroupMenu();
			groupMenu.preInsert();
			groupMenu.setMenuId(menuId);
			groupMenu.setGroupId(group.getId());
			groupMenus.add(groupMenu);
		}
		groupMenuDao.insertBatch(groupMenus);

		// role
		List<GroupRole> groupRoles = new ArrayList<>();
		for (Integer roleId : group.getRoleIds()) {
			GroupRole groupRole = new GroupRole();
			groupRole.preInsert();
			groupRole.setGroupId(group.getId());
			groupRole.setRoleId(roleId);
			groupRoles.add(groupRole);
		}
		groupRoleDao.insertBatch(groupRoles);

	}

	private void update(Group group) {
		group.preUpdate();
		groupDao.updateByPrimaryKeySelective(group);
		groupMenuDao.deleteByGroupId(group.getId());
		groupRoleDao.deleteByGroupId(group.getId());
		// menu
		List<GroupMenu> groupMenus = new ArrayList<>();
		for (Integer menuId : group.getMenuIds()) {
			GroupMenu groupMenu = new GroupMenu();
			groupMenu.preInsert();
			groupMenu.setMenuId(menuId);
			groupMenu.setGroupId(group.getId());
			groupMenus.add(groupMenu);
		}
		groupMenuDao.insertBatch(groupMenus);

		// role
		List<GroupRole> groupRoles = new ArrayList<>();
		for (Integer roleId : group.getRoleIds()) {
			GroupRole groupRole = new GroupRole();
			groupRole.preInsert();
			groupRole.setGroupId(group.getId());
			groupRole.setRoleId(roleId);
			groupRoles.add(groupRole);
		}
		groupRoleDao.insertBatch(groupRoles);
	}

	@Override
	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		Group group = new Group();
		group.setId(id);
		group.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		groupDao.updateByPrimaryKeySelective(group);
	}

	@Override
	public Group detail(Integer id) {
		Assert.notNull(id, "id is null");
		Group group = groupDao.selectByPrimaryKey(id);
		Assert.notNull(group, "group is null");
		List<Integer> menuIds = groupMenuDao.selectMenuIdsByGroupId(id);
		List<Integer> roleIds = groupRoleDao.selectRoleIdsByGroupId(id);
		group.setMenuIds(menuIds);
		group.setRoleIds(roleIds);
		return group;
	}

}