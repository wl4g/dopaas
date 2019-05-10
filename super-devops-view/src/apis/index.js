/**
 * Created by sailengsi on 2017/5/11.
 */

/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 * @type {Object}
 */

import user from './user/'
import article from './article/'
import order from './order/'
import system from './system/'
import test from './test/'
import managemant from './managemant/'
import configguration from './configguration/'
import instanceman from './instanceman/'
import historic from './historic/'
import track from './track/'
export default [{
  module: 'user',
  name: '用户管理',
  list: user
}, {
  module: 'article',
  name: '文章管理',
  list: article
}, {
  module: 'order',
  name: '订单管理',
  list: order
}, {
  module: 'system',
  name: '系统设置',
  list: system
}, {
  module: 'test',
  name: '测试模块',
  list: test
}, {
  module: 'managemant',
  name: '节点',
  list: managemant
}, {
  module: 'configguration',
  name: '版本管理',
  list: configguration
},{
  module: 'instanceman',
  name: '版本管理',
  list: instanceman
},{
  module: 'historic',
  name: '版本管理',
  list: historic
},{
  module: 'track',
  name: '版本管理',
  list: track
}]
