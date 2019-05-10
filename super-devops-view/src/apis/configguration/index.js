/**
 * 用户模块
 * @type {Object}
 */
export default [
  {
    name: '修改版本',
    method: 'update',
    path: '/devops-scm/configGuration/update',
    type: 'post'
  },
  {
    name: '修改配置',
    method: 'updateguration',
    path: '/devops-scm/configGuration/updateGuration',
    type: 'post'
  },
  {
    name: '删除配置项',
    method: 'deleteVersiondetail',
    path: '/devops-scm/configGuration/deleteVersionDetail',
    type: 'post'
  },
  {
    name: '查询版本',
    method: 'select',
    path: '/devops-scm/configGuration/select',
    type: 'post'
  },
  {
    name: '添加版本',
    method: 'configset',
    path: '/devops-scm/configGuration/config-set.json',
    type: 'json'
  },
  {
    name: '获取版本列表',
    method: 'lists',
    path: '/devops-scm/configGuration/config-list.json',
    type: 'post'
  },
  {
    name: '获取分组名称',
    method: 'grouplists',
    path: '/devops-scm/appGroup/group_list',
    type: 'post'
  },
  {
    name: '获取版本详情',
    method: 'configselect',
    path: '/devops-scm/configGuration/config-select.json',
    type: 'post'
  },
  {
    name: '校验配置',
    method: 'configcheck',
    path: '/devops-scm/configGuration/config-check.json',
    type: 'post'
  },
  {
    name: '获取分组名称',
    method: 'getlog',
    path: '/devops-srm/console/consoleLog',
    type: 'json',
    async : false
  },
  {
    name: '获取分组名称',
    method: 'statistics',
    path: '/devops-srm/statistics/statisticsLog',
    type: 'json'
  }
]
