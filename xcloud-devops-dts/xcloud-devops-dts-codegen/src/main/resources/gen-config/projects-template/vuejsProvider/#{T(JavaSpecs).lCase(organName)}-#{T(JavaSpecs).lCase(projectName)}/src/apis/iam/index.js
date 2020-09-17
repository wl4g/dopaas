import global from "../../common/global_variable";
import request from '@/utils/ajax/request'

export default [
    //user
    {
        name: '用户列表',
        method: 'userList',
        path: '/user/list',
        type: 'post',
        sys: global.iam
    },
    {
        name: '保存用户',
        method: 'saveUser',
        path: '/user/save',
        type: 'json',
        sys: global.iam
    },
    {
        name: '用户详情',
        method: 'userDetail',
        path: '/user/detail',
        type: 'post',
        sys: global.iam
    },
    {
        name: '删除用户',
        method: 'delUser',
        path: '/user/del',
        type: 'post',
        sys: global.iam
    },
    //menu
    {
        name: 'menu树列表',
        method: 'getMenuTree',
        path: '/menu/tree',
        type: 'post',
        sys: global.iam
    },
    {
        name: '保存menu',
        method: 'saveMenu',
        path: '/menu/save',
        type: 'json',
        sys: global.iam
    },
    {
        name: '删除menu',
        method: 'delMenu',
        path: '/menu/del',
        type: 'post',
        sys: global.iam
    },
    {
        name: '获取用户menu列表',
        method: 'getMenuList',
        path: '/menu/list',
        type: 'post',
        sys: global.iam
    },
    //role
    {
        name: '角色列表',
        method: 'getRoles',
        path: '/role/getRolesByUserGroups',
        type: 'post',
        sys: global.iam
    },
    {
        name: '角色列表',
        method: 'roleList',
        path: '/role/list',
        type: 'post',
        sys: global.iam
    },
    {
        name: '保存角色',
        method: 'saveRole',
        path: '/role/save',
        type: 'json',
        sys: global.iam
    },
    {
        name: '删除角色',
        method: 'delRole',
        path: '/role/del',
        type: 'post',
        sys: global.iam
    },
    {
        name: '角色详情',
        method: 'roleDetail',
        path: '/role/detail',
        type: 'post',
        sys: global.iam
    },
    //group
    {
        name: '获取分组树',
        method: 'getGroupsTree',
        path: '/group/getGroupsTree',
        type: 'post',
        sys: global.iam
    },
    {
        name: '保存Group',
        method: 'saveGroup',
        path: '/group/save',
        type: 'json',
        sys: global.iam
    },
    {
        name: '删除Group',
        method: 'delGroup',
        path: '/group/del',
        type: 'post',
        sys: global.iam
    },
    {
        name: 'Group详情',
        method: 'groupDetail',
        path: '/group/detail',
        type: 'post',
        sys: global.iam
    },{
        name: 'getOrganizations',
        method: 'getOrganizations',
        path: '/group/getOrganizations',
        type: 'post',
        sys: global.iam
    },
    //online
    {
        name: 'onlineList',
        method: 'onlineList',
        path: '/mgr/v1/getSessions',
        type: 'post',
        sys: global.iam
    },
    {
        name: 'getIamServer',
        method: 'getIamServer',
        path: '/mgr/v1/findIamServers',
        type: 'post',
        sys: global.iam
    },
    {
        name: 'destroySessions',
        method: 'destroySessions',
        path: '/mgr/v1/destroySessions',
        type: 'post',
        sys: global.iam
    },
    // Cluster Config Informcation
    {
        name: 'appModules',
        method: 'clusterConfigInfo',
        path: '/clusterConfig/info',
        type: 'get', // 原因参见：getInit
        sys: global.iam,
        usedefault: true,
    },
    {
        name: 'applylocale',
        method: 'applylocale',
        path: '/login/applylocale',
        type: 'post',
        sys: global.iam
    },
    {
        name: 'logout',
        method: 'logout',
        path: '/logout',
        type: 'post',
        sys: global.iam
    },
    //dict
    {
        name: '字典列表',
        method: 'dictList',
        path: '/dict/list',
        type: 'post',
        sys: global.iam
    },
    {
        name: '保存字典',
        method: 'saveDict',
        path: '/dict/save',
        type: 'post',
        sys: global.iam
    },
    {
        name: '字典详情',
        method: 'dictDetail',
        path: '/dict/detail',
        type: 'post',
        sys: global.iam
    },
    {
        name: '删除字典',
        method: 'delDict',
        path: '/dict/del',
        type: 'post',
        sys: global.iam
    },
    {
        name: '所有字典类型',
        method: 'allDictType',
        path: '/dict/allType',
        type: 'post',
        sys: global.iam
    },
    {
        name: '字典详情',
        method: 'getDictByType',
        path: '/dict/getByType',
        type: 'post',
        sys: global.iam
    },
    {
        name: '获取字典缓存',
        method: 'dictCache',
        path: '/dict/getInit',
        type: 'get', // 由于初始页面时调用，用post会要求调用IAMCore.getXsrfToken等（此时index.html动态加载的IAM sdk还未异步加载完）
        sys: global.iam
    },
    //contact
    {
        name: '联系人列表',
        method: 'contactList',
        path: '/contact/list',
        type: 'post',
        sys: global.iam
    },
    {
        name: '保存',
        method: 'saveContact',
        path: '/contact/save',
        type: 'json',
        sys: global.iam
    },
    {
        name: '联系人详情',
        method: 'contactDetail',
        path: '/contact/detail',
        type: 'post',
        sys: global.iam
    },
    {
        name: '逻辑删除联系人',
        method: 'delContact',
        path: '/contact/del',
        type: 'post',
        sys: global.iam
    },
    //group
    {
        name: '联系人分组列表',
        method: 'contactGroupList',
        path: '/contactGroup/list',
        type: 'post',
        sys: global.iam
    },
    {
        name: '所有分组列表',
        method: 'groupList',
        path: '/contactGroup/groupList',
        type: 'post',
        sys: global.iam
    },
    {
        name: '保存联系人分组',
        method: 'saveContactGroup',
        path: '/contactGroup/save',
        type: 'post',
        sys: global.iam
    },
    {
        name: '删除联系人分组',
        method: 'delContactGroup',
        path: '/contactGroup/del',
        type: 'post',
        sys: global.iam
    },

    //area
    {
        name: 'getAreaTree',
        method: 'getAreaTree',
        path: '/area/getAreaTree',
        type: 'get',
        sys: global.iam
    },

]

export function getRoutes() {
    return request({
        url: '/menu/list',
        method: 'get',
        sys: global.iam,
        usedefault: false
    })
}
