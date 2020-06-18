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
import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.dao.iam.GroupMenuDao;
import com.wl4g.devops.dao.iam.MenuDao;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.service.GroupService;
import com.wl4g.devops.iam.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.devops.common.bean.BaseBean.DEFAULT_USER_ROOT;
import static com.wl4g.devops.components.tools.common.lang.TypeConverts.parseIntOrNull;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getPrincipalInfo;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Menu service implements.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2019-10-30 15:48:00
 */
@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	protected MenuDao menuDao;
	@Autowired
	protected GroupService groupService;
	@Autowired
	protected GroupMenuDao groupMenuDao;

	@Override
	public Map<String, Object> getMenuTree() {
		Map<String, Object> result = new HashMap<>();
		Set<Menu> menusSet = getMenusSet();
		List<Menu> menus = new ArrayList<>(menusSet);
		menus = set2Tree(menus);
		result.put("data", menus);
		result.put("data2", new ArrayList<>(menusSet));
		return result;
	}

	public Menu getParent(List<Menu> menus, Integer parentId) {
		for (Menu menu : menus) {
			if (parentId != null && menu.getId() != null && menu.getId().intValue() == parentId.intValue()) {
				return menu;
			}
		}
		return null;
	}

	@Override
	public List<Menu> getMenuList() {
		IamPrincipalInfo info = getPrincipalInfo();
		if (DEFAULT_USER_ROOT.equals(info.getPrincipal())) {
			return menuDao.selectWithRoot();// root
		} else {
			return menuDao.selectByUserId(parseIntOrNull(info.getPrincipalId()));
		}
	}

	@Override
	public void save(Menu menu) {
		checkSort(menu);
		if (menu.getId() != null) {
			update(menu);
		} else {
			insert(menu);
		}
	}

	@Override
	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		Menu menu = new Menu();
		menu.setId(id);
		menu.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		menuDao.updateByPrimaryKeySelective(menu);
	}

	@Override
	public Menu detail(Integer id) {
		return menuDao.selectByPrimaryKey(id);
	}

	private void insert(Menu menu) {
		menu.preInsert();
		Integer parentId = menu.getParentId();
		if(Objects.nonNull(menu.getType()) && menu.getType().intValue()==3){// if menu type is button
			menu.setLevel(0);
		}else{
			if(Objects.nonNull(parentId) && 0 != parentId){// if has parent menu , set level = parent's level + 1
				Menu parentMenu = menuDao.selectByPrimaryKey(parentId);
				Assert.notNull(parentMenu,"parentMenu is null");
				Assert.notNull(parentMenu.getLevel(),"parentMenu's level is null");
				menu.setLevel(parentMenu.getLevel()+1);
			}else{// if is parent menu , set level = 1
				menu.setLevel(1);
			}
		}
		menuDao.insertSelective(menu);
	}

	private void update(Menu menu) {
		menu.preUpdate();
		menuDao.updateByPrimaryKeySelective(menu);
	}

	private void checkSort(Menu menu){
		List<Menu> menus = menuDao.selectByParentId(menu.getParentId());
		boolean hadSame = false;
		for(Menu m : menus){
			if(menu.getSort().equals(m.getSort()) && !m.getId().equals(menu.getId())){
				hadSame = true;
				break;
			}
		}
		Assert2.isTrue(!hadSame,"menu's sort repeat");
	}

	private Set<Menu> getMenusSet() {
		IamPrincipalInfo info = getPrincipalInfo();

		List<Menu> menus = null;
		if (DEFAULT_USER_ROOT.equals(info.getPrincipal())) {
			menus = menuDao.selectWithRoot();
		} else {
			Integer userId = null;
			if (isNotBlank(info.getPrincipalId())) {
				userId = Integer.parseInt(info.getPrincipalId());
			}
			menus = menuDao.selectByUserIdAccessGroup(userId);
		}
		Set<Menu> set = new LinkedHashSet<>();
		set.addAll(menus);
		return set;
	}

	private List<Menu> set2Tree(List<Menu> menus) {
		List<Menu> top = new ArrayList<>();
		for (Menu menu : menus) {
			Menu parent = getParent(menus, menu.getParentId());
			if (parent == null) {
				top.add(menu);
			}
		}
		for (Menu menu : top) {
			List<Menu> children = getChildren(menus, null, menu.getId());
			if (!CollectionUtils.isEmpty(children)) {
				menu.setChildren(children);
			}
		}
		return top;
	}

	private List<Menu> getChildren(List<Menu> menus, List<Menu> children, Integer parentId) {
		if (children == null) {
			children = new ArrayList<>();
		}
		for (Menu menu : menus) {
			if (menu.getParentId() != null && parentId != null && menu.getParentId().intValue() == parentId.intValue()) {
				children.add(menu);
			}
		}
		for (Menu menu : children) {
			List<Menu> children1 = getChildren(menus, null, menu.getId());
			if (!CollectionUtils.isEmpty(children1)) {
				menu.setChildren(children1);
			}
		}
		return children;
	}

}