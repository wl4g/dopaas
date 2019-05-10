/**
 * 用户模块
 * @type {Object}
 */
export default [
  {
    name: '节点增加',
    method: 'insert',
    path: '/devops-scm/appGroup/insert',
    type: 'json'
  },
  {
    name: '节点列表',
    method: 'list',
    path: '/devops-scm/appGroup/list',
    type: 'post'
  },
  {
    name: '查询节点',
    method: 'select',
    path: '/devops-scm/appGroup/select',
    type: 'post'
  },
  {
    name: '删除应用组',
    method: 'deleteenv',
    path: '/devops-scm/appGroup/delete_env',
    type: 'post'
  },
  {
    name: '删除节点',
    method: 'deleteInstance',
    path: '/devops-scm/appGroup/deleteInstance',
    type: 'post'
  },
  {
    name: '修改应用组',
    method: 'update',
    path: '/devops-scm/appGroup/update',
    type: 'post'
  },
  {
    name: '添加节点',
    method: 'insertInstance',
    path: '/devops-scm/appGroup/insertInstance',
    type: 'json'
  },
  {
    name: '修改节点',
    method: 'updateInstance',
    path: '/devops-scm/appGroup/updateInstance',
    type: 'post'
  },
  {
    name: '获取分组名称',
    method: 'grouplist',
    path: '/devops-scm/appGroup/group_list',
    type: 'post'
  },
  {
    name: '获取环境名称列表',
    method: 'envirlist',
    path: '/devops-scm/appGroup/envir_list',
    type: 'post'
  },
  {
    name: '获取实例名称列表',
    method: 'instancelist',
    path: '/devops-scm/appGroup/instance_list',
    type: 'json'
  }
]
