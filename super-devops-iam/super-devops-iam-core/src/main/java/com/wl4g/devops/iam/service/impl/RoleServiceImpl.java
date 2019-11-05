package com.wl4g.devops.iam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.iam.Group;
import com.wl4g.devops.common.bean.iam.GroupRole;
import com.wl4g.devops.common.bean.iam.Role;
import com.wl4g.devops.common.bean.iam.RoleMenu;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.dao.iam.GroupDao;
import com.wl4g.devops.dao.iam.GroupRoleDao;
import com.wl4g.devops.dao.iam.RoleDao;
import com.wl4g.devops.dao.iam.RoleMenuDao;
import com.wl4g.devops.iam.handler.UserUtil;
import com.wl4g.devops.iam.service.GroupService;
import com.wl4g.devops.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author vjay
 * @date 2019-10-29 16:02:00
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleMenuDao roleMenuDao;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRoleDao groupRoleDao;

    @Override
    public List getRolesByUserGroups() {
        //TODO get current logined userId ==> get Groups by this user ==> get Roles by Groups
        Integer currentLoginUserId = userUtil.getCurrentLoginUserId();
        List<Group> groups = groupDao.selectByUserId(currentLoginUserId);
        Set<Role> set = new HashSet<>();
        for(Group group : groups){
            List<Role> roles = roleDao.selectByGroupId(group.getId());
            set.addAll(roles);
        }
        return new ArrayList(set);
    }


    @Override
    public Map<String,Object> list(CustomPage customPage, String name, String displayName) {
        Map<String,Object> resp = new HashMap<>();
        Integer currentLoginUserId = userUtil.getCurrentLoginUserId();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
        Page<Project> page = PageHelper.startPage(pageNum, pageSize, true);
        List<Role> list = roleDao.list(currentLoginUserId,name,displayName);
        Set<Group> groupsSet = groupService.getGroupsSet();//get user group
        for(Role role : list){
            List<Group> groups = groupDao.selectByRoleId(role.getId());
            groups = removeUnhad(groups,groupsSet);//remove unhad
            String s = groups2Str(groups);
            role.setGroupDisplayName(s);
        }
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.put("page", customPage);
        resp.put("list", list);
        return resp;
    }

    private List<Group> removeUnhad(List<Group> groups,Set<Group> groupsSet){
        if(CollectionUtils.isEmpty(groups)){
            return Collections.emptyList();
        }
        if(CollectionUtils.isEmpty(groupsSet)){
            return Collections.emptyList();
        }
        for(int i=groups.size()-1;i>=0;i--){
            boolean had = false;
            for(Group group1 : groupsSet){
                if(groups.get(i).getId().intValue()==group1.getId().intValue()){
                    had = true;
                    break;
                }
            }
            if(!had){
                groups.remove(i);
            }
        }
        return groups;
    }

    private String groups2Str(List<Group> groups){
        if(CollectionUtils.isEmpty(groups)){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<groups.size();i++){
            if(i==groups.size()-1){
                stringBuilder.append(groups.get(i).getDisplayName());
            }else{
                stringBuilder.append(groups.get(i).getDisplayName()).append(",");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void save(Role role) {
        if(!CollectionUtils.isEmpty(role.getMenuIds())){//handle repeat
            Set<Integer> set = new HashSet();
            set.addAll(role.getMenuIds());
            role.setMenuIds(new ArrayList<>(set));
        }
        if(!CollectionUtils.isEmpty(role.getGroupIds())){//handle repeat
            Set<Integer> set = new HashSet();
            set.addAll(role.getGroupIds());
            role.setGroupIds(new ArrayList<>(set));
        }
        if (role.getId() != null) {
            update(role);
        } else {
            insert(role);
        }
    }


    private void insert(Role role) {
        role.preInsert();
        roleDao.insertSelective(role);
        List<RoleMenu> roleMenus = new ArrayList<>();
        //menu
        for(Integer menuId :role.getMenuIds()){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.preInsert();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(role.getId());
            roleMenus.add(roleMenu);
        }
        roleMenuDao.insertBatch(roleMenus);
        //group
        List<GroupRole> groupRoles = new ArrayList<>();
        for(Integer groupId :role.getGroupIds()){
            GroupRole groupRole = new GroupRole();
            groupRole.preInsert();
            groupRole.setGroupId(groupId);
            groupRole.setRoleId(role.getId());
            groupRoles.add(groupRole);
        }
        groupRoleDao.insertBatch(groupRoles);
    }

    private void update(Role role) {
        role.preUpdate();
        roleDao.updateByPrimaryKeySelective(role);
        roleMenuDao.deleteByRoleId(role.getId());
        groupRoleDao.deleteByRoleId(role.getId());
        List<Integer> menuIds = role.getMenuIds();
        //menu
        List<RoleMenu> roleMenus = new ArrayList<>();
        for(Integer menuId :menuIds){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.preInsert();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(role.getId());
            roleMenus.add(roleMenu);
        }
        roleMenuDao.insertBatch(roleMenus);
        //group
        List<GroupRole> groupRoles = new ArrayList<>();
        for(Integer groupId :role.getGroupIds()){
            GroupRole groupRole = new GroupRole();
            groupRole.preInsert();
            groupRole.setGroupId(groupId);
            groupRole.setRoleId(role.getId());
            groupRoles.add(groupRole);
        }
        groupRoleDao.insertBatch(groupRoles);
    }

    @Override
    public void del(Integer id) {
        Assert.notNull(id, "id is null");
        Role role = new Role();
        role.setId(id);
        role.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        roleDao.updateByPrimaryKeySelective(role);
    }

    @Override
    public Role detail(Integer id){
        Role role = roleDao.selectByPrimaryKey(id);
        List<Integer> menuIds = roleMenuDao.selectMenuIdByRoleId(id);
        List<Integer> groupIds = groupRoleDao.selectGroupIdByRoleId(id);
        role.setMenuIds(menuIds);
        role.setGroupIds(groupIds);
        return role;

    }
}
