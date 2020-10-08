import global from "../../common/global_variable";
import ajax from '@/utils/ajax/ajax'

export default [
    //user
    {
        name: '用户列表',
        method: 'userList',
        path: '/user/list',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '保存用户',
        method: 'saveUser',
        path: '/user/save',
        type: 'json',
        sysModule: global.iam
    },
    {
        name: '用户详情',
        method: 'userDetail',
        path: '/user/detail',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '删除用户',
        method: 'delUser',
        path: '/user/del',
        type: 'post',
        sysModule: global.iam
    },
    //menu
    {
        name: 'menu树列表',
        method: 'getMenuTree',
        path: '/menu/tree',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '保存menu',
        method: 'saveMenu',
        path: '/menu/save',
        type: 'json',
        sysModule: global.iam
    },
    {
        name: '删除menu',
        method: 'delMenu',
        path: '/menu/del',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '获取用户menu列表',
        method: 'getMenuList',
        path: '/menu/list',
        type: 'post',
        sysModule: global.iam
    },
    //role
    {
        name: '角色列表',
        method: 'getRoles',
        path: '/role/getRolesByUserGroups',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '角色列表',
        method: 'roleList',
        path: '/role/list',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '保存角色',
        method: 'saveRole',
        path: '/role/save',
        type: 'json',
        sysModule: global.iam
    },
    {
        name: '删除角色',
        method: 'delRole',
        path: '/role/del',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '角色详情',
        method: 'roleDetail',
        path: '/role/detail',
        type: 'post',
        sysModule: global.iam
    },
    //group
    {
        name: '获取分组树',
        method: 'getGroupsTree',
        path: '/group/getGroupsTree',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '保存Group',
        method: 'saveGroup',
        path: '/group/save',
        type: 'json',
        sysModule: global.iam
    },
    {
        name: '删除Group',
        method: 'delGroup',
        path: '/group/del',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: 'Group详情',
        method: 'groupDetail',
        path: '/group/detail',
        type: 'post',
        sysModule: global.iam
    }, {
        name: 'getOrganizations',
        method: 'getOrganizations',
        path: '/group/getOrganizations',
        type: 'post',
        sysModule: global.iam
    },
    //online
    {
        name: 'onlineList',
        method: 'onlineList',
        path: '/mgr/v1/getSessions',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: 'getIamServer',
        method: 'getIamServer',
        path: '/mgr/v1/findIamServers',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: 'destroySessions',
        method: 'destroySessions',
        path: '/mgr/v1/destroySessions',
        type: 'post',
        sysModule: global.iam
    },
    // Cluster Config Informcation
    {
        name: 'appModules',
        method: 'clusterConfigInfo',
        path: '/clusterConfig/loadInit',
        type: 'get', // 必须使用get，原因同理：dict/loadInit
        sysModule: global.iam,
    },
    {
        name: 'applylocale',
        method: 'applylocale',
        path: '/login/applylocale',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: 'logout',
        method: 'logout',
        path: '/logout',
        type: 'post',
        sysModule: global.iam
    },
    //dict
    {
        name: '字典列表',
        method: 'dictList',
        path: '/dict/list',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '保存字典',
        method: 'saveDict',
        path: '/dict/save',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '字典详情',
        method: 'dictDetail',
        path: '/dict/detail',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '删除字典',
        method: 'delDict',
        path: '/dict/del',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '所有字典类型',
        method: 'allDictType',
        path: '/dict/allType',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '字典详情',
        method: 'getDictByType',
        path: '/dict/getByType',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '获取字典缓存',
        method: 'dictCache',
        path: '/dict/loadInit',
        type: 'get', // 必须是get，因为在初始期间调用，使用post会调用IAMCore.getXsrfToken等，此时index.html动态加载的IAM sdk还未异步加载完
        sysModule: global.iam
    },
    //contact
    {
        name: '联系人列表',
        method: 'contactList',
        path: '/contact/list',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '保存',
        method: 'saveContact',
        path: '/contact/save',
        type: 'json',
        sysModule: global.iam
    },
    {
        name: '联系人详情',
        method: 'contactDetail',
        path: '/contact/detail',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '逻辑删除联系人',
        method: 'delContact',
        path: '/contact/del',
        type: 'post',
        sysModule: global.iam
    },
    //group
    {
        name: '联系人分组列表',
        method: 'contactGroupList',
        path: '/contactGroup/list',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '所有分组列表',
        method: 'groupList',
        path: '/contactGroup/groupList',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '保存联系人分组',
        method: 'saveContactGroup',
        path: '/contactGroup/save',
        type: 'post',
        sysModule: global.iam
    },
    {
        name: '删除联系人分组',
        method: 'delContactGroup',
        path: '/contactGroup/del',
        type: 'post',
        sysModule: global.iam
    },
    //area
    {
        name: 'getAreaTree',
        method: 'getAreaTree',
        path: '/area/getAreaTree',
        type: 'get',
        sysModule: global.iam
    },
]

// [顺序优先特殊接口，直接走ajax]登录完成时，获取路由动态创建菜单
export function getRoutes() {
    return new Promise(function(resolve, reject) {
        ajax({
            type: 'get',
            path: '/menu/list',
            sysModule: global.iam,
            fn: data => {
                resolve(data);
            },
            errFn: (obj, error) => {
                reject(error);
            }
        });
    });
}
